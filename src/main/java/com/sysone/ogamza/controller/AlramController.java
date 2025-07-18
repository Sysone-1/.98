package com.sysone.ogamza.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.ToggleSwitch;

public class AlramController {
    @FXML
    private ToggleSwitch toggleSwitch;
    @FXML
    private ComboBox<String> timeComboBox;
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
    }
}
