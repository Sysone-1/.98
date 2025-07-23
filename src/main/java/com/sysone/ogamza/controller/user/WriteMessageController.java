package com.sysone.ogamza.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WriteMessageController {

    @FXML
    private Button cancelButton;


    @FXML
    private void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
