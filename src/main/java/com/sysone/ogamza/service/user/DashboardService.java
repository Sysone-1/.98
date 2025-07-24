package com.sysone.ogamza.service.user;

import com.sysone.ogamza.controller.user.DashboardController;
import com.sysone.ogamza.controller.user.ScheduleContentController;
import com.sysone.ogamza.dto.user.DashboardScheduleDTO;
import com.sysone.ogamza.dao.user.DashboardDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardService {

    @Getter
    private static final DashboardService instance = new DashboardService();
    private static final DashboardDAO dashboardDao = DashboardDAO.getInstance();

    private DashboardService() {}

    /**
        출근 시간 조회
    */
    public String getTodayAccessTime (long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(LocalDateTime.now());
        return formatTime(accessTime);
    }

    /**
        퇴근 시간 조회
    */
    public String getTodayLeaveTime(long id) {
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(LocalDateTime.now());

        int today = LocalDateTime.now().getDayOfMonth();
        return (leaveTime.getDayOfMonth() == today) ? "- - : - - : - -" : formatTime(leaveTime);
    }

    /**
        근로 시간 및 잔여 근로 시간 조회
    */
    public String[] getWorkingTime(long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(LocalDateTime.now());
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(LocalDateTime.now());

        int today = LocalDateTime.now().getDayOfMonth();
        LocalDateTime currentTime = (leaveTime.getDayOfMonth() == today) ? LocalDateTime.now() : leaveTime;

        Duration duration = Duration.between(accessTime, currentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        System.out.println(minutes);

        String worked = (minutes % 60 == 0) ? (hours != 0) ? (hours) + "시간" : "0시간" : (hours != 0) ? (hours) + "시간 " + (minutes % 60) + "분" : "0시간 " + (minutes % 60) + "분";
        String remaining = (hours >= 9) ? "0시간 0분" : (minutes !=  0) ? (hours != 0) ? (8 - hours) + "시간 " + (60 - ((minutes % 60))) + "분" : "7시간 " + (60 - ((minutes % 60))) + "분" : "8시간 0분";

        return new String[]{worked, remaining, String.valueOf(minutes)};
    }

    /**
        총연차 조회
    */
    public int getVacationDays(long id) {
        return dashboardDao.findVacationDaysByEmpId(id);
    }

    /**
        사용 반차, 연차 조회 (공휴일 count 제외는 추후에 공공데이터API - 한국천문연구원_특일 정보 활용)
    */
    public double getUsedVacationDays(long id) {
        double vacationDays = 0;
        List<HashMap<String, String>> result = dashboardDao.findUsedVacationDaysByEmpId(id);

        for (HashMap<String, String> hm : result) {
            if ("연차".equals(hm.get("type"))) {
                String[] dates = hm.get("duration").split(",");

                LocalDateTime start = LocalDateTime.parse(dates[0]);
                LocalDateTime end = LocalDateTime.parse(dates[1]);

                while (!start.isAfter(end)) {
                    DayOfWeek dayOfWeek = start.getDayOfWeek();
                    if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                        vacationDays += 1;
                    }
                    start = start.plusDays(1);
                }

            } else if ("반차".equals(hm.get("type"))) {
                vacationDays += 0.5;
            }
        }
        return vacationDays;
    }

    /**
        총 근무 시간 조회
    */
    public int getTotalWorkingHours(long id) {
        return dashboardDao.findAllWorkTimeByDateAndEmpId(id);
    }

    /**
        총 연장 근무 시간 조회 (하루 최대 3시간 기준)
    */
    public int getTotalExtendWorkingHours(long id) {
        return dashboardDao.findAllExtendWorkTimeByDateAndEmpId(id) * 3;
    }

    /**
        총 주말 근무 시간 조회 (하루 최대 8시간 기준)
    */
    public int getTotalWeekendWorkingHours(long id) {
        return dashboardDao.findAllWeekendWorkTimeByDateAndEmpId(id) * 8;
    }

    /**
        주 기준 일정 조회
    */
    public List<String> getWeekSchedules(long id) {
        List<DashboardScheduleDTO> resultList = dashboardDao.findSchedulesByDateAndEmpId(id);
        List<String> scheduleList = new ArrayList<>();

        for (DashboardScheduleDTO dto : resultList) {
            if (dto.getIsGranted() == 1) {
                scheduleList.add(String.format("%02d월 %02d일 %s",
                        dto.getStartDate().getMonthValue(),
                        dto.getStartDate().getDayOfMonth(),
                        dto.getTitle()));
            }
        }
        return scheduleList;
    }

    /**
        시간 포맷팅
    */
    private String formatTime(LocalDateTime time) {
        return String.format("%02d : %02d : %02d", time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
        일정 리스트 생성 중 라벨에 속성 부여
    */
    public Label getLabel(String schedule, int index) {
        Label item = new Label(schedule);
        item.setStyle(
                "-fx-pref-width:500;" +
                "-fx-pref-height:50;" +
                "-fx-background-color: #F3F1F1;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 16px;" +
                "-fx-font-family: 'Inter'"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setRadius(5);

        item.setEffect(shadow);

        item.setUserData(index);
        item.setCursor(Cursor.HAND);

        item.setOnMouseClicked(event -> openScheduleDetailModal(index));

        return item;
    }

    /**
        일정 상세 모달 창 띄우기
     */
    private void openScheduleDetailModal(int index) {
        try {
            FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/fxml/user/ScheduleContent.fxml"));
            Parent root = loader.load();

            ScheduleContentController controller = loader.getController();
            controller.setScheduleIndex(index);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("일정 상세 조회");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
