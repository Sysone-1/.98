package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceStatsDao (수정된 버전)
 * – 현재 테이블 스키마에 맞춰 수정된 총원·출근·지각·결근·휴가 인원 조회 전용 DAO
 *
 * 주요 수정사항:
 * - '정시출근' → '출근' 변경
 * - '출장' → '결근' 변경
 * - 휴가 로직은 유지 (데이터에는 없지만 로직상 유지)
 * - 현재 스키마(EMPLOYEE, DEPARTMENT, ATTENDANCE)에 맞춤
 */
public class AttendanceStatsDAO {

    /**
     * 전체 당일 통계 조회
     */
    public int[] fetchTodayStats() {
        return fetchTodayStatsByDept("전체");
    }

    /**
     * 부서별 당일 통계 조회 (스키마에 맞게 수정)
     * @param departmentName 부서명 ("전체"일 경우 전체 조회)
     * @return int[] {총원, 출근, 지각, 결근, 휴가}
     */
    public int[] fetchTodayStatsByDept(String departmentName) {
        String sql = """
            SELECT
                COUNT(DISTINCT E.ID) AS total_cnt,
                SUM(
                  CASE
                    WHEN sch.emp_id IS NOT NULL THEN 0
                    WHEN t.first_access IS NOT NULL
                     AND t.first_access < TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD')||'090000','YYYYMMDDHH24MISS')
                      THEN 1 ELSE 0
                  END
                ) AS attendance_cnt,  -- 09:00 이전 출근
                SUM(
                  CASE
                    WHEN sch.emp_id IS NOT NULL THEN 0
                    WHEN t.first_access IS NOT NULL
                     AND t.first_access >= TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD')||'090000','YYYYMMDDHH24MISS')
                     AND t.first_access < TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD')||'140000','YYYYMMDDHH24MISS')
                      THEN 1 ELSE 0
                  END
                ) AS late_cnt,        -- 09~14시 지각
                SUM(
                  CASE
                    WHEN sch.emp_id IS NOT NULL THEN 0
                    WHEN t.first_access IS NULL
                     OR t.first_access >= TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD')||'140000','YYYYMMDDHH24MISS')
                      THEN 1 ELSE 0
                  END
                ) AS absent_cnt,      -- 결근
                SUM(
                  CASE
                    WHEN sch.emp_id IS NOT NULL THEN 1 ELSE 0
                  END
                ) AS vacation_cnt     -- 휴가
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            -- 당일 휴가 여부 확인
            LEFT JOIN (
                SELECT EMPLOYEE_ID AS emp_id
                FROM SCHEDULE
                WHERE TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE)
                GROUP BY EMPLOYEE_ID
            ) sch ON E.ID = sch.emp_id
            -- 당일 최초 태깅 시각
            LEFT JOIN (
                SELECT EMPLOYEE_ID,
                       MIN(ACCESS_TIME) AS first_access
                FROM ACCESS_LOG
                WHERE TRUNC(ACCESS_TIME) = TRUNC(SYSDATE)
                GROUP BY EMPLOYEE_ID
            ) t ON E.ID = t.EMPLOYEE_ID
            WHERE E.IS_DELETED = 0
              AND (? = '전체' OR D.NAME = ?)
        """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 바인드 변수 설정: 두 개의 ? 모두 부서명으로 바인딩
            stmt.setString(1, departmentName);
            stmt.setString(2, departmentName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total      = rs.getInt("total_cnt");
                    int onTime     = rs.getInt("attendance_cnt");
                    int late       = rs.getInt("late_cnt");
                    int absent     = rs.getInt("absent_cnt");
                    int vacation   = rs.getInt("vacation_cnt");

                    // 디버그 로그 (원할 경우 활성화)
                    System.out.printf("fetchTodayStatsByDept [%s] → total=%d, onTime=%d, late=%d, absent=%d, vacation=%d%n",
                            departmentName, total, onTime, late, absent, vacation);

                    return new int[]{ total, onTime, late, absent, vacation };
                }
            }
        } catch (SQLException e) {
            System.err.println("통계 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }

        // 기본값 반환
        return new int[]{0, 0, 0, 0, 0};
    }


    /**
     * 모든 부서 목록 조회 (기존과 동일)
     */
    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        departments.add("전체");
        String sql = "SELECT DISTINCT NAME FROM DEPARTMENT ORDER BY NAME";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                departments.add(rs.getString("NAME"));
            }
        } catch (SQLException e) {
            System.err.println("부서 목록 조회 오류: " + e.getMessage());
        }

        return departments;
    }
}