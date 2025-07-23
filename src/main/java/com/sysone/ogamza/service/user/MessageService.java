package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.MessageReceiverDAO;
import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MessageService {

    private static final MessageService instance = new MessageService();
    private MessageService(){}
    public static MessageService getInstance(){return instance;}


    public ObservableList<MessageInBoxDTO> getInboxMessages(int userId) {
        try {
            List<MessageInBoxDTO> rawList = MessageReceiverDAO.getInstance().getMessageBoxList(userId);
            return FXCollections.observableArrayList(rawList);
        }catch (Exception e){
            System.out.println("메세지를 불러오는데 실패했습니다" + e.getMessage());
            return null;
        }
    }

    public MessageDetailDTO getMessageDetail(int msgId){

        MessageDetailDTO msg = null;
        try{
            msg = MessageReceiverDAO.getInstance().getMessageDetail(msgId);

            return msg;
        }catch (Exception e){
            System.out.println("쪽지를 열람하는데 실패하였습니다." + e.getMessage());
            return msg;
        }
    }

    public int getUnreadMessageCount(int employeeId) {
        try {
            return MessageReceiverDAO.getInstance().getUnreadMessageCount(employeeId);
        }catch (Exception e){
            System.out.println("사원의 쪽지를 카운팅하는데 오류가 발생했습니다"+ e.getMessage());
            return 0;
        }
    }

}
