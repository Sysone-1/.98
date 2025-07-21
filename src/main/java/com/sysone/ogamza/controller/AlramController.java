package com.sysone.ogamza.controller;

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

public class AlramController {
    @FXML
    private ToggleSwitch toggleSwitch;
    @FXML
    private ComboBox<String> timeComboBox;

    private Stage modalStage;

    private final LocalTime offTime = LocalTime.of(18,0);

    //수정
    private boolean toggleSwitchState;
    private String selectedTime;

    @FXML
    public void initialize() {
        //초기화 시 ToggleSwitch 상태에 따라 ComboBox 활성화 여부
        toggleSwitch.selectedProperty().addListener((observable,oldvalue,newvalue) -> {
            if (newvalue) {
                timeComboBox.setDisable(false); //Switch가 켜지면 콤보박스 활성화
            } else {
                timeComboBox.setDisable(true); //Switch가 꺼지면 콤보박스 비활성화
            }
        });

        //초기 상태에 따른 ComboBox 설정 (ToggleSwitch까 꺼져 있으면 ComboBox도 비활성화)
        if(!toggleSwitch.isSelected()) {
            timeComboBox.setDisable(true);
        }

        //비활성화 상태일 떄도 기본값으로 "3분전"을 설정
        timeComboBox.getSelectionModel().select("3분전");
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {

    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        //알림 설정 저장
        toggleSwitchState = toggleSwitch.isSelected();
        //선택된 알람 시간
        selectedTime = timeComboBox.getSelectionModel().getSelectedItem();
        //알림 시간 계산 후 알림 트리거
        long minuteBeforeOffTime = getMinutesBeforeOffTime(selectedTime);
        scheduleNotification(minuteBeforeOffTime);
        //모달 창 닫기
        if (modalStage != null) {
            modalStage.close();
        }
    }
    private long getMinutesBeforeOffTime(String selectedTime) {
        switch(selectedTime) {
            case "3분전":
                return 3;
            case "5분전":
                return 5;
            case "10분전":
                return 10;
            default:
                return 0;
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
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // 알림 트리거 (여기서는 예시로 출력)
            showNotification();
        }, delay, TimeUnit.MILLISECONDS);

    }

    private long calculateDelay(LocalTime notificationTime) {
        LocalTime now = LocalTime.now();
        long delay = ChronoUnit.MILLIS.between(now, notificationTime);
        return delay < 0 ? 0 : delay;  // 알림 시간이 이미 지났다면 0으로 설정
    }

    // 알림을 트리거하는 함수
    private void showNotification() {
        // JavaFX에서 UI 업데이트를 안전하게 하기 위해 Platform.runLater() 사용
        Platform.runLater(() -> {
            // ControlsFX 토스트 알림 생성
            Notifications.create()
                    .title("퇴근 알림")
                    .text("오늘 하루도 수고하셨어요 !")
                    .showInformation();  // 정보형 알림
        });
    }

    // Stage를 저장하는 메서드
    public void setModalStage(Stage stage) {
        this.modalStage = stage;
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

    }
    //추가
    public void restoreSavedSetting() {
        Platform.runLater(() -> {
            toggleSwitch.setSelected(toggleSwitchState);
            timeComboBox.setValue(selectedTime);
        });
    }
}
