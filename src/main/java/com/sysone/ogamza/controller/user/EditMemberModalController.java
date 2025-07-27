package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.MemberDetailDTO;
import com.sysone.ogamza.service.user.MemberService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * ===========================================
 * 회원 정보 수정 모달 컨트롤러 (EditMemberModalController)
 * ===========================================
 * - 로그인된 사용자의 이름, 직책, 연락처 수정 기능 제공
 * - 연락처 형식 유효성 검사 및 비밀번호 확인 기능 포함
 * - 저장 시 MemberService를 통해 DB에 수정 내용 반영
 * - ModalControllable 인터페이스를 통해 모달 Stage 제어
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class EditMemberModalController implements ModalControllable, Initializable {

    // ▶ FXML UI 구성요소
    @FXML private Label telErrorLabel;           // 연락처 오류 메시지 출력용
    @FXML private Label pwErrorLabel;            // 비밀번호 오류 메시지 출력용
    @FXML private Label nameLabel;               // 사용자 이름 표시
    @FXML private Label positionLabel;           // 사용자 직책 표시
    @FXML private PasswordField passwordField;   // 새 비밀번호 입력 필드
    @FXML private PasswordField confirmPasswordField; // 비밀번호 확인 필드
    @FXML private TextField phonePart2;          // 연락처 중간 자리 (예: 1234)
    @FXML private TextField phonePart3;          // 연락처 마지막 자리 (예: 5678)

    // ▶ 내부 변수
    private final MemberService memberService = new MemberService(); // 회원 서비스 객체
    private Stage modalStage;      // 모달 Stage
    private String email;          // 로그인된 사용자의 이메일

    /**
     * ▶ ModalControllable 구현: 모달 Stage 주입
     */
    @Override
    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    /**
     * ▶ ModalControllable 구현: 모달 Stage 반환
     */
    @Override
    public Stage getModalStage() {
        return modalStage;
    }

    /**
     * ▶ 화면 초기화 시 실행되는 메서드
     * - 로그인된 사용자 정보를 세션에서 가져와 초기값 세팅
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. 세션에서 로그인 사용자 정보 가져오기
        LoginUserDTO user = Session.getInstance().getLoginUser();
        email = user.getEmail();

        try {
            // 2. 회원 상세 정보 조회
            MemberDetailDTO dto = memberService.getMemberDetail(email);

            // 3. 화면에 기본 정보 출력
            nameLabel.setText(dto.getName());
            positionLabel.setText(dto.getPosition());

            // 4. 전화번호 형식 "010-1234-5678" → 파싱
            String[] parts = dto.getTel().split("-");
            if (parts.length == 3) {
                phonePart2.setText(parts[1]);
                phonePart3.setText(parts[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert("회원 정보를 불러오는데 실패했습니다.");
        }
    }

    /**
     * ▶ 저장 버튼 클릭 시 호출
     * - 입력값 검증 → 저장 요청 → 성공 시 모달 종료
     */
    public void handleSave(ActionEvent actionEvent) {
        clearErrors(); // 오류 메시지 초기화

        String pw1 = passwordField.getText();
        String pw2 = confirmPasswordField.getText();
        String mid = phonePart2.getText();
        String last = phonePart3.getText();

        boolean hasError = false;

        // ▶ 비밀번호 유효성 검사
        if (pw1 == null || pw1.isBlank() || pw2 == null || pw2.isBlank()) {
            showPwError("비밀번호를 입력해주세요.");
            hasError = true;
        } else if (!pw1.equals(pw2)) {
            showPwError("비밀번호가 일치하지 않습니다.");
            hasError = true;
        }

        // ▶ 연락처 유효성 검사
        if (!mid.matches("\\d{3,4}") || !last.matches("\\d{4}")) {
            showTelError("연락처 형식이 올바르지 않습니다.");
            hasError = true;
        }

        // ▶ 에러 발생 시 중단
        if (hasError) return;

        // ▶ 새 연락처 생성
        String newTel = "010-" + mid + "-" + last;

        try {
            memberService.updateMember(email, pw1, newTel); // DB 저장 요청
            alert("회원 정보가 저장되었습니다.");
            modalStage.close(); // 모달 닫기
        } catch (Exception e) {
            e.printStackTrace();
            alert("회원 정보 저장 실패");
        }
    }

    /**
     * ▶ 오류 메시지 모두 숨기기
     */
    private void clearErrors() {
        pwErrorLabel.setVisible(false);
        telErrorLabel.setVisible(false);
    }

    /**
     * ▶ 전화번호 오류 메시지 표시
     */
    private void showTelError(String msg) {
        telErrorLabel.setText(msg);
        telErrorLabel.setVisible(true);
    }

    /**
     * ▶ 비밀번호 오류 메시지 표시
     */
    private void showPwError(String msg) {
        pwErrorLabel.setText(msg);
        pwErrorLabel.setVisible(true);
        pwErrorLabel.setManaged(true); // 공간 유지
    }

    /**
     * ▶ 알림 팝업 표시 (모달 기준)
     */
    private void alert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("알림");
            alert.setHeaderText(null); // 헤더 생략
            alert.setContentText(msg);
            alert.initOwner(modalStage); // 모달 기준 위치
            alert.showAndWait();
        });
    }
}
