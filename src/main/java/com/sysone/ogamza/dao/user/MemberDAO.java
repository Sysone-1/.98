package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MemberDetailDTO;
import com.sysone.ogamza.sql.user.MemberSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;

public class MemberDAO {

    public MemberDetailDTO findByEmail(String email) throws SQLException {
        String query = MemberSQL.SELECT_EMPLOYEE;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MemberDetailDTO(
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getString("tel"),
                            null
                    );
                }
            }
        }
        return null;
    }

    public void updateByEmail(String email, String password, String tel) throws SQLException {
        String sql = MemberSQL.UPDATE_EMPLOYEE;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, password);
            pstmt.setString(2, tel);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
    }
}





