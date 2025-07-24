package com.sysone.ogamza.service.user;

import com.sysone.ogamza.controller.user.DashboardController;
import com.sysone.ogamza.controller.user.ScheduleContentController;
import com.sysone.ogamza.dto.user.DashboardScheduleDTO;
import com.sysone.ogamza.dao.user.DashboardDAO;
import com.sysone.ogamza.utils.dashboard.UsedVacationCalculator;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 대시보드 정보를 처리하는 서비스 클래스입니다.
 * <p>
 * 출퇴근 시간, 근로 시간, 연차 사용 현황, 일정 정보 등의 데이터를 조회 및 가공하여
 * UI에 필요한 데이터를 제공합니다.
 * 또한, 일정 상세 보기 모달 창 표시 등의 UI 로직 일부도 포함하고 있습니다.
 * </p>
 *
 * @author 김민호
 */
public class DashboardService {

    @Getter
    private static final DashboardService instance = new DashboardService();
    private static final DashboardDAO dashboardDao = DashboardDAO.getInstance();

    private DashboardService() {}

    /**
     * 해당 사용자의 오늘 출근 시간을 조회합니다.
     *
     * @param id 사용자 ID
     * @return 출근 시간 문자열 (HH : mm : ss)
     */
    public String getTodayAccessTime (long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByEmpId(id).orElse(LocalDateTime.now());
        return formatTime(accessTime);
    }

    /**
     * 해당 사용자의 오늘 퇴근 시간을 조회합니다.
     * 아직 퇴근 기록이 없으면 "- - : - - : - -" 반환.
     *
     * @param id 사용자 ID
     * @return 퇴근 시간 문자열 (HH : mm : ss 또는 대시)
     */
    public String getTodayLeaveTime(long id) {
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByEmpId(id).orElse(LocalDateTime.now());

        int today = LocalDateTime.now().getDayOfMonth();
        return (leaveTime.getDayOfMonth() == today) ? "- - : - - : - -" : formatTime(leaveTime);
    }

    /**
     * 해당 사용자의 오늘 근로 시간 및 잔여 근로 시간을 계산합니다.
     *
     * @param id 사용자 ID
     * @return [0]: 근로 시간, [1]: 잔여 시간, [2]: 총 분(minute) 문자열
     */
    public String[] getWorkingTime(long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByEmpId(id).orElse(LocalDateTime.now());
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByEmpId(id).orElse(LocalDateTime.now());

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
     * 해당 사용자의 총 연차 일수를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 총 연차 일수
     */
    public int getVacationDays(long id) {
        return dashboardDao.findVacationDaysByEmpId(id);
    }

    /**
     * 해당 사용자의 사용 연차(반차 포함) 일수를 계산합니다.
     *
     * @param id 사용자 ID
     * @return 사용 연차 일수 (double 형식, 예: 1.5)
     */
    public double getUsedVacationDays(long id) {
        return UsedVacationCalculator.compute(dashboardDao.findUsedVacationDaysByEmpId(id));
    }

    /**
     * 해당 사용자의 누적 근무 시간을 조회합니다.
     *
     * @param id 사용자 ID
     * @return 총 근무 시간(시간 단위)
     */
    public int getTotalWorkingHours(long id) {
        return dashboardDao.findAllWorkTimeByEmpId(id);
    }

    /**
     * 해당 사용자의 누적 연장 근무 시간을 조회합니다.
     * 하루 최대 3시간 기준으로 계산합니다.
     *
     * @param id 사용자 ID
     * @return 연장 근무 시간 (시간 단위)
     */
    public int getTotalExtendWorkingHours(long id) {
        return dashboardDao.findAllExtendWorkTimeByEmpId(id) * 3;
    }

    /**
     * 해당 사용자의 누적 주말 근무 시간을 조회합니다.
     * 하루 최대 8시간 기준으로 계산합니다.
     *
     * @param id 사용자 ID
     * @return 주말 근무 시간 (시간 단위)
     */
    public int getTotalWeekendWorkingHours(long id) {
        return dashboardDao.findAllWeekendWorkTimeByEmpId(id) * 8;
    }

    /**
     * 주간 일정 중 승인 완료된 일정들을 문자열 리스트로 반환합니다.
     * 형식: "MM월 dd일 일정명"
     *
     * @param id 사용자 ID
     * @return 주간 일정 문자열 리스트
     */
    public List<String> getWeekSchedules(long id) {
        List<DashboardScheduleDTO> resultList = dashboardDao.findSchedulesByEmpId(id);
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
     * LocalDateTime 객체를 시간 문자열로 포맷합니다.
     *
     * @param time 포맷할 시간
     * @return HH : mm : ss 형식의 문자열
     */
    private String formatTime(LocalDateTime time) {
        return String.format("%02d : %02d : %02d", time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
     * 일정 텍스트를 담은 라벨(Label)을 생성하고 클릭 시 일정 상세 모달을 띄웁니다.
     *
     * @param schedule 일정 텍스트
     * @param index    해당 일정의 인덱스
     * @return 스타일이 적용된 JavaFX Label 객체
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
     * 일정 상세 내용을 확인할 수 있는 모달 창을 표시합니다.
     *
     * @param index 라벨에 매핑된 일정 인덱스
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
