package com.sysone.ogamza.controller.user;

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

public class AlramController implements ModalControllable {
    @FXML
    private ToggleSwitch toggleSwitch;
    @FXML
    private ComboBox<String> timeComboBox;

    private Stage modalStage;

    private final LocalTime offTime = LocalTime.of(18, 0);

    //수정
    private boolean toggleSwitchState;
    private String selectedTime;

    private ScheduledExecutorService scheduler;

    //변경 설정 전 설정 상태 저장용
    private boolean prevToggleSwitchState;
    private String prevSelectedTime;

    @FXML
    public void initialize() {
        //초기화 시 ToggleSwitch 상태에 따라 ComboBox 활성화 여부
        toggleSwitch.selectedProperty().addListener((observable, oldvalue, newvalue) -> {
            if (newvalue) {
                timeComboBox.setDisable(false); //Switch가 켜지면 콤보박스 활성화
            } else {
                timeComboBox.setDisable(true); //Switch가 꺼지면 콤보박스 비활성화
            }
        });

        //초기 상태에 따른 ComboBox 설정 (ToggleSwitch까 꺼져 있으면 ComboBox도 비활성화)
        if (!toggleSwitch.isSelected()) {
            timeComboBox.setDisable(true);
        }

        //비활성화 상태일 떄도 기본값으로 "3분전"을 설정
        timeComboBox.getSelectionModel().select("3분전");
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        //알림 설정 저장
        toggleSwitchState = toggleSwitch.isSelected();
        //선택된 알람 시간
        selectedTime = timeComboBox.getSelectionModel().getSelectedItem();

        if(isSettingChanged()) {
            //토글이 켜져 있을 떄만 알림 트리거
            if (toggleSwitchState && selectedTime != null) {
                //알림 시간 계산 후 알림 트리거
                long minuteBeforeOffTime = getMinutesBeforeOffTime(selectedTime);
                scheduleNotification(minuteBeforeOffTime);
            }
            //변경된 값을 이전 값으로 저장
            prevToggleSwitchState = toggleSwitchState;
            prevSelectedTime = selectedTime;
        }

        //모달 창 닫기
        if (modalStage != null) {
            modalStage.close();
        }
    }

    private boolean isSettingChanged() {
        return prevToggleSwitchState != toggleSwitchState || !selectedTime.equals(prevSelectedTime);
    }

    private long getMinutesBeforeOffTime(String selectedTime) {
        switch (selectedTime) {
            case "3분전":
                return 3;
            case "5분전":
                return 5;
            case "10분전":
                return 10;
            default:
                return 3;
        }
    }

    private void scheduleNotification(long minuteBeforeOffTime) {
        LocalTime notificationTime = offTime.minus(minuteBeforeOffTime, ChronoUnit.MINUTES);

        //알람을 일정 시간 후에 트리거하기 위해 ScheduledExcutorService 사용
        scheduleAlarm(notificationTime);
    }

    // 알림을 일정 시간 후에 트리거하도록 예약하는 함수
    private void scheduleAlarm(LocalTime notificationTime) {
        long delay = calculateDelay(notificationTime); // 알림을 트리거할 시간까지의 차이 계산
        if (delay < 0) {
            System.out.println("알림 시간이 지남 - 알림 예약 취소");
            return;
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // 알림 트리거 (여기서는 예시로 출력)
            showNotification();
            // 알림 완료 후 스케줄러 종료 (자원 해제)
            scheduler.shutdown();
        }, delay, TimeUnit.MILLISECONDS);

    }

    private long calculateDelay(LocalTime notificationTime) {
        LocalTime now = LocalTime.now();
        long delay = ChronoUnit.MILLIS.between(now, notificationTime);
        return delay; // 음수도 그대로 반환
    }

    // 알림을 트리거하는 함수
    private void showNotification() {
        // JavaFX에서 UI 업데이트를 안전하게 하기 위해 Platform.runLater() 사용
        Platform.runLater(() -> {
            String message = String.format("%s 퇴근 예정입니다. 오늘 하루도 수고하셨습니다!",selectedTime);
            // ControlsFX 토스트 알림 생성
            Notifications.create()
                    .title("퇴근 알림")
                    .text(message)
                    .showInformation();  // 정보형 알림
        });
    }


    //추가
    public String getSelectedTime() {
        return timeComboBox.getSelectionModel().getSelectedItem();
    }

    //추가
    public boolean getToggleSwitchState() {
        return toggleSwitch.isSelected();
    }

    //추가
    public void setSavedSetting(boolean toggleSwitchState, String selectedTime) {
        this.toggleSwitchState = toggleSwitchState;
        this.selectedTime = selectedTime;

        this.prevToggleSwitchState = toggleSwitchState;
        this.prevSelectedTime = selectedTime;

    }

    //추가
    public void restoreSavedSetting() {
        Platform.runLater(() -> {
            toggleSwitch.setSelected(toggleSwitchState);
            timeComboBox.setValue(selectedTime);
        });
    }

    // ModalControllable 인터페이스 구현
    @Override
    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    @Override
    public Stage getModalStage() {
        return modalStage;
    }
}