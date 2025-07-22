package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.dto.admin.OutworkRequestDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OutworkRequestDAO implements RequestDAO {

    // 외근을 출장으로 매핑
    private static final String REQUEST_TYPE = "외근";

    @Override
    public int countPendingRequests() {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE SCHEDULE_TYPE = ?
            AND IS_GRANTED = 0
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, REQUEST_TYPE);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<OutworkRequestDTO> getPendingRequests() {
        List<OutworkRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'YYYY-MM-DD HH24:MI') AS start_time,
                TO_CHAR(S.END_DATE,'YYYY-MM-DD HH24:MI') AS end_time,
                S.TITLE AS location,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE = ?
            AND S.IS_GRANTED = 0
            ORDER BY S.START_DATE DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, REQUEST_TYPE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OutworkRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getString("location"),
                            rs.getString("content"),
                            rs.getInt("is_granted")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countCompletedRequests() {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE SCHEDULE_TYPE = ?
            AND IS_GRANTED IN (1,2)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, REQUEST_TYPE);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<OutworkRequestDTO> getCompletedRequests() {
        List<OutworkRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'YYYY-MM-DD HH24:MI') AS start_time,
                TO_CHAR(S.END_DATE,'YYYY-MM-DD HH24:MI') AS end_time,
                S.TITLE AS location,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE = ?
            AND S.IS_GRANTED IN (1,2)
            ORDER BY S.APPROVAL_DATE DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, REQUEST_TYPE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OutworkRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getString("location"),
                            rs.getString("content"),
                            rs.getInt("is_granted")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateRequestStatus(int requestId, String newStatus) {
        String sql = """
            UPDATE SCHEDULE
            SET IS_GRANTED = CASE 
                WHEN ? = '승인' THEN 1
                WHEN ? = '거절' THEN 2
                ELSE IS_GRANTED
            END,
            APPROVAL_DATE = SYSDATE
            WHERE ID = ?
            AND SCHEDULE_TYPE = ?
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, newStatus);
            stmt.setInt(3, requestId);
            stmt.setString(4, REQUEST_TYPE);

            int updated = stmt.executeUpdate();
            System.out.println("출장 승인 상태 업데이트: " + updated + "건 처리됨");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("출장 승인 상태 업데이트 실패", e);
        }
    }
}
