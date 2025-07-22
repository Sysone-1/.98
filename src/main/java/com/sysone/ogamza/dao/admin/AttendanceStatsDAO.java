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
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '출근' THEN 1 ELSE 0 END) AS attendance_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '지각' THEN 1 ELSE 0 END) AS late_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '결근' THEN 1 ELSE 0 END) AS absent_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '휴가' THEN 1 ELSE 0 END) AS vacation_cnt
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            LEFT JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
                AND A.ATTENDANCE_DATE = TRUNC(SYSDATE)
            WHERE E.IS_DELETED = 0
                AND (? = '전체' OR D.NAME = ?)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departmentName);
            stmt.setString(2, departmentName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("total_cnt"),
                            rs.getInt("attendance_cnt"),    // 출근
                            rs.getInt("late_cnt"),          // 지각
                            rs.getInt("absent_cnt"),        // 결근
                            rs.getInt("vacation_cnt")       // 휴가 (데이터에는 없지만 로직 유지)
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("통계 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return new int[]{0, 0, 0, 0, 0};
    }

    /**
     * 주간 통계 조회 (추가된 기능)
     * @param departmentName 부서명
     * @return int[] 지난 7일간 통계
     */
    public int[] fetchWeeklyStatsByDept(String departmentName) {
        String sql = """
            SELECT
                COUNT(DISTINCT E.ID) AS total_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '출근' THEN 1 ELSE 0 END) AS attendance_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '지각' THEN 1 ELSE 0 END) AS late_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '결근' THEN 1 ELSE 0 END) AS absent_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '휴가' THEN 1 ELSE 0 END) AS vacation_cnt
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            LEFT JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 7
            WHERE E.IS_DELETED = 0
                AND (? = '전체' OR D.NAME = ?)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departmentName);
            stmt.setString(2, departmentName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("total_cnt"),
                            rs.getInt("attendance_cnt"),
                            rs.getInt("late_cnt"),
                            rs.getInt("absent_cnt"),
                            rs.getInt("vacation_cnt")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("주간 통계 조회 오류: " + e.getMessage());
        }

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

    /**
     * 특정 날짜의 통계 조회 (추가된 기능)
     * @param date 조회할 날짜 (YYYY-MM-DD 형식)
     * @param departmentName 부서명
     * @return int[] 해당 날짜의 통계
     */
    public int[] fetchStatsByDate(String date, String departmentName) {
        String sql = """
            SELECT
                COUNT(DISTINCT E.ID) AS total_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '출근' THEN 1 ELSE 0 END) AS attendance_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '지각' THEN 1 ELSE 0 END) AS late_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '결근' THEN 1 ELSE 0 END) AS absent_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '휴가' THEN 1 ELSE 0 END) AS vacation_cnt
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            LEFT JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
                AND A.ATTENDANCE_DATE = TO_DATE(?, 'YYYY-MM-DD')
            WHERE E.IS_DELETED = 0
                AND (? = '전체' OR D.NAME = ?)
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);
            stmt.setString(2, departmentName);
            stmt.setString(3, departmentName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("total_cnt"),
                            rs.getInt("attendance_cnt"),
                            rs.getInt("late_cnt"),
                            rs.getInt("absent_cnt"),
                            rs.getInt("vacation_cnt")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("날짜별 통계 조회 오류: " + e.getMessage());
        }

        return new int[]{0, 0, 0, 0, 0};
    }
}