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

public class EditMemberModalController implements ModalControllable, Initializable {
    @FXML private Label telErrorLabel;
    @FXML private Label pwErrorLabel;
    @FXML private Label nameLabel;
    @FXML private Label positionLabel;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phonePart2;
    @FXML private TextField phonePart3;

    private final MemberService memberService = new MemberService();
    private Stage modalStage;
    private String email; //세션에서 가져온 이메일

    // 모달 Stage 세터 / 게터 (interface 구현)
    @Override
    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    @Override
    public Stage getModalStage() {
        return modalStage;
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        //1. 세션에서 이메일 꺼냄
        LoginUserDTO user = Session.getInstance().getLoginUser();
        email = user.getEmail();

        try {
            //2. 세션에서 회원 정보 가져옴
            MemberDetailDTO dto = memberService.getMemberDetail(email);

            nameLabel.setText(dto.getName());
            positionLabel.setText(dto.getPosition());

            //3. 전화번호 분리 (010-1234-5678)
            String[] parts = dto.getTel().split("-");
            if (parts.length == 3) {
                phonePart2.setText(parts[1]);
                phonePart3.setText(parts[2]);
            }
        }catch (Exception e) {
            e.printStackTrace();
            alert("회원 정보를 불러오는데 실패했습니다.");
        }
    }

    private void alert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("알림");
            alert.setHeaderText(null); // 헤더 생략
            alert.setContentText(msg);
            alert.initOwner(modalStage); // 현재 모달 창 기준으로 띄움
            alert.showAndWait(); // 확인 누를 때까지 대기
        });
    }


    public void handleSave(ActionEvent actionEvent) {
        clearErrors();  //모든 에러 숨기기
        
        String pw1 = passwordField.getText();
        String pw2 = confirmPasswordField.getText();
        String mid = phonePart2.getText();
        String last = phonePart3.getText();

        boolean hasError = false;

        if(pw1 == null || pw1.isBlank() || pw2 == null || pw2.isBlank()) {
            showPwError("비밀번호를 입력해주세요.");
            hasError = true;
        } else if (!pw1.equals(pw2)) {
            showPwError("비밀번호가 일치하지 않습니다.");
            hasError = true;
        }
        if (!mid.matches("\\d{3,4}") || !last.matches("\\d{4}")) {
            showTelError("연락처 형식이 올바르지 않습니다.");
            hasError = true;
        }
        if (hasError) {
            return;
        }

        String newTel = "010-" +mid + "-" + last;

        try {
            memberService.updateMember(email, pw1, newTel);
            alert("회원 정보가 저장되었습니다.");
            modalStage.close(); //모달 닫기
        }catch (Exception e) {
            e.printStackTrace();
            alert("회원 정보 저장 실패");
        }
    }

    private void clearErrors() {
        pwErrorLabel.setVisible(false);
        telErrorLabel.setVisible(false);
    }

    private void showTelError(String msg) {
        telErrorLabel.setText(msg);
        telErrorLabel.setVisible(true);

    }

    private void showPwError(String msg) {
        pwErrorLabel.setText(msg);
        pwErrorLabel.setVisible(true);
        pwErrorLabel.setManaged(true);
    }
}
