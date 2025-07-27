package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.AlarmSettingDTO;
import com.sysone.ogamza.service.user.AlarmService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ===========================================
 * 퇴근 알림 설정 컨트롤러 (AlramController)
 * ===========================================
 * - 사용자 설정을 통해 퇴근 알림을 등록 및 해제할 수 있음
 * - ToggleSwitch: 알림 허용 여부
 * - ComboBox: 퇴근 몇 분 전에 알림을 받을지 설정 (3분, 5분, 10분)
 * - 설정 저장 시 AlarmService 통해 DB 저장 및 알림 스케줄링
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class AlramController implements ModalControllable {

    @FXML private ToggleSwitch toggleSwitch;         // 알림 허용 여부 토글
    @FXML private ComboBox<String> timeComboBox;     // 알림 시간 설정 콤보박스

    private final LocalTime offTime = LocalTime.of(18, 0); // 퇴근 시간
    private ScheduledExecutorService scheduler;           // 알림 스케줄링용 쓰레드풀

    private Stage modalStage;                         // 모달 창 Stage (닫기용)
    private final AlarmService alarmService = new AlarmService(); // 알림 서비스 객체

    // 설정 변경 비교용 변수
    private boolean prevToggleSwitchState;
    private String prevSelectedTime;

    /**
     * ▶ FXML 초기화 시 실행
     * - DB에서 알림 설정 조회
     * - Toggle 및 ComboBox UI 구성
     */
    @FXML
    public void initialize() {
        int id = Session.getInstance().getLoginUser().getId();
        AlarmSettingDTO dto = alarmService.getAlarmSetting(id);

        // DB에 설정이 있으면 UI에 반영
        if (dto != null && (dto.getAlarm1() == 1 || dto.getAlarm2() == 1 || dto.getAlarm3() == 1)) {
            toggleSwitch.setSelected(true);
            if (dto.getAlarm1() == 1) timeComboBox.setValue("3분전");
            else if (dto.getAlarm2() == 1) timeComboBox.setValue("5분전");
            else if (dto.getAlarm3() == 1) timeComboBox.setValue("10분전");
        } else {
            toggleSwitch.setSelected(false);
            timeComboBox.setDisable(true);
            timeComboBox.getSelectionModel().select("3분전");
        }

        // 토글 상태에 따라 콤보박스 활성/비활성
        toggleSwitch.selectedProperty().addListener((observable, oldvalue, newvalue) -> {
            timeComboBox.setDisable(!newvalue);
        });

        // 초기 상태 저장
        prevToggleSwitchState = toggleSwitch.isSelected();
        prevSelectedTime = timeComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * ▶ 저장 버튼 클릭 시 실행되는 메서드
     * - 설정 변경 여부 확인 후 Service 호출
     */
    @FXML
    public void onSave(ActionEvent actionEvent) {
        boolean toggleSwitchState = toggleSwitch.isSelected();
        String selectedTime = timeComboBox.getSelectionModel().getSelectedItem();

        if (!isSettingChanged(toggleSwitchState, selectedTime)) return;

        int userId = Session.getInstance().getLoginUser().getId();

        if (toggleSwitchState && selectedTime != null) {
            int minutes = switch (selectedTime) {
                case "3분전" -> 3;
                case "5분전" -> 5;
                case "10분전" -> 10;
                default -> 3;
            };

            alarmService.saveOrUpdate(userId, minutes);
            scheduleNotification(minutes);
        } else {
            alarmService.saveOrUpdate(userId, 0); // OFF 상태로 저장
        }

        prevToggleSwitchState = toggleSwitchState;
        prevSelectedTime = selectedTime;

        if (modalStage != null) modalStage.close();
    }

    /**
     * ▶ 설정 변경 여부 확인
     */
    private boolean isSettingChanged(boolean currentSwitch, String currentTime) {
        return prevToggleSwitchState != currentSwitch || !prevSelectedTime.equals(currentTime);
    }

    /**
     * ▶ 지정된 분 전으로 퇴근 알림 예약
     */
    private void scheduleNotification(long minutes) {
        LocalTime notifyAt = offTime.minusMinutes(minutes);
        long delay = ChronoUnit.MILLIS.between(LocalTime.now(), notifyAt);
        if (delay < 0) return;

        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            showNotification(minutes);
            scheduler.shutdown();
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * ▶ 실제 알림 팝업 표시
     */
    private void showNotification(long minutes) {
        Platform.runLater(() -> {
            String message = minutes + "분 후 퇴근 예정입니다. 오늘 하루도 수고하셨습니다!";
            Notifications.create()
                    .title("퇴근 알림")
                    .text(message)
                    .showInformation();
        });
    }

    @Override
    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    @Override
    public Stage getModalStage() {
        return modalStage;
    }
}
