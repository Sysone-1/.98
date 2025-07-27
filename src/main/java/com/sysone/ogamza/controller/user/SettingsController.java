package com.sysone.ogamza.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ===========================================
 * 설정 페이지 컨트롤러 (SettingsController)
 * ===========================================
 * - 설정 페이지의 모달 창 오픈을 담당하는 컨트롤러
 * - 회원 정보 수정, 퇴근 알림 설정 등의 모달 창 호출 기능 포함
 * - 공통 모달 오픈 메서드로 재사용성 향상
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class SettingsController {

    /**
     * ▶ 공통 모달 창 열기 메서드
     * - FXML 경로와 타이틀을 받아 모달을 열고 컨트롤러를 반환
     * - ModalControllable 구현체라면 Stage 주입
     *
     * @param fxmlPath FXML 파일 경로
     * @param title    모달 창 제목
     * @return 모달을 제어할 컨트롤러 객체 (null 가능성 있음)
     */
    private <T> T openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL); // 모달로 설정
            modalStage.setTitle(title);                          // 창 제목 설정
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);                      // 크기 고정

            // 컨트롤러에 모달 Stage 주입
            T controller = loader.getController();
            if (controller instanceof ModalControllable) {
                ((ModalControllable) controller).setModalStage(modalStage);
            }

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ▶ 회원 정보 수정 모달 열기
     * - EditMemberModalController 호출
     */
    @FXML
    public void openEditMemberModal(MouseEvent mouseEvent) {
        EditMemberModalController controller = openModal("/fxml/user/EditMemberModal.fxml", "회원정보 수정");
        if (controller != null) {
            controller.getModalStage().showAndWait();
        }
    }

    /**
     * ▶ 퇴근 알림 설정 모달 열기
     * - AlramController 호출
     */
    @FXML
    public void openAlarmModal(MouseEvent mouseEvent) {
        AlramController controller = openModal("/fxml/user/Alarm.fxml", "퇴근 알림 설정");
        if (controller != null) {
            controller.getModalStage().showAndWait();
        }
    }
}
