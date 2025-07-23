package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MessageDTO;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDAO {
    public void insertMessage(MessageDTO dto) {
        String sql = "INSERT INTO MESSAGE (id, sender_id, receiver_id, content) values (message_seq.nextval, ?,?,?)";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getSenderId());
            pstmt.setInt(2, dto.getReceiverId());
            pstmt.setString(3, dto.getContent());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
