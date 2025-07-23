package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MemberDetailDTO;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;

public class MemberDAO {

    public MemberDetailDTO findByEmail(String email) throws SQLException {
        String query = "SELECT name, position, tel, password FROM employee WHERE email = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
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
        String sql = "UPDATE employee SET password = ?, tel = ? WHERE email = ?";
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, tel);
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }
}





