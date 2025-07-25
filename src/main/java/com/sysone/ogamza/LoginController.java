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
import javafx.stage.Window;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private final LoginService loginService = new LoginService();

    // UI
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML public Label errorLabel;

    /*---------------- 초기화 ----------------*/
    @FXML
    public void initialize() {

        /* ▶ ENTER 키 처리 */
        passwordField.setOnKeyPressed(keyEvt -> {
            if ("ENTER".equals(keyEvt.getCode().toString())) {
                Platform.runLater(() -> {
                    try {
                        handleLoginFromKeyboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("로그인 처리 중 오류가 발생했습니다.");
                    }
                });
            }
        });

        emailField.setOnKeyPressed(keyEvt -> {
            if ("ENTER".equals(keyEvt.getCode().toString())) {
                passwordField.requestFocus();
            }
        });
    }

    /*---------------- 키보드에서 호출되는 로그인 ----------------*/
    private void handleLoginFromKeyboard() throws SQLException, IOException {
        performLogin();
    }

    /*---------------- 버튼에서 호출되는 로그인 ----------------*/
    @FXML
    private void handleLogin(ActionEvent event) throws SQLException, IOException {
        performLogin();
    }

    /*---------------- 실제 로그인 로직 ----------------*/
    private void performLogin() throws SQLException, IOException {
        String email = emailField.getText();
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

        /* ▶ Stage 찾기 - 모든 방법 시도 */
        Stage stage = getCurrentStageAllMethods();

        if (stage == null) {
            System.err.println("❗ Stage 를 찾지 못했습니다. 화면 전환 실패.");
            showError("화면 전환에 실패했습니다.");
            return;
        }

        System.out.println("✅ Stage 찾기 성공! 화면 전환 진행...");
        stage.setScene(new Scene(root));
        stage.setTitle(".98");
        stage.show();
    }

    /*---------------- 모든 방법으로 Stage 찾기 ----------------*/
    private Stage getCurrentStageAllMethods() {
        Stage stage = null;

        // 방법 1: UI 컴포넌트에서 직접 찾기
        stage = findStageFromComponents();
        if (stage != null) return stage;

        // 방법 2: Window에서 Stage로 캐스팅 (PopupWindow 등 처리)
        stage = findStageFromWindow();
        if (stage != null) return stage;

        // 방법 3: 모든 열린 Stage에서 찾기
        stage = findStageFromOpenStages();
        if (stage != null) return stage;

        return null;
    }

    /*---------------- 방법 1: UI 컴포넌트에서 Stage 찾기 ----------------*/
    private Stage findStageFromComponents() {
        Node[] nodes = {emailField, passwordField, errorLabel};

        for (Node node : nodes) {
            if (node != null && node.getScene() != null) {
                Window window = node.getScene().getWindow();
                if (window instanceof Stage) {
                    System.out.println("✅ [방법1] Stage 발견: " + node.getClass().getSimpleName());
                    return (Stage) window;
                }
            }
        }
        return null;
    }

    /*---------------- 방법 2: Window에서 Stage 찾기 ----------------*/
    private Stage findStageFromWindow() {
        Node[] nodes = {emailField, passwordField, errorLabel};

        for (Node node : nodes) {
            if (node != null && node.getScene() != null) {
                Window window = node.getScene().getWindow();
                System.out.println("🔍 Window 타입: " + (window != null ? window.getClass().getName() : "null"));

                // Window가 Stage의 서브클래스일 수 있음
                if (window != null) {
                    // Owner가 Stage인 경우 (Dialog 등)
                    if (window.getClass().getName().contains("Stage") ||
                            window.toString().contains("Stage")) {
                        try {
                            Stage stage = (Stage) window;
                            System.out.println("✅ [방법2] Stage 발견: 캐스팅 성공");
                            return stage;
                        } catch (ClassCastException e) {
                            System.out.println("❌ [방법2] 캐스팅 실패: " + e.getMessage());
                        }
                    }
                }
            }
        }
        return null;
    }

    /*---------------- 방법 3: 열린 모든 Stage에서 찾기 ----------------*/
    private Stage findStageFromOpenStages() {
        try {
            // JavaFX의 모든 열린 Stage 가져오기
            for (Window window : Stage.getWindows()) {
                if (window instanceof Stage && window.isShowing()) {
                    Stage stage = (Stage) window;
                    System.out.println("✅ [방법3] 열린 Stage 발견: " + stage.getTitle());
                    return stage;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ [방법3] 실패: " + e.getMessage());
        }
        return null;
    }

    /*---------------- 공통 ----------------*/
    private void showError(String msg) {
        Platform.runLater(() -> {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
        });
    }
}