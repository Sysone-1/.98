package com.sysone.ogamza;

import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;

public class LoginDAO {
    public LoginUserDTO getUserInfo (String email, String password) throws SQLException {
        String query = """
                  SELECT id, department_id, position, email, name, is_admin, card_uid
                  FROM employee 
                  WHERE email = ? AND password = ?
                """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2,password);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    Blob blob = rs.getBlob("card_uid");
                    String cardUid = blob != null ? new String(blob.getBytes(1,(int) blob.length())):null;
                    return new LoginUserDTO (
                            rs.getInt("id"),
                            rs.getInt("department_id"),
                            rs.getString("position"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getInt("is_admin"),
                            cardUid
                    );
                }
            }
        }
        return null;
    }
}
