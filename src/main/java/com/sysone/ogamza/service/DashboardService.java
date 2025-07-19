package com.sysone.ogamza.service;

import com.sysone.ogamza.controller.DashboardController;
import com.sysone.ogamza.repository.DashboardDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardService {

    private static final DashboardService instance = new DashboardService();
    private static final DashboardDAO dashboardDao = DashboardDAO.getInstance();

    private DashboardService() {}


    public static DashboardService getInstance() {
        return instance;
    }

    /*
        출근 시간 조회
    */
    public String getTodayAccessTime (long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(LocalDateTime.now());
        return formatTime(accessTime);
    }

    /*
        퇴근 시간 조회
    */
    public String getTodayLeaveTime(long id) {
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(LocalDateTime.now());

        int today = LocalDateTime.now().getDayOfMonth();
        return (leaveTime.getDayOfMonth() == today) ? "- - : - - : - -" : formatTime(leaveTime);
    }

    /*
        근로 시간 및 잔여 근로 시간 조회
    */
    public String[] getWorkingTime(long id) {
        String[] timeArray = new String[3];
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(LocalDateTime.now());
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(LocalDateTime.now());

        int today = LocalDateTime.now().getDayOfMonth();
        LocalDateTime currentTime = (leaveTime.getDayOfMonth() == today) ? LocalDateTime.now() : leaveTime;

        Duration duration = Duration.between(accessTime, currentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        // timeArray[0] : 근로 시간, timeArray[1] : 잔여 근로 시간, timeArray[2] : 총 근로 시간(totalMinutes)
        timeArray[0] = (minutes % 60 == 0) ? (hours != 0) ? (hours - 1) + "시간" : "0시간" : (hours - 1) + "시간 " + (minutes % 60) + "분";
        timeArray[1] = (hours >= 9) ? "0시간 0분" : (minutes !=  0) ? (8 - hours) + "시간 " + (60 - ((minutes % 60))) + "분" : "8시간 0분";
        timeArray[2] = String.valueOf(minutes);

        return timeArray;
    }

    /*
        총연차 조회
    */
    public int getVacationDays(long id) {
        return dashboardDao.findVacationDaysByEmpId(id);
    }

    /*
        사용 연차 조회
    */
    public int getUsedVacationDays(long id) {
        return dashboardDao.findUsedVacationDaysByEmpId(id);
    }

    /*
        총 근무 시간 조회
    */
    public int getTotalWorkingHours(long id) {
        return dashboardDao.findAllWorkTimeByDateAndEmpId(id);
    }

    /*
        총 연장 근무 시간 조회 (하루 최대 3시간 기준)
    */
    public int getTotalExtendWorkingHours(long id) {
        return dashboardDao.findAllExtendWorkTimeByDateAndEmpId(id) * 3;
    }

    /*
        총 주말 근무 시간 조회 (하루 최대 8시간 기준)
    */
    public int getTotalWeekendWorkingHours(long id) {
        return dashboardDao.findAllWeekendWorkTimeByDateAndEmpId(id) * 8;
    }

    /*
        주 기준 일정 조회
    */
    public List<String> getWeekSchedules(long id) {
       return dashboardDao.findSchedulesByDateAndEmpId(id);
    }

    /*
        시간 포맷팅
    */
    private String formatTime(LocalDateTime time) {
        return String.format("%02d : %02d : %02d", time.getHour(), time.getMinute(), time.getSecond());
    }

    /*
        일정 리스트 생성 중 라벨에 속성 부여
    */
    public Label getLabel(String schedule) {
        Label item = new Label(schedule);
        item.setStyle(
                "-fx-pref-width:500;" +
                        "-fx-pref-height:50;" +
                        "-fx-background-color: #1E90FF;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 16px;"
        );
        item.setCursor(Cursor.HAND);

        item.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/fxml/ScheduleList.fxml"));
                Parent formRoot = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle("일정 조회");

                dialogStage.setScene(new Scene(formRoot));
                dialogStage.setResizable(false);
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return item;
    }
}
