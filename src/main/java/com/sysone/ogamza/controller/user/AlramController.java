package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dao.user.AlarmDAO;
import com.sysone.ogamza.dto.user.AlarmSettingDTO;
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

    private final LocalTime offTime = LocalTime.of(18, 0);
    private ScheduledExecutorService scheduler;

    private Stage modalStage;
    private final AlarmDAO alarmDAO = new AlarmDAO();

    //변경 설정 전 설정 상태 저장용
    private boolean prevToggleSwitchState;
    private String prevSelectedTime;

    @FXML
    public void initialize() {
        int id = Session.getInstance().getLoginUser().getId();
        AlarmSettingDTO dto = alarmDAO.findByUserId(id);

        // DB에 값이 있으면 설정 적용
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
        //초기화 시 ToggleSwitch 상태에 따라 ComboBox 활성화 여부
        toggleSwitch.selectedProperty().addListener((observable, oldvalue, newvalue) -> {
            //Switch가 꺼지면 콤보박스 비활성화
            timeComboBox.setDisable(!newvalue); //Switch가 켜지면 콤보박스 활성화
        });

        prevToggleSwitchState = toggleSwitch.isSelected();
        prevSelectedTime = timeComboBox.getSelectionModel().getSelectedItem();
    }

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

            alarmDAO.saveOrUpdate(userId, minutes);
            scheduleNotification(minutes);
        } else {
            alarmDAO.saveOrUpdate(userId, 0); // 모두 OFF
        }

        prevToggleSwitchState = toggleSwitchState;
        prevSelectedTime = selectedTime;

        if (modalStage != null) modalStage.close();
    }

    private boolean isSettingChanged(boolean currentSwitch, String currentTime) {
        return prevToggleSwitchState != currentSwitch || !prevSelectedTime.equals(currentTime);
    }

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

    private void showNotification(long minutes) {
        Platform.runLater(() -> {
            String message = minutes + "분 후 퇴근 예정입니다. 오늘 하루도 수고하셨습니다!";
            Notifications.create().title("퇴근 알림").text(message).showInformation();
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