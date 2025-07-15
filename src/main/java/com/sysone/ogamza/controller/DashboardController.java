package com.sysone.ogamza.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML
    private Label welcomeLabel;

    public void initialize() {
        welcomeLabel.setText("Dashboard 입니다!");
    }

}
