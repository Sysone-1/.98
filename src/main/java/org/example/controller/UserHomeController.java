package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class UserHomeController {

    @FXML
    private Label welcomeLabel;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private ImageView logo;

    public void initialize() {
        // Optional: 초기값 설정
        welcomeLabel.setText("안녕하세요 :)");
        nameLabel.setText("김선호");
        roleLabel.setText("UX/UI Designer");
    }
    

}
