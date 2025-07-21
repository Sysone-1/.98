package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ScheduleRegisterController {

    @FXML private TextField titleField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker  endDatePicker;
    @FXML private TextField contentField;
    @FXML private ComboBox<String> typeComboBox;

    private static final ScheduleService scheduleService = ScheduleService.getInstance();


    /**
        ComboBox 초기화
     */
    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("연장 근무", "휴일", "연차", "반차", "외근");
        typeComboBox.setValue("종류를 선택하세요.");
    }

    /**
        상신 버튼 핸들러
    */
    @FXML
    private void handleSubmit(ActionEvent event) {
        ScheduleContentDTO scheduleListDto = new ScheduleContentDTO(
                DashboardController.empId,
                titleField.getText(),
                typeComboBox.getValue(),
                startDatePicker.getValue().atStartOfDay(),
                endDatePicker.getValue().atStartOfDay(),
                contentField.getText(),
                0
        );

        boolean success = scheduleService.createSchedule(scheduleListDto);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
        닫기 버튼 핸들러
    */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

