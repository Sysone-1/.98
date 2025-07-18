package com.sysone.ogamza.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    public void openEditMemberModal(MouseEvent mouseEvent) {
        try {
            // 파일을 불러옴.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditMemberModal.fxml"));
            // FXML을 Java 객체로 로드
            Parent root = loader.load();
            // 모달 생성
            Stage modalStage = new Stage();
            // 모달 설정
            modalStage.initModality(Modality.APPLICATION_MODAL); //모달로 설정
            // 창 위의 제목을 설정
            modalStage.setTitle("회원 정보 수정");
            // 모달 창에서 보일 UI 구성
            modalStage.setScene(new Scene(root));
            // 모달 크기 변경 금지
            modalStage.setResizable(false);
            //모달 창이 닫힐 떄까지 대기
            modalStage.showAndWait();
        }catch(IOException e) {
            e.printStackTrace();

        }
    }
}
