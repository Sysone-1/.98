package com.sysone.ogamza.controller;

import com.sysone.ogamza.service.DashboardService;
import com.sysone.ogamza.view.ArcProgress;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class DashboardController {
    @FXML private Text accessTime, leaveTime, workingHours, remainingWorkingHours, totalVacation, usedVacation, remainingVacation;
    @FXML private Text totalWorkingHours, totalRemainingWorkingHours, extendWorkingTime, weekendWorkingTime;
    @FXML private ProgressBar workingProgressBar, vacationProgressBar;
    @FXML private Text todayMonth, todayWeek;
    @FXML private VBox scheduleListBox;
    @FXML private Text noScheduleText;

    private static final DashboardService dashboardService = DashboardService.getInstance();
    private static final long empId = 1001L;

    @FXML
    public void initialize() {
        loadAccessTime();
        loadLeaveTime();
        loadWorkingHours();
        loadVacationDays();
        loadTotalWorkingHours();
        loadTodayScheduleList();
    }

    /*
        출근 시간 조회 및 setText
    */
    private void loadAccessTime() {
        accessTime.setText(dashboardService.getTodayAccessTime(empId));
    }

    /*
        퇴근 시간 조회 및 setText
    */
    private void loadLeaveTime() {
        String time = dashboardService.getTodayLeaveTime(empId);
        leaveTime.setText(time);
    }

    /*
        근로 시간 및 잔여 근로 시간 조회
    */
    private void loadWorkingHours() {
        String[] timeArray = dashboardService.getWorkingTime(empId);
        int totalMinutes = Integer.parseInt(timeArray[2]);

        ArcProgress.percent =((double) totalMinutes / 540) * 100;
        workingHours.setText(timeArray[0]);
        remainingWorkingHours.setText(timeArray[1]);
    }

    /*
        총연차, 사용 연차, 남은 연차 조회
    */
    private void loadVacationDays() {
        int total = dashboardService.getVacationDays(empId);
        int used = dashboardService.getUsedVacationDays(empId);
        int remaining = total - used;

        totalVacation.setText(total + "일");
        usedVacation.setText(used + "일");
        remainingVacation.setText(remaining + "일");
        vacationProgressBar.setProgress((double) used / total);
    }

    /*
        총 근무 시간, 남은 근무 시간, 남은 연장 근무 시간, 남은 휴일 연장 근무 시간 조회
    */
    private void loadTotalWorkingHours() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int week = now.get(WeekFields.of(Locale.KOREA).weekOfMonth());

        todayMonth.setText(String.format("%02d월", month));
        todayWeek.setText(week + "주차");

        // 해당 월 평일 근로 시간
        int base = dashboardService.getTotalWorkingHours(empId);

        // 해당 월 연장 근무 횟수
        int extend = dashboardService.getTotalExtendWorkingHours(empId);

        // 해당 월 휴일 근무 횟수
        int weekend = dashboardService.getTotalWeekendWorkingHours(empId);

        int totalMinutes = base + extend * 60 + weekend * 60;

        int totalHours = totalMinutes / 60;
        int remainHours = 68 - totalHours;
        int remainMinutes = totalMinutes % 60;

        if (remainMinutes > 0) {
            totalWorkingHours.setText(totalHours + "시간 " + remainMinutes + "분");
            totalRemainingWorkingHours.setText((remainHours - 1) + "시간 " + (60 - remainMinutes) + "분");
        } else {
            totalWorkingHours.setText(totalHours + "시간");
            totalRemainingWorkingHours.setText(remainHours + "시간");
        }

        workingProgressBar.setProgress((double) totalMinutes / (68 * 60));
        extendWorkingTime.setText((12 - dashboardService.getTotalExtendWorkingHours(empId)) + "시간 ");
        weekendWorkingTime.setText((16 - dashboardService.getTotalWeekendWorkingHours(empId)) + "시간 ");
    }

    /*
        일정 등록 리스트 조회
    */
    private void loadTodayScheduleList() {
        scheduleListBox.getChildren().clear();

        List<String> scheduleList = dashboardService.getWeekSchedules(empId);
        scheduleListBox.setAlignment(scheduleList.isEmpty() ? Pos.CENTER : Pos.TOP_CENTER);

        if (scheduleList.isEmpty()) {
            scheduleListBox.getChildren().add(noScheduleText);
        } else {
            for (String schedule : scheduleList) {
                Label item = getLabel(schedule);
                scheduleListBox.getChildren().add(item);
            }
        }
    }

    /*
        라벨에 속성 부여
    */
    private static Label getLabel(String schedule) {
        Label item = new Label(schedule);
        item.setStyle("-fx-pref-width:500; -fx-pref-height:50; -fx-background-color: #1E90FF; -fx-text-fill: white; -fx-padding: 10 20 10 20; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 16px;");
        return item;
    }
}
