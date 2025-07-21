package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.sql.user.UserHomeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmojiDAO {
    private static final EmojiDAO instance = new EmojiDAO();
    private EmojiDAO(){}
    public static EmojiDAO getInstance(){return instance;}


    public int updateEmoji(int employeeId, String emoji)throws SQLException{

        String sql = UserHomeSQL.UPDATE_EMOJI;

        try(Connection conn = OracleConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1, emoji);
            pstmt.setInt(2,employeeId);
            return pstmt.executeUpdate();
        }
    }
}
