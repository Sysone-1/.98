package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.MessageBoxViewDTO;
import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
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

/**
 * 사용자 쪽지함 화면의 컨트롤러 클래스입니다.
 * 로그인한 사용자가 받은 쪽지와 보낸 쪽지를 조회하며,
 * 탭을 전환해 각 목록을 확인하고 쪽지 상세 보기 및 쪽지 보내기 기능을 제공합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */

public class MessageInBoxController {

    /** 쪽지 목록을 표시하는 ListView */
    @FXML private ListView<MessageBoxViewDTO> messageListView;

    /** 받은 쪽지, 보낸 쪽지 탭 버튼 */
    @FXML private ToggleButton receivedTab;
    @FXML private ToggleButton sentTab;

    /** 초기화 메서드 - 로그인 유저 확인, 탭 그룹 설정, 받은 쪽지 로딩 */
    @FXML
    public void initialize(){
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        // 리스트뷰 셀 커스텀 및 콜백 연결
        messageListView.setCellFactory(listView -> new MessageBoxCell(this::openMessageDetailModal));
        messageListView.setPlaceholder(new Label("쪽지함이 비어있습니다."));

        // 탭 토글 설정
        ToggleGroup tabGroup = new ToggleGroup();
        receivedTab.setToggleGroup(tabGroup);
        sentTab.setToggleGroup(tabGroup);

        receivedTab.setSelected(true);
        applyTabStyle();
        loadReceivedMessages();

        // 탭 클릭 이벤트 설정
        receivedTab.setOnAction(e -> {
            applyTabStyle();
            loadReceivedMessages();
        });

        sentTab.setOnAction(e -> {
            applyTabStyle();
            loadSentMessages();
        });
    }

    /** 쪽지 상세 보기 모달을 띄우는 메서드 */
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

    /** 읽음 처리된 쪽지를 리스트에서 실시간으로 갱신 */
    private void updateReadStatusInList(int messageId) {
        for (MessageBoxViewDTO dto : messageListView.getItems()) {
            if (dto instanceof MessageInBoxDTO && dto.getMessageId() == messageId && dto.getIsRead() == 0) {
                dto.setIsRead(1);
                messageListView.refresh();
                break;
            }
        }
    }

    /** 쪽지 보내기 화면을 모달로 띄우는 메서드 */
    public void handleWriteMessage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/WriteMessage.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("쪽지 보내기");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException e){
            System.out.println("쪽지 보내기 모달 출력 실패" + e.getMessage());
            e.printStackTrace();
        }
    }

    /** 탭 버튼 스타일을 선택 상태에 따라 동적으로 변경 */
    private void applyTabStyle() {
        if (receivedTab.isSelected()) {
            receivedTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor:hand;");
            sentTab.setStyle("-fx-font-size: 14px; -fx-background-color: #E0E0E0; -fx-text-fill: black; -fx-cursor:hand;");
        } else {
            sentTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor:hand;");
            receivedTab.setStyle("-fx-font-size: 14px; -fx-background-color: #E0E0E0; -fx-text-fill: black; -fx-cursor:hand;");
        }
    }

    /** 로그인 유저 기준 받은 쪽지 리스트 로딩 */
    private void loadReceivedMessages(){
        int userId = Session.getInstance().getLoginUser().getId();
        List<MessageBoxViewDTO> list = new ArrayList<>(MessageService.getInstance().getInboxMessages(userId));
        messageListView.setItems(FXCollections.observableArrayList(list));
    }

    /** 로그인 유저 기준 보낸 쪽지 리스트 로딩 */
    private void loadSentMessages(){
        int userId = Session.getInstance().getLoginUser().getId();
        List<MessageBoxViewDTO> list = new ArrayList<>(MessageService.getInstance().getSentBox(userId));
        messageListView.setItems(FXCollections.observableArrayList(list));
    }
}
