package com.sysone.ogamza.controller;

import com.sysone.ogamza.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    private final LoginService loginService = new LoginService();
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    public Label errorLabel;
    @FXML
    private void handleLogin(ActionEvent event) throws SQLException, IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        //입력값이 비었는지 확인
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("이메일과 비밀번호를 모두 입력해주세요.");
            errorLabel.setVisible(true);
            return; //로그인 시도 중단
        }

        if(loginService.login(email,password)) {
        errorLabel.setVisible(false);
        // 로그인 성공 시 MainLayout.fxml 로딩
        Parent mainRoot = FXMLLoader.load(getClass().getResource("/fxml/MainLayout.fxml"));
        // 현재 창을 가져와서 새 Scene 설정
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(mainRoot));
        // 창 제목 설정
        stage.setTitle(".98");
        // 창 보여주기
        stage.show();
        }else {
            errorLabel.setText("이메일 또는 비밀번호가 잘못되었습니다.");
            errorLabel.setVisible(true);
        }
    }
}
