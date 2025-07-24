package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.dto.admin.OutworkRequestDTO;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for handling “외근”(Field Work / Business Trip) approvals.
 * Mirrors the behaviour of VacationRequestDAO so that the service layer
 * can treat every request type uniformly.
 */
public class OutworkRequestDAO implements RequestDAO {

    /** Fixed SCHEDULE_TYPE value stored in DB for external work. */
    private static final String REQUEST_TYPE = "외근";

    /* ------------------------------------------------------- *
     * 1. PENDING COUNTERS
     * ------------------------------------------------------- */
    @Override
    public int countPendingRequests() {
        final String sql = """
            SELECT COUNT(*)
            FROM   SCHEDULE
            WHERE  SCHEDULE_TYPE = ?
              AND  IS_GRANTED     = 0
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, REQUEST_TYPE);              // ★ 필수 바인딩
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[OutworkDAO] countPendingRequests 실패: " + e.getMessage());
            return 0;
        }
    }

    /* ------------------------------------------------------- *
     * 2. PENDING LIST
     * ------------------------------------------------------- */
    @Override
    public List<OutworkRequestDTO> getPendingRequests() {
        final String sql = """
        SELECT
            S.ID              AS request_id,
            S.EMPLOYEE_ID     AS employee_id,
            E.NAME            AS employee_name,
            D.NAME            AS department,
            E.POSITION        AS position,
            TO_CHAR(S.START_DATE,   'YYYY-MM-DD HH24:MI') AS start_time,
            TO_CHAR(S.END_DATE,     'YYYY-MM-DD HH24:MI') AS end_time,
            S.TITLE           AS location,
            S.CONTENT         AS content,
            S.IS_GRANTED      AS is_granted
        FROM   SCHEDULE S
        JOIN   EMPLOYEE   E ON S.EMPLOYEE_ID = E.ID
        JOIN   DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
        WHERE  S.SCHEDULE_TYPE = ?
          AND  S.IS_GRANTED    = 0
        ORDER BY S.START_DATE DESC
        """;

        List<OutworkRequestDTO> list = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // ★ 필수 바인딩 추가
            ps.setString(1, REQUEST_TYPE);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OutworkRequestDTO(
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department"),
                            rs.getString("position"),
                            null,                       // approval_date is null while pending
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getString("location"),
                            rs.getString("content"),
                            rs.getInt("is_granted")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[OutworkDAO] getPendingRequests 실패: " + e.getMessage());
        }
        return list;
    }


    /* ------------------------------------------------------- *
     * 3. COMPLETED COUNTERS
     * ------------------------------------------------------- */
    @Override
    public int countCompletedRequests() {
        final String sql = """
            SELECT COUNT(*)
            FROM   SCHEDULE
            WHERE  SCHEDULE_TYPE = ?
              AND  IS_GRANTED     IN (1,2)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, REQUEST_TYPE);              // ★ 필수 바인딩
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("[OutworkDAO] countCompletedRequests 실패: " + e.getMessage());
            return 0;
        }
    }

    /* ------------------------------------------------------- *
     * 4. COMPLETED LIST
     * ------------------------------------------------------- */
    @Override
    public List<OutworkRequestDTO> getCompletedRequests() {
        final String sql = """
            SELECT
                S.ID              AS request_id,
                S.EMPLOYEE_ID     AS employee_id,
                E.NAME            AS employee_name,
                D.NAME            AS department,
                E.POSITION        AS position,
                TO_CHAR(S.APPROVAL_DATE,'YYYY-MM-DD')       AS approval_date,
                TO_CHAR(S.START_DATE,   'YYYY-MM-DD HH24:MI') AS start_time,
                TO_CHAR(S.END_DATE,     'YYYY-MM-DD HH24:MI') AS end_time,
                S.TITLE           AS location,
                S.CONTENT         AS content,
                S.IS_GRANTED      AS is_granted
            FROM   SCHEDULE S
            JOIN   EMPLOYEE   E ON S.EMPLOYEE_ID = E.ID
            JOIN   DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE  S.SCHEDULE_TYPE = ?
              AND  S.IS_GRANTED     IN (1,2)
            ORDER BY S.APPROVAL_DATE DESC
            """;

        List<OutworkRequestDTO> list = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, REQUEST_TYPE);              // ★ 필수 바인딩
            try (ResultSet rs = ps.executeQuery()) {
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
            System.err.println("[OutworkDAO] getCompletedRequests 실패: " + e.getMessage());
        }
        return list;
    }

    /* ------------------------------------------------------- *
     * 5. APPROVAL / REJECTION UPDATE
     * ------------------------------------------------------- */
    @Override
    public void updateRequestStatus(int requestId, String newStatus) {
        final String sql = """
                UPDATE SCHEDULE
                SET    IS_GRANTED    = DECODE(?, '1', 1, '2', 2, IS_GRANTED),
                       APPROVAL_DATE = SYSDATE
                WHERE  ID            = ?
                  AND  SCHEDULE_TYPE  = ?
                """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // conn.setAutoCommit(true); // (기본값)
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            ps.setString(3, REQUEST_TYPE);

            int rows = ps.executeUpdate();           // 변경 즉시 커밋됨
            System.out.printf("[OutworkDAO] 외근 요청 %d건 상태변경 완료%n", rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
