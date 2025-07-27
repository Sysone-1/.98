package com.sysone.ogamza;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * ===========================================
 * 로그아웃 유틸리티 (LogoutUtil)
 * ===========================================
 * - 전역적으로 로그아웃 기능을 처리하는 유틸리티 클래스
 * - 세션 초기화 및 로그인 화면으로 전환
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class LogoutUtil {

    /**
     * ▶ 로그아웃 처리 메서드
     * - 세션 초기화
     * - 로그인 화면으로 전환
     *
     * @param event 로그아웃을 트리거한 마우스 이벤트 (현재 Stage 탐색용)
     */
    public static void logout(MouseEvent event) {
        try {
            // 1. 세션 초기화 (로그인 사용자 정보 제거)
            Session.getInstance().clear();

            // 2. 로그인 화면 FXML 로드
            Parent loginRoot = FXMLLoader.load(
                    Objects.requireNonNull(LogoutUtil.class.getResource("/fxml/Login.fxml"))
            );

            // 3. 새로운 Scene 생성
            Scene loginScene = new Scene(loginRoot);

            // 4. 현재 Stage 가져오기 (이벤트 발생한 노드로부터)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 5. Scene 전환 및 속성 설정
            stage.setScene(loginScene);
            stage.setTitle("로그인");
            stage.setWidth(1300);    // 창 너비 설정
            stage.setHeight(820);    // 창 높이 설정
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // FXML 로드 실패 시 예외 출력
        }
    }
}
