/**
 * ===========================================
 * 로그인 컨트롤러 (LoginController)
 * ===========================================
 * - 이메일과 비밀번호를 입력받아 로그인 기능 수행
 * - 로그인 성공 시 사용자 권한에 따라 화면 전환
 * - 로그인은 버튼 클릭 또는 ENTER 키로 수행 가능
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

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

    // ▶ UI 요소
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML public Label errorLabel;

    /**
     * ▶ 초기화: ENTER 키 이벤트 설정
     */
    @FXML
    public void initialize() {
        // 비밀번호 필드에서 ENTER 입력 시 로그인 시도
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

        // 이메일 필드에서 ENTER 입력 시 비밀번호 필드로 포커스 이동
        emailField.setOnKeyPressed(keyEvt -> {
            if ("ENTER".equals(keyEvt.getCode().toString())) {
                passwordField.requestFocus();
            }
        });
    }

    /**
     * ▶ 키보드에서 호출되는 로그인 처리
     */
    private void handleLoginFromKeyboard() throws SQLException, IOException {
        performLogin();
    }

    /**
     * ▶ 로그인 버튼 클릭 시 호출되는 이벤트
     */
    @FXML
    private void handleLogin(ActionEvent event) throws SQLException, IOException {
        performLogin();
    }

    /**
     * ▶ 로그인 로직 (공통 처리)
     */
    private void performLogin() throws SQLException, IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        // 입력값 검증
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showError("이메일과 비밀번호를 모두 입력해주세요.");
            return;
        }

        // 로그인 시도
        LoginUserDTO user = loginService.login(email, password);

        if (user == null) {
            showError("이메일 또는 비밀번호가 잘못되었습니다.");
            return;
        }

        // 로그인 성공 시 세션에 저장
        Session.getInstance().setLoginUser(user);

        // 관리자 여부에 따라 화면 선택
        String fxml = user.getIsAdmin() == 1
                ? "/fxml/admin/AdminMainLayout.fxml"
                : "/fxml/user/UserMainLayout.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        // 현재 Stage 찾기
        Stage stage = getCurrentStageAllMethods();

        if (stage == null) {
            System.err.println("❗ Stage 를 찾지 못했습니다. 화면 전환 실패.");
            showError("화면 전환에 실패했습니다.");
            return;
        }

        // 화면 전환
        System.out.println("✅ Stage 찾기 성공! 화면 전환 진행...");
        stage.setScene(new Scene(root));
        stage.setTitle(".98");
        stage.show();
    }

    /**
     * ▶ 현재 Stage를 다양한 방법으로 시도하여 찾기
     */
    private Stage getCurrentStageAllMethods() {
        Stage stage;

        // 방법 1: UI 컴포넌트에서 찾기
        stage = findStageFromComponents();
        if (stage != null) return stage;

        // 방법 2: Window에서 캐스팅 시도
        stage = findStageFromWindow();
        if (stage != null) return stage;

        // 방법 3: 열린 Stage 목록에서 찾기
        stage = findStageFromOpenStages();
        return stage;
    }

    /**
     * ▶ 방법 1: UI 컴포넌트를 통해 Stage 찾기
     */
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

    /**
     * ▶ 방법 2: Window 객체에서 Stage 캐스팅 시도
     */
    private Stage findStageFromWindow() {
        Node[] nodes = {emailField, passwordField, errorLabel};

        for (Node node : nodes) {
            if (node != null && node.getScene() != null) {
                Window window = node.getScene().getWindow();
                System.out.println("🔍 Window 타입: " + (window != null ? window.getClass().getName() : "null"));

                if (window != null) {
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

    /**
     * ▶ 방법 3: 모든 열린 Stage 중에서 활성화된 Stage 찾기
     */
    private Stage findStageFromOpenStages() {
        try {
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

    /**
     * ▶ 에러 메시지를 화면에 출력
     */
    private void showError(String msg) {
        Platform.runLater(() -> {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
        });
    }
}
