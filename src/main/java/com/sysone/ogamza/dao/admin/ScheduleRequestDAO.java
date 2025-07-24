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
     * DB 실제 타입을 그대로 표시하도록 수정
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
                S.SCHEDULE_TYPE AS schedule_type,
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
                String scheduleType = rs.getString("schedule_type");
                String displayType = mapToDisplayType(scheduleType);

                list.add(new BaseRequestDTO(
                        rs.getInt("request_id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getString("approval_date"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        displayType, // 화면 표시용 매핑된 타입
                        scheduleType, // DB 실제값 (새로 추가)
                        rs.getString("title") != null ? rs.getString("title") : "",
                        rs.getString("content") != null ? rs.getString("content") : "",
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
            case "연장 근무" -> "출퇴근변경";
            case "휴일", "연차", "반차" -> "휴가";
            case "외근" -> "출장";
            default -> dbType; // 알 수 없는 타입은 그대로 표시
        };
    }

    /**
     * 특정 SCHEDULE_TYPE들의 대기중 건수를 조회 (배열 지원)
     */
    public int getPendingCountByTypes(String[] scheduleTypes) {
        if (scheduleTypes == null || scheduleTypes.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM SCHEDULE WHERE SCHEDULE_TYPE IN (");
        for (int i = 0; i < scheduleTypes.length; i++) {
            sql.append("?");
            if (i < scheduleTypes.length - 1) {
                sql.append(",");
            }
        }
        sql.append(") AND IS_GRANTED = 0");

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < scheduleTypes.length; i++) {
                stmt.setString(i + 1, scheduleTypes[i]);
            }

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
     * 요청 상태를 업데이트 (승인 or 거절)
     * 트랜잭션 처리 추가
     */
    public boolean updateStatus(int requestId, String newStatus) {
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

        Connection conn = null;
        try {
            conn = OracleConnector.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setString(2, newStatus);
                stmt.setInt(3, requestId);

                int updated = stmt.executeUpdate();

                if (updated > 0) {
                    conn.commit(); // 성공시 커밋
                    System.out.println("통합 승인 상태 업데이트 성공: " + updated + "건 처리됨 (ID: " + requestId + ", 상태: " + newStatus + ")");
                    return true;
                } else {
                    conn.rollback(); // 실패시 롤백
                    System.out.println("업데이트할 레코드가 없습니다. (ID: " + requestId + ")");
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("승인 상태 업데이트 실패", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 자동 커밋 복원
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
