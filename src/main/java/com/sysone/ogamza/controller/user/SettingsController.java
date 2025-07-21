package com.sysone.ogamza.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    //추가
    private boolean toggleSwitchState = false;
    private String selectedTime = "3분전";
    //함수
    private void openModal(String fxmlPath, String title) {
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

            // AlarmController 인스턴스 가져오기
            AlramController alarmController = loader.getController();

            //추가
            alarmController.setSavedSetting(toggleSwitchState, selectedTime);

            //AlramController에 stage 객체 전달
            alarmController.setModalStage(modalStage);
            alarmController.restoreSavedSetting();
            //모달 창이 닫힐 때까지 대기
            modalStage.showAndWait();

            //상태 저장 (닫힌 이후 값 갱신)
            toggleSwitchState = alarmController.getToggleSwitchState();
            selectedTime = alarmController.getSelectedTime();
        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void openEditMemberModal(MouseEvent mouseEvent) {
        openModal("/fxml/user/EditMemberModal.fxml","회원정보 수정");

    }

    public void openAlarmModal(MouseEvent mouseEvent) {
        openModal("/fxml/user/Alarm.fxml","퇴근 알림 설정");

    }
}
