package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.AlarmSettingDTO;
import com.sysone.ogamza.utils.db.OracleConnector;
import oracle.ucp.proxy.annotation.Pre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlarmDAO {
    public AlarmSettingDTO findByUserId(int id) {
        String sql = "SELECT alarm_1, alarm_2, alarm_3 FROM employee WHERE id = ? AND IS_DELETED = 0";
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new AlarmSettingDTO(id,
                        rs.getInt("alarm_1"),
                        rs.getInt("alarm_2"),
                        rs.getInt("alarm_3"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void saveOrUpdate(int id, int minutesBefore) {
        int a1 = 0, a2 = 0, a3 = 0;
        switch (minutesBefore) {
            case 3 -> a1 = 1;
            case 5 -> a2 = 1;
            case 10 -> a3 = 1;
        }
        String sql = """
                    MERGE INTO employee a
                    USING (SELECT ? AS ID FROM dual) b
                    ON (a.ID = b.ID)
                    WHEN MATCHED THEN
                        UPDATE SET ALARM_1 = ?, ALARM_2 = ?, ALARM_3 = ?
                    WHEN NOT MATCHED THEN
                        INSERT (ID, ALARM_1, ALARM_2, ALARM_3, IS_DELETED)
                        VALUES (?, ?, ?, ?, 0)
                """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, a1);
            pstmt.setInt(3, a2);
            pstmt.setInt(4, a3);
            pstmt.setInt(5, id);
            pstmt.setInt(6, a1);
            pstmt.setInt(7, a2);
            pstmt.setInt(8, a3);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
