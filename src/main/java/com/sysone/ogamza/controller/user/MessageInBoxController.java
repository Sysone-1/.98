package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.MessageBoxViewDTO;
import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import com.sysone.ogamza.dto.user.MessageSentBoxDTO;
import com.sysone.ogamza.service.user.MessageService;
import com.sysone.ogamza.view.user.MessageBoxCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageInBoxController {

    @FXML private ListView<MessageBoxViewDTO> messageListView;
    @FXML private ToggleButton receivedTab;
    @FXML private ToggleButton sentTab;

    @FXML
    public void initialize(){
        // 유저 정보 가져오기
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        // 📌 커스텀 셀 + 클릭 처리 콜백 전달
        messageListView.setCellFactory(listView -> new MessageBoxCell(this::openMessageDetailModal));
        messageListView.setPlaceholder(new Label("쪽지함이 비어있습니다."));


        ToggleGroup tabGroup = new ToggleGroup();
        receivedTab.setToggleGroup(tabGroup);
        sentTab.setToggleGroup(tabGroup);

        // 초기화
        receivedTab.setSelected(true);
        applyTabStyle();
        loadReceivedMessages();


        // 이벤트 핸들링
        receivedTab.setOnAction(e -> {
            applyTabStyle();
            loadReceivedMessages(); // 받은 쪽지 리스트 로드
        });

        sentTab.setOnAction(e -> {
            applyTabStyle();
            loadSentMessages(); // 보낸 쪽지 리스트 로드
        });
    }

    //  상세 모달 열기
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
            System.out.println("쪽지 상세 모달 출력 실패");
            e.printStackTrace();
        }
    }


    // 읽음처리 view 실시간 적용
    private void updateReadStatusInList(int messageId) {
        for (MessageBoxViewDTO dto : messageListView.getItems()) {
            if (dto instanceof MessageInBoxDTO && dto.getMessageId() == messageId && dto.getIsRead() == 0)
            {
                dto.setIsRead(1); // ← DTO 내부 값 변경
                messageListView.refresh(); // ← ListView 다시 그림
                break;
            }
        }
    }

    // 쪽지 보내기 모달 띄우기
    public void handleWriteMessage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/WriteMessage.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("쪽지 보내기");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        }catch (IOException e){
            System.out.println("쪽지 보내기 모달 출력 실패"+e.getMessage());
            e.printStackTrace();
        }

    }


    // 💡 스타일 스위칭 함수
    private void applyTabStyle() {
        if (receivedTab.isSelected()) {
            receivedTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor:hand;");
            sentTab.setStyle("-fx-font-size: 14px; -fx-background-color: #E0E0E0; -fx-text-fill: black; -fx-cursor:hand;");
        } else {
            sentTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor:hand;");
            receivedTab.setStyle("-fx-font-size: 14px; -fx-background-color: #E0E0E0; -fx-text-fill: black; -fx-cursor:hand;");
        }
    }

    // 받은 편지 로드
    private void loadReceivedMessages(){
        int userId = Session.getInstance().getLoginUser().getId();
        List<MessageBoxViewDTO> list = new ArrayList<>(MessageService.getInstance().getInboxMessages(userId));
        messageListView.setItems(FXCollections.observableArrayList(list));
    }

    // 보낸 편지 로드
    private void loadSentMessages(){
        int userId = Session.getInstance().getLoginUser().getId();
        List<MessageBoxViewDTO> list = new ArrayList<>(MessageService.getInstance().getSentBox(userId));
        messageListView.setItems(FXCollections.observableArrayList(list));
    }

}
