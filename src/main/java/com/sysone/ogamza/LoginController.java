package com.sysone.ogamza;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private final LoginService loginService = new LoginService();

    // UI
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML public Label errorLabel;

    // ★ 장착되면 재사용할 Stage 캐시
    private Stage primaryStage;

    /*---------------- 초기화 ----------------*/
    @FXML
    public void initialize() {

        /* ▶ Scene 이 붙으면 Stage 캐시 */
        emailField.sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    primaryStage = (Stage) newScene.getWindow();   // 한 번만 저장
                }
            }
        });

        /* ▶ ENTER 키 처리 */
        passwordField.setOnKeyPressed(keyEvt -> {
            if ("ENTER".equals(keyEvt.getCode().toString())) {
                try {
                    handleLogin(new ActionEvent(passwordField, null)); // fake event
                } catch (Exception e) {

                }
            }
        });

        emailField.setOnKeyPressed(keyEvt -> {
            if ("ENTER".equals(keyEvt.getCode().toString())) {
                passwordField.requestFocus();
            }
        });
    }

    /*---------------- 로그인 ----------------*/
    @FXML
    private void handleLogin(ActionEvent event) throws SQLException, IOException {

        String email    = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showError("이메일과 비밀번호를 모두 입력해주세요.");
            return;
        }

        LoginUserDTO user = loginService.login(email, password);

        if (user == null) {
            showError("이메일 또는 비밀번호가 잘못되었습니다.");
            return;
        }

        /* 로그인 성공 → 세션 저장 */
        Session.getInstance().setLoginUser(user);

        String fxml = user.getIsAdmin() == 1
                ? "/fxml/admin/AdminMainLayout.fxml"
                : "/fxml/user/UserMainLayout.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        /* ▶ Stage 선택 우선순위
           1) ActionEvent 로부터
           2) 캐시 primaryStage
        */
        Stage stage = null;
        if (event != null && event.getSource() instanceof Node n && n.getScene() != null) {
            stage = (Stage) n.getScene().getWindow();
        } else if (primaryStage != null) {
            stage = primaryStage;
        }

        if (stage == null) {
            System.err.println("❗ Stage 를 찾지 못했습니다. 화면 전환 실패.");
            return;
        }

        stage.setScene(new Scene(root));
        stage.setTitle(".98");
        stage.show();
    }

    /*---------------- 공통 ----------------*/
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}

