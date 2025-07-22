package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.dto.admin.VacationRequestDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VacationRequestDAO implements RequestDAO {

    // 휴일, 연차, 반차를 모두 휴가로 통합 처리
    private static final String[] REQUEST_TYPES = {"휴일", "연차", "반차"};

    @Override
    public int countPendingRequests() {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE SCHEDULE_TYPE IN ('휴일', '연차', '반차')
            AND IS_GRANTED = 0
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<VacationRequestDTO> getPendingRequests() {
        List<VacationRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'YYYY-MM-DD') AS start_date,
                TO_CHAR(S.END_DATE,'YYYY-MM-DD') AS end_date,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted,
                S.SCHEDULE_TYPE AS original_type
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE IN ('휴일', '연차', '반차')
            AND S.IS_GRANTED = 0
            ORDER BY S.START_DATE DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 원본 타입을 content에 포함하여 구분 가능하도록 함
                    String content = "[" + rs.getString("original_type") + "] " +
                            (rs.getString("content") != null ? rs.getString("content") : "");

                    list.add(new VacationRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            content,
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
            WHERE SCHEDULE_TYPE IN ('휴일', '연차', '반차')
            AND IS_GRANTED IN (1, 2)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<VacationRequestDTO> getCompletedRequests() {
        List<VacationRequestDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                S.ID AS request_id,
                S.EMPLOYEE_ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department,
                E.POSITION AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD') AS approval_date,
                TO_CHAR(S.START_DATE,'YYYY-MM-DD') AS start_date,
                TO_CHAR(S.END_DATE,'YYYY-MM-DD') AS end_date,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted,
                S.SCHEDULE_TYPE AS original_type
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.SCHEDULE_TYPE IN ('휴일', '연차', '반차')
            AND S.IS_GRANTED IN (1, 2)
            ORDER BY S.APPROVAL_DATE DESC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String content = "[" + rs.getString("original_type") + "] " +
                            (rs.getString("content") != null ? rs.getString("content") : "");

                    list.add(new VacationRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            rs.getString("approval_date"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            content,
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
            AND SCHEDULE_TYPE IN ('휴일', '연차', '반차')
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, newStatus);
            stmt.setInt(3, requestId);

            int updated = stmt.executeUpdate();
            System.out.println("휴가 승인 상태 업데이트: " + updated + "건 처리됨");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("휴가 승인 상태 업데이트 실패", e);
        }
    }
}
