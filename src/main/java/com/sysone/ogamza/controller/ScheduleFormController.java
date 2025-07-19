package com.sysone.ogamza.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ScheduleFormController {

    @FXML private TextField titleField;
    @FXML private TextField typeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker  endDatePicker;
    @FXML private TextField contentField;

    /*
        등록 버튼 핸들러
    */
    @FXML
    private void handleSubmit(ActionEvent event) {
        String title = titleField.getText();
        String type = typeField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String content = contentField.getText();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /*
        삭제 버튼 핸들러
    */
    @FXML
    private void handleRemove(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /*
        취소 버튼 핸들러
    */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

