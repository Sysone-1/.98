package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.MessageReceiverDAO;
import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import com.sysone.ogamza.dto.user.MessageSentBoxDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
/**
 * 사용자 쪽지 서비스 관련 비즈니스 로직을 처리하는 클래스입니다.
 * 쪽지함 조회(받은/보낸), 쪽지 상세 보기, 읽지 않은 쪽지 수 카운트 기능을 제공합니다.
 * DAO를 통해 데이터베이스와 통신하며 예외 처리를 포함합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class MessageService {

    /** 단일 인스턴스 생성을 위한 싱글톤 패턴 구현 */
    private static final MessageService instance = new MessageService();
    public MessageService(){}
    public static MessageService getInstance(){return instance;}

    /**
     * 받은 쪽지함 목록을 조회합니다.
     *
     * @param employeeId 로그인한 사원의 ID
     * @return 받은 쪽지 목록 (ObservableList 형태)
     */
    public ObservableList<MessageInBoxDTO> getInboxMessages(int employeeId) {
        try {
            List<MessageInBoxDTO> rawList = MessageReceiverDAO.getInstance().getMessageBoxList(employeeId);
            return FXCollections.observableArrayList(rawList);
        } catch (Exception e) {
            System.out.println("메세지를 불러오는데 실패했습니다: " + e.getMessage());
            return null;
        }
    }

    /**
     * 보낸 쪽지함 목록을 조회합니다.
     *
     * @param employeeId 로그인한 사원의 ID
     * @return 보낸 쪽지 목록 (ObservableList 형태)
     */
    public ObservableList<MessageSentBoxDTO> getSentBox(int employeeId) {
        try {
            List<MessageSentBoxDTO> rawList = MessageReceiverDAO.getInstance().getSentList(employeeId);
            return FXCollections.observableArrayList(rawList);
        } catch (Exception e) {
            System.out.println("메세지를 불러오는데 실패했습니다: " + e.getMessage());
            return null;
        }
    }

    /**
     * 특정 쪽지의 상세 내용을 조회합니다.
     *
     * @param msgId 쪽지 ID
     * @return 쪽지 상세 정보 DTO
     */
    public MessageDetailDTO getMessageDetail(int msgId) {
        try {
            return MessageReceiverDAO.getInstance().getMessageDetail(msgId);
        } catch (Exception e) {
            System.out.println("쪽지를 열람하는데 실패하였습니다: " + e.getMessage());
            return null;
        }
    }

    /**
     * 읽지 않은 쪽지 개수를 조회합니다.
     *
     * @param employeeId 로그인한 사원의 ID
     * @return 읽지 않은 쪽지 개수
     */
    public int getUnreadMessageCount(int employeeId) {
        try {
            return MessageReceiverDAO.getInstance().getUnreadMessageCount(employeeId);
        } catch (Exception e) {
            System.out.println("사원의 쪽지를 카운팅하는데 오류가 발생했습니다: " + e.getMessage());
            return 0;
        }
    }
}
