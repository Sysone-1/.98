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

    private <T> T openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);

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

    @FXML
    public void openEditMemberModal(MouseEvent mouseEvent) {
        EditMemberModalController controller = openModal("/fxml/user/EditMemberModal.fxml", "회원정보 수정");
        if (controller != null) {
            controller.getModalStage().showAndWait();
        }
    }

    @FXML
    public void openAlarmModal(MouseEvent mouseEvent) {
        AlramController controller = openModal("/fxml/user/Alarm.fxml", "퇴근 알림 설정");
        if (controller != null) {
            controller.getModalStage().showAndWait();
        }
    }
}
