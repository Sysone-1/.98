package com.sysone.ogamza.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class UserHomeController {

    @FXML
    private Label welcomeLabel;

    public void initialize() {
        welcomeLabel.setText("OnTime 프로젝트에 오신 걸 환영합니다!");
    }

}
