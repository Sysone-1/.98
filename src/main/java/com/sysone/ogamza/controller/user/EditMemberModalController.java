package com.sysone.ogamza.controller.user;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class EditMemberModalController implements ModalControllable{
    private Stage modalStage;

    @Override
    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    @Override
    public Stage getModalStage() {
        return modalStage;
    }

    public void handleCancel(ActionEvent actionEvent) {
    }

    public void handleSave(ActionEvent actionEvent) {
    }
}
