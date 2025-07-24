package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.dto.admin.ClockChangeRequestDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClockChangeRequestDAO implements RequestDAO<ClockChangeRequestDTO> {

    // 연장 근무와 휴일을 출퇴근 변경신청으로 매핑
    private static final String[] REQUEST_TYPES = {"연장 근무", "휴일"};

    @Override
    public int countPendingRequests() {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE SCHEDULE_TYPE IN ('연장 근무', '휴일')
            AND IS_GRANTED = 0
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<ClockChangeRequestDTO> getPendingRequests() {
        List<ClockChangeRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'HH24:MI') AS original_time,
                TO_CHAR(S.END_DATE,'HH24:MI') AS requested_time,
                S.SCHEDULE_TYPE AS schedule_type,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE IN ('연장 근무', '휴일')
            AND S.IS_GRANTED = 0
            ORDER BY S.ID DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClockChangeRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("original_time"),
                            rs.getString("requested_time"),
                            rs.getString("schedule_type"), // DB 실제값 전달
                            rs.getString("content") != null ? rs.getString("content") : "",
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
            WHERE SCHEDULE_TYPE IN ('연장 근무', '휴일')
            AND IS_GRANTED IN (1,2)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<ClockChangeRequestDTO> getCompletedRequests() {
        List<ClockChangeRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'HH24:MI') AS original_time,
                TO_CHAR(S.END_DATE,'HH24:MI') AS requested_time,
                S.SCHEDULE_TYPE AS schedule_type,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE IN ('연장 근무', '휴일')
            AND S.IS_GRANTED IN (1,2)
            ORDER BY S.APPROVAL_DATE DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClockChangeRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("original_time"),
                            rs.getString("requested_time"),
                            rs.getString("schedule_type"), // DB 실제값 전달
                            rs.getString("content") != null ? rs.getString("content") : "",
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
                SET
                  IS_GRANTED = DECODE(?, '1', 1, '2', 2, IS_GRANTED),
                  APPROVAL_DATE = SYSDATE
                WHERE
                  ID = ?
                  AND SCHEDULE_TYPE IN ('연장 근무', '휴일')
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);

            int updated = stmt.executeUpdate();
            System.out.println("출퇴근 변경 승인 상태 업데이트: " + updated + "건 처리됨 (ID: " + requestId + ", 상태: " + newStatus + ")");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("출퇴근 변경 승인 상태 업데이트 실패", e);
        }
    }
}
