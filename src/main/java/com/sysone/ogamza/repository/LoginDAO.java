package com.sysone.ogamza.repository;

import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {
    public boolean isValidUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM employee WHERE email =? AND password = ?";
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2,password);

            try(ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
