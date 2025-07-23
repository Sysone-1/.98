package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import com.sysone.ogamza.service.user.MessageService;
import com.sysone.ogamza.view.user.MessageInBoxCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MessageInBoxController {

    @FXML private ListView<MessageInBoxDTO> messageListView;

    @FXML
    public void initialize(){
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        // 📌 커스텀 셀 + 클릭 처리 콜백 전달
        messageListView.setCellFactory(listView ->
                new MessageInBoxCell(this::openMessageDetailModal)
        );

        messageListView.setItems(
                MessageService.getInstance().getInboxMessages(user.getId())
        );
    }

    // 📌 상세 모달 열기
    public void openMessageDetailModal(int messageId) {
        try {
            MessageDetailDTO detail = MessageService.getInstance().getMessageDetail(messageId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/MessageDetail.fxml"));
            Parent root = loader.load();

            MessageDetailController controller = loader.getController();
            controller.setMessageDetail(detail);

            Stage modal = new Stage();
            modal.setTitle("쪽지 상세");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();
            updateReadStatusInList(messageId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateReadStatusInList(int messageId) {
        for (MessageInBoxDTO dto : messageListView.getItems()) {
            if (dto.getMessageId() == messageId && dto.getIsRead() == 0) {
                dto.setIsRead(1); // ← DTO 내부 값 변경
                messageListView.refresh(); // ← ListView 다시 그림
                break;
            }
        }
    }
}
