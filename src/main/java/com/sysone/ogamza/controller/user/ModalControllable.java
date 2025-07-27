package com.sysone.ogamza.controller.user;

import javafx.stage.Stage;

/**
 * ===========================================
 * 모달 창 제어 인터페이스 (ModalControllable)
 * ===========================================
 * - FXML 컨트롤러가 모달(Stage)을 제어할 수 있도록 정의된 인터페이스
 * - 컨트롤러가 자신이 속한 모달 창(Stage)을 외부에서 주입받고 제어 가능하게 함
 * - 주로 모달 닫기, Stage 속성 제어 등에 사용됨
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public interface ModalControllable {

    /**
     * ▶ 모달 Stage 객체를 주입받기 위한 메서드
     *
     * @param modalStage 외부에서 전달받은 모달 Stage
     */
    void setModalStage(Stage modalStage);

    /**
     * ▶ 현재 컨트롤러가 가지고 있는 모달 Stage 반환
     *
     * @return Stage 모달 Stage 객체
     */
    Stage getModalStage();
}
