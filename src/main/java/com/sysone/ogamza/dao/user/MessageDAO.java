package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MessageDTO;
import com.sysone.ogamza.sql.user.MessageSenderSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * =============================================
 * 쪽지 전송 DAO (MessageDAO)
 * =============================================
 * - DB에 쪽지 데이터를 INSERT하는 기능을 제공
 * - DB 연결 및 SQL 실행 책임을 가짐
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
public class MessageDAO {

    /**
     * ▶ 쪽지 전송
     * - 전달받은 DTO 정보를 기반으로 DB에 쪽지를 INSERT
     *
     * @param dto 전송할 쪽지 정보 (보낸 사람, 받는 사람, 내용 포함)
     */
    public void insertMessage(MessageDTO dto) {
        String sql = MessageSenderSQL.INSERT_MESSAGE;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // SQL에 값 바인딩
            pstmt.setInt(1, dto.getSenderId());   // 보낸 사람 ID
            pstmt.setInt(2, dto.getReceiverId()); // 받는 사람 ID
            pstmt.setString(3, dto.getContent()); // 쪽지 내용

            // DB 실행
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // 예외 발생 시 런타임 예외로 변환 후 전파
            throw new RuntimeException(e);
        }
    }
}
