package com.sysone.ogamza.dao;

import com.sysone.ogamza.entity.Record;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 출입 기록 데이터베이스 접근을 담당하는 DAO 클래스입니다.
 * <p>
 * Oracle 데이터베이스에서 출입 로그와 관련된 정보를 조회하는 기능을 제공합니다.
 * </p>
 *
 * 작성자: 조윤상
 */
public class RecordDao {

    /**
     * 데이터베이스에서 모든 출입 기록을 조회합니다.
     * <p>
     * access_log 테이블을 기준으로 employee, department 테이블과 LEFT JOIN 하여
     * 출입 시간, 직원 정보, 부서명, 직급, 출입 승인 여부 등을 조회합니다.
     * </p>
     *
     * @return 출입 기록 리스트(List of {@link Record})
     * @throws SQLException DB 접근 중 오류가 발생한 경우 던져집니다.
     */
    public List<Record> findAllRecords() throws SQLException {
        List<Record> records = new ArrayList<>();

        String sql = """
                SELECT
                    NVL(e.id, -1) AS emp_id,
                    NVL(e.name, '미등록') AS employee_name,
                    NVL(d.name, '미등록') AS department_name,
                    NVL(e.position, '미등록') AS position,
                    a.access_time AS tagging_time,
                    CASE
                        WHEN e.id IS NULL THEN '출입 거부'
                        ELSE '출입'
                    END AS approval_status
                FROM access_log a
                LEFT JOIN employee e ON a.employee_id = e.id
                LEFT JOIN department d ON e.department_id = d.id
                ORDER BY a.access_time DESC
                """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Record r = new Record(
                            rs.getLong("emp_id"),
                            rs.getString("employee_name"),
                            rs.getString("department_name"),
                            rs.getString("position"),
                            rs.getObject("tagging_time", LocalDateTime.class),
                            rs.getString("approval_status")
                    );
                    records.add(r);
                } catch (Exception e) {
                    // 데이터 처리 중 개별 행 오류 발생 시 로그만 남기고 계속 진행
                    System.err.println("Error processing a row: " + e.getMessage());
                    System.err.println("Failed row data: emp_id=" + rs.getObject("emp_id") + "," +
                            "employee_name = " + rs.getObject("employee_name"));
                }
            }
        }
        return records;
    }
}
