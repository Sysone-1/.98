package com.sysone.ogamza.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

public class SettingsController {

    //추가
    private boolean toggleSwitchState = false;
    private String selectedTime = "3분전";
    //함수
    private <T> T openModal(String fxmlPath, String title) {
        try {
            //파일을 불러옴
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            //FXML을 Java 객체로 로드
            Parent root = loader.load();

            //모달 생성
            Stage modalStage = new Stage();
            //모달 설정
            modalStage.initModality(Modality.APPLICATION_MODAL);
            //창 위의 제목을 설정
            modalStage.setTitle(title);
            //모달 창에서 보일 UI 구성
            modalStage.setScene(new Scene(root));
            //모달 크기 변경 금지
            modalStage.setResizable(false);

            // 컨트롤러에게 모달 Stage 전달
            T controller = loader.getController();
            if (controller instanceof ModalControllable) {
                ((ModalControllable)controller).setModalStage(modalStage);
            }
            return controller;
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @FXML
    public void openEditMemberModal(MouseEvent mouseEvent) {
        EditMemberModalController controller =  openModal("/fxml/user/EditMemberModal.fxml","회원정보 수정");
        if (controller != null) {
            controller.getModalStage().showAndWait();
        }


    }

    @FXML
    public void openAlarmModal(MouseEvent mouseEvent) {
        AlramController controller =  openModal("/fxml/user/Alarm.fxml","퇴근 알림 설정");
        if (controller != null) {
            controller.setSavedSetting(toggleSwitchState, selectedTime);
            controller.restoreSavedSetting();

            // 모달 띄우기
            controller.getModalStage().showAndWait();

            //모달 닫힌 뒤 값 반영
            toggleSwitchState = controller.getToggleSwitchState();
            selectedTime = controller.getSelectedTime();
        }

    }
}