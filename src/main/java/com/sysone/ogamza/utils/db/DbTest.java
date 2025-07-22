package com.sysone.ogamza.utils.db;
import java.sql.*;

public class DbTest {
    public static void main(String[] args) {
        try (Connection conn = OracleConnector.getConnection()) {
            System.out.println("✅ 연결 성공!");
            System.out.println("DB 버전: " + conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}