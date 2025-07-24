package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.MessageDetailDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MessageDetailController {

    @FXML
    private Label senderNameLabel;
    @FXML
    private Label sendDateLabel;
    @FXML
    private TextArea contentArea;
//    @FXML
//    private Button closeButton;

    public void setMessageDetail(MessageDetailDTO dto) {


        if (dto == null) {
            System.err.println("❗ MessageDetailDTO is null. 쪽지 데이터를 불러오지 못했습니다.");
            senderNameLabel.setText("알 수 없음");
            sendDateLabel.setText("-");
            contentArea.setText("쪽지 내용을 불러올 수 없습니다.");
            return;
        }

        senderNameLabel.setText(dto.getName() != null ? dto.getName() : "이름 없음");
        sendDateLabel.setText(dto.getSendDate() != null ? dto.getSendDate().toString() : "-");
        contentArea.setText(dto.getContent() != null ? dto.getContent() : "(내용 없음)");
    }

//    @FXML
//    private void handleClose() {
//        Stage stage = (Stage) closeButton.getScene().getWindow();
//        stage.close();
//    }

}
