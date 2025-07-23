package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.dto.admin.BaseRequestDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRequestDAO {

    /**
     * 승인·기각된 전체 건수 조회 (모든 SCHEDULE_TYPE)
     */
    public int getCompletedCount() {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE IS_GRANTED IN (1, 2)
            AND SCHEDULE_TYPE IN ('연장 근무', '휴일', '연차', '반차', '외근')
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 승인·기각된 모든 요청 목록 조회 (결재 완료 창용)
     * DB 타입을 화면 표시용으로 매핑
     */
    public List<BaseRequestDTO> getAllCompletedList() {
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
                S.SCHEDULE_TYPE AS original_type,
                S.TITLE AS title,
                S.CONTENT AS content,
                S.IS_GRANTED AS is_granted
            FROM SCHEDULE S
            JOIN EMPLOYEE E ON S.EMPLOYEE_ID = E.ID
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE S.IS_GRANTED IN (1, 2)
            AND S.SCHEDULE_TYPE IN ('연장 근무', '휴일', '연차', '반차', '외근')
            ORDER BY S.APPROVAL_DATE DESC
            """;

        List<BaseRequestDTO> list = new ArrayList<>();
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // DB 값을 화면 표시용으로 매핑
                String originalType = rs.getString("original_type");
                String displayType = mapToDisplayType(originalType);

                // 원본 타입 정보를 content에 포함
                String content = "[" + originalType + "] " +
                        (rs.getString("content") != null ? rs.getString("content") : "");

                list.add(new BaseRequestDTO(
                        rs.getInt("request_id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getString("approval_date"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        displayType, // 매핑된 표시 타입
                        rs.getString("title"),
                        content, // 원본 타입 포함한 내용
                        rs.getInt("is_granted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * DB SCHEDULE_TYPE을 화면 표시용으로 매핑
     */
    private String mapToDisplayType(String dbType) {
        return switch (dbType) {
            case "연장 근무" -> "출퇴근 변경신청";
            case "휴일", "연차", "반차" -> "휴가";
            case "외근" -> "출장";
            default -> dbType; // 알 수 없는 타입은 그대로 표시
        };
    }

    /**
     * 특정 SCHEDULE_TYPE의 대기중 건수를 조회
     */
    public int getPendingCount(String requestType) {
        String sql = """
            SELECT COUNT(*)
            FROM SCHEDULE
            WHERE SCHEDULE_TYPE = ?
            AND IS_GRANTED = 0
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, requestType);
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

    /**
     * 요청 상태를 업데이트 (승인 or 기각)
     */
    public void updateStatus(int requestId, String newStatus) {
        String sql = """
            UPDATE SCHEDULE
            SET IS_GRANTED = CASE 
                WHEN ? = '승인' THEN 1
                WHEN ? = '거절' THEN 2
                ELSE IS_GRANTED
            END,
            APPROVAL_DATE = SYSDATE
            WHERE ID = ?
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, newStatus);
            stmt.setInt(3, requestId);

            int updated = stmt.executeUpdate();
            System.out.println("통합 승인 상태 업데이트: " + updated + "건 처리됨");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("승인 상태 업데이트 실패", e);
        }
    }
}
