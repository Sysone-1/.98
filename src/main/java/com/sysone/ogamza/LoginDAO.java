package com.sysone.ogamza;

import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;

public class LoginDAO {
    public LoginUserDTO getUserInfo (String email, String password) throws SQLException {
        String query = """
                  SELECT e.id, d.name AS dept_name, e.position, e.email, e.name, e.is_admin, e.card_uid, e.pic_dir
                  FROM employee e
                  JOIN department d ON d.id = e.department_id
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
                            rs.getString("dept_name"),
                            rs.getString("position"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getInt("is_admin"),
                            cardUid,
                            rs.getString("pic_dir")
                    );
                }
            }
        }
        return null;
    }
}
