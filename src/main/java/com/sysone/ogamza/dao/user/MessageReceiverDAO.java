package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MessageDetailDTO;
import com.sysone.ogamza.dto.user.MessageInBoxDTO;
import com.sysone.ogamza.dto.user.MessageSentBoxDTO;
import com.sysone.ogamza.sql.user.MessageReceiverSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 쪽지 수신 관련 DAO 클래스입니다.
 * 받은 쪽지, 보낸 쪽지, 상세 조회, 읽지 않은 쪽지 수 카운트 등을 처리합니다.
 * 오라클 DB와 직접 통신합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class MessageReceiverDAO {

    /** 싱글톤 패턴 */
    private static final MessageReceiverDAO instance = new MessageReceiverDAO();
    private MessageReceiverDAO() {}
    public static MessageReceiverDAO getInstance() { return instance; }

    /**
     * 받은 쪽지 리스트 조회
     * @param receiverId 수신자 ID
     * @return 받은 쪽지 리스트
     */
    public List<MessageInBoxDTO> getMessageBoxList(int receiverId) throws SQLException {
        String sql = MessageReceiverSQL.SELECT_RECEIVER;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, receiverId);
            ResultSet resultSet = pstmt.executeQuery();
            List<MessageInBoxDTO> response = new ArrayList<>();

            while (resultSet.next()) {
                response.add(MessageInBoxDTO.builder()
                        .messageId(resultSet.getInt("ID"))
                        .senderProfile(resultSet.getString("PIC_DIR"))
                        .senderName(resultSet.getString("NAME"))
                        .senderDept(resultSet.getString("DEPT_NAME"))
                        .content(resultSet.getString("CONTENT"))
                        .sendDate(resultSet.getDate("SEND_DATE").toLocalDate())
                        .isRead(resultSet.getInt("IS_READ"))
                        .build());
            }
            return response;
        }
    }

    /**
     * 쪽지 상세 정보 조회 (읽음 처리 포함)
     * @param msgId 쪽지 ID
     * @return 쪽지 상세 DTO
     */
    public MessageDetailDTO getMessageDetail(int msgId) throws SQLException {
        String sql = MessageReceiverSQL.SELECT_MESSAGE;
        String updateRead = MessageReceiverSQL.UPDATE_READ;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement update = conn.prepareStatement(updateRead)) {

            update.setInt(1, msgId);
            update.executeUpdate();

            pstmt.setInt(1, msgId);
            ResultSet resultSet = pstmt.executeQuery();
            MessageDetailDTO response = null;

            if (resultSet.next()) {
                response = MessageDetailDTO.builder()
                        .name(resultSet.getString("NAME"))
                        .content(resultSet.getString("CONTENT"))
                        .sendDate(resultSet.getDate("SEND_DATE").toLocalDate())
                        .isRead(resultSet.getInt("IS_READ"))
                        .build();
            }
            return response;
        }
    }

    /**
     * 읽지 않은 쪽지 개수 조회
     * @param employeeId 사원 ID
     * @return 읽지 않은 쪽지 개수
     */
    public int getUnreadMessageCount(int employeeId) throws SQLException {
        String sql = MessageReceiverSQL.SELECT_COUNT;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("COUNT");
            } else {
                return 0;
            }
        }
    }

    /**
     * 보낸 쪽지 리스트 조회
     * @param employeeId 발신자 ID
     * @return 보낸 쪽지 리스트
     */
    public List<MessageSentBoxDTO> getSentList(int employeeId) throws SQLException {
        String sql = MessageReceiverSQL.SELECT_SENT;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet resultSet = pstmt.executeQuery();
            List<MessageSentBoxDTO> response = new ArrayList<>();

            while (resultSet.next()) {
                response.add(MessageSentBoxDTO.builder()
                        .messageId(resultSet.getInt("ID"))
                        .receiverName(resultSet.getString("NAME"))
                        .receiverDept(resultSet.getString("DEPT_NAME"))
                        .content(resultSet.getString("CONTENT"))
                        .sentAt(resultSet.getDate("SEND_DATE").toLocalDate())
                        .build());
            }
            return response;
        }
    }
}
