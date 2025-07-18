package com.sysone.ogamza.controller;

import com.sysone.ogamza.service.DashboardService;
import com.sysone.ogamza.view.ArcProgress;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class DashboardController {
    @FXML private Text accessTime;
    @FXML private Text leaveTime;
    @FXML private Text totalVacation;
    @FXML private Text usedVacation;
    @FXML private Text remainingVacation;
    @FXML private Text workingHours;
    @FXML private Text remainingWorkingHours;
    @FXML private Text totalWorkingHours;
    @FXML private Text totalRemainingWorkingHours;
    @FXML private Text extendWorkingTime;
    @FXML private Text weekendWorkingTime;
    @FXML private ProgressBar workingProgressBar;
    @FXML private ProgressBar vacationProgressBar;
    @FXML private VBox scheduleListBox;
    @FXML private Text noScheduleText;
    @FXML private Text todayMonth;
    @FXML private Text todayWeek;

    private static final DashboardService dashboardService = DashboardService.getInstance();

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
        Long empId = 1001L;
        accessTime.setText(dashboardService.getTodayAccessTime(empId));
    }
    /*
        퇴근 시간 조회 및 setText
    */
    private void loadLeaveTime() {
        Long empId = 1001L;
        String time = dashboardService.getTodayLeaveTime(empId);
        leaveTime.setText(time);
    }

    /*
        근로시간 및 잔여근로시간 조회
    */
    private void loadWorkingHours() {
        long empId = 1001L;
        String[] timeArray = dashboardService.getWorkingTime(empId);

        ArcProgress.percent =((double) Integer.parseInt(timeArray[2]) / 540) * 100;
        workingHours.setText(timeArray[0]);
        remainingWorkingHours.setText(timeArray[1]);
    }

    /*
        총연차, 사용 연차, 남은 연차 조회
    */
    private void loadVacationDays() {
        long empId = 1001L;
        int vacationDays = dashboardService.getVacationDays(empId);
        int usedVactaionDays = dashboardService.getUsedVacationDays(empId);
        totalVacation.setText(vacationDays + "일");
        usedVacation.setText(usedVactaionDays + "일");
        remainingVacation.setText((vacationDays - usedVactaionDays) + "일");
        vacationProgressBar.setProgress((double) usedVactaionDays / vacationDays);
    }

    /*
        총 근무시간, 남은 근무시간, 남은 연장근무시간, 남은 휴일연장근무시간 조회
    */
    private void loadTotalWorkingHours() {
        long empId = 1001L;

        LocalDateTime today = LocalDateTime.now();
        int month = today.getMonthValue();
        int weekOfMonth = today.get(WeekFields.of(Locale.KOREA).weekOfMonth());

        todayMonth.setText("0" + String.valueOf(month) + "월");
        todayWeek.setText(String.valueOf(weekOfMonth) + "주차");

        // 지금까지 평일에만한 근로시간
        int totalWorkingTime = dashboardService.getTotalWorkingHours(empId);

        // 연장근무 횟수
        int totalExtendWorkingTime = dashboardService.getTotalExtendWorkingHours(empId);

        // 휴일 근무 횟수
        int totalWeekendWorkingTime = dashboardService.getTotalWeekendWorkingHours(empId);

        totalWorkingTime = totalWorkingTime + totalExtendWorkingTime * 180 + totalWeekendWorkingTime * 480;

        extendWorkingTime.setText((12 - totalExtendWorkingTime)  + "시간 ");
        weekendWorkingTime.setText((16 - totalWeekendWorkingTime) + "시간 ");

        if (totalWorkingTime % 60 != 0) {
            totalWorkingHours.setText((totalWorkingTime / 60) + "시간 " + (totalWorkingTime % 60) + "분");
            totalRemainingWorkingHours.setText((68 - (totalWorkingTime / 60) - 1) + "시간 " + (60 - totalWorkingTime % 60) + "분");
        } else {
            totalWorkingHours.setText((totalWorkingTime / 60) + "시간");
            totalRemainingWorkingHours.setText((68 - (totalWorkingTime / 60)) + "시간");
        }
        workingProgressBar.setProgress(((double)(totalWorkingTime / 60) + (double)(totalWorkingTime % 60) / 60) / 68);
    }

    /*
        일정 등록 리스트 조회
    */
    private void loadTodayScheduleList() {
        long empId = 1001L;
        List<String> scheduleList = dashboardService.getWeekSchedules(empId);

        scheduleListBox.getChildren().clear();

        if (scheduleList.isEmpty()) {
            scheduleListBox.getChildren().add(noScheduleText);
        } else {
            for (String schedule : scheduleList) {
                Label item = new Label(schedule);
                item.setStyle("-fx-pref-width:500; -fx-pref-height:50; -fx-background-color: #1E90FF; -fx-text-fill: white; -fx-padding: 10 20 10 20; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 16px;" );
                scheduleListBox.getChildren().add(item);
            }
        }
    }
}
