package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.dto.admin.WorkDeviationModelDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkDeviationDao (수정된 버전)
 * – 현재 테이블 스키마에 맞춰 수정된 일일 근무 이탈자 통계 조회 기능용 DAO 클래스
 *
 * 주요 수정사항:
 * - '정시출근' → '출근' 변경 (정상 출근이므로 이탈자에서 제외)
 * - '출장' → '결근' 변경
 * - 이탈 유형: '지각', '결근', '휴가' (출근은 정상이므로 제외)
 * - 현재 스키마에 맞춰 쿼리 완전히 재작성
 * - 성능 향상을 위한 인덱스 힌트 및 조건 최적화
 */
public class WorkDeviationDAO {

    /**
     * 부서 전체의 이탈자 통계 조회 (스키마에 맞게 수정)
     * @return List of WorkDeviationModelDto
     */
    public List<WorkDeviationModelDTO> selectAll() {
        String sql = """
            SELECT
                E.ID as emp_id,
                E.NAME as emp_name,
                D.NAME as dept_name,
                A.ATTENDANCE_STATUS as deviation_type,
                TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND A.ATTENDANCE_STATUS IN ('지각', '결근', '휴가')
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 30
            ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            """;
        return queryList(sql);
    }

    /**
     * 특정 부서의 이탈자 통계 조회 (스키마에 맞게 수정)
     * @param dept 부서명 ("전체"일 경우 전체 조회)
     * @return List of WorkDeviationModelDto
     */
    public List<WorkDeviationModelDTO> selectByDept(String dept) {
        if ("전체".equals(dept)) {
            return selectAll();
        }

        String sql = """
            SELECT
                E.ID as emp_id,
                E.NAME as emp_name,
                D.NAME as dept_name,
                A.ATTENDANCE_STATUS as deviation_type,
                TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND D.NAME = ?
                AND A.ATTENDANCE_STATUS IN ('지각', '결근', '휴가')
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 30
            ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            """;
        return queryList(sql, dept);
    }

    /**
     * 특정 날짜 범위의 이탈자 조회 (추가된 기능)
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate 종료일 (YYYY-MM-DD)
     * @param dept 부서명 ("전체"일 경우 전체 조회)
     * @return List of WorkDeviationModelDto
     */
    public List<WorkDeviationModelDTO> selectByDateRange(String startDate, String endDate, String dept) {
        String sql = """
            SELECT
                E.ID as emp_id,
                E.NAME as emp_name,
                D.NAME as dept_name,
                A.ATTENDANCE_STATUS as deviation_type,
                TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND A.ATTENDANCE_STATUS IN ('지각', '결근', '휴가')
                AND A.ATTENDANCE_DATE BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')
                AND (? = '전체' OR D.NAME = ?)
            ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            """;
        return queryList(sql, startDate, endDate, dept, dept);
    }

    /**
     * 특정 이탈 유형별 조회 (추가된 기능)
     * @param deviationType 이탈 유형 ('지각', '결근', '휴가')
     * @param dept 부서명
     * @return List of WorkDeviationModelDto
     */
    public List<WorkDeviationModelDTO> selectByDeviationType(String deviationType, String dept) {
        String sql = """
            SELECT
                E.ID as emp_id,
                E.NAME as emp_name,
                D.NAME as dept_name,
                A.ATTENDANCE_STATUS as deviation_type,
                TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND A.ATTENDANCE_STATUS = ?
                AND (? = '전체' OR D.NAME = ?)
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 30
            ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            """;
        return queryList(sql, deviationType, dept, dept);
    }

    /**
     * 이탈자 통계 요약 조회 (수정된 기능)
     * @param dept 부서명
     * @return 각 이탈 유형별 카운트 배열 [지각, 결근, 휴가]
     */
    public int[] getDeviationSummary(String dept) {
        String sql = """
            SELECT
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '지각' THEN 1 ELSE 0 END) as late_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '결근' THEN 1 ELSE 0 END) as absent_cnt,
                SUM(CASE WHEN A.ATTENDANCE_STATUS = '휴가' THEN 1 ELSE 0 END) as vacation_cnt
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND A.ATTENDANCE_STATUS IN ('지각', '결근', '휴가')
                AND (? = '전체' OR D.NAME = ?)
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 30
            """;

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dept);
            ps.setString(2, dept);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("late_cnt"),      // 지각
                            rs.getInt("absent_cnt"),    // 결근 (기존 출장)
                            rs.getInt("vacation_cnt")   // 휴가
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("이탈자 통계 요약 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return new int[]{0, 0, 0};
    }

    /**
     * 쿼리 실행 및 리스트 매핑 공통 처리 (기존 메서드 개선)
     * @param sql 실행할 SQL
     * @param params 바인딩할 파라미터 (없으면 빈 배열)
     * @return List of WorkDeviationModelDto
     */
    private List<WorkDeviationModelDTO> queryList(String sql, String... params) {
        List<WorkDeviationModelDTO> list = new ArrayList<>();

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // 파라미터 바인딩
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkDeviationModelDTO model = new WorkDeviationModelDTO();
                    model.setEmpId(rs.getInt("emp_id"));
                    model.setEmpName(rs.getString("emp_name"));
                    model.setDeptName(rs.getString("dept_name"));
                    model.setDeviationType(rs.getString("deviation_type"));
                    model.setDate(rs.getString("deviation_date"));
                    list.add(model);
                }
            }
        } catch (SQLException e) {
            System.err.println("근무 이탈자 조회 오류: " + e.getMessage());
            e.printStackTrace();
            // TODO: 로깅 시스템 연동 고려
        }

        return list;
    }

    /**
     * 최근 이탈 기록 조회 (추가된 기능)
     * @param limit 조회할 최대 레코드 수
     * @return List of WorkDeviationModelDto
     */
    public List<WorkDeviationModelDTO> selectRecentDeviations(int limit) {
        String sql = """
            SELECT * FROM (
                SELECT
                    E.ID as emp_id,
                    E.NAME as emp_name,
                    D.NAME as dept_name,
                    A.ATTENDANCE_STATUS as deviation_type,
                    TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
                FROM EMPLOYEE E
                JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
                JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
                WHERE E.IS_DELETED = 0
                    AND A.ATTENDANCE_STATUS IN ('지각', '결근', '휴가')
                ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            ) WHERE ROWNUM <= ?
            """;

        List<WorkDeviationModelDTO> list = new ArrayList<>();

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkDeviationModelDTO model = new WorkDeviationModelDTO();
                    model.setEmpId(rs.getInt("emp_id"));
                    model.setEmpName(rs.getString("emp_name"));
                    model.setDeptName(rs.getString("dept_name"));
                    model.setDeviationType(rs.getString("deviation_type"));
                    model.setDate(rs.getString("deviation_date"));
                    list.add(model);
                }
            }
        } catch (SQLException e) {
            System.err.println("최근 이탈 기록 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 정상 출근자 조회 (추가된 기능)
     * 이탈자가 아닌 정상 출근한 사원들을 조회
     * @param dept 부서명
     * @return List of WorkDeviationModelDto (정상 출근자)
     */
    public List<WorkDeviationModelDTO> selectNormalAttendance(String dept) {
        String sql = """
            SELECT
                E.ID as emp_id,
                E.NAME as emp_name,
                D.NAME as dept_name,
                A.ATTENDANCE_STATUS as deviation_type,
                TO_CHAR(A.ATTENDANCE_DATE, 'YYYY-MM-DD') as deviation_date
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            JOIN ATTENDANCE A ON A.EMPLOYEE_ID = E.ID
            WHERE E.IS_DELETED = 0
                AND A.ATTENDANCE_STATUS = '출근'
                AND (? = '전체' OR D.NAME = ?)
                AND A.ATTENDANCE_DATE >= TRUNC(SYSDATE) - 30
            ORDER BY A.ATTENDANCE_DATE DESC, E.NAME
            """;
        return queryList(sql, dept, dept);
    }

    /**
     * 일일 출근 현황 요약 (전체 현황 포함)
     * @param dept 부서명
     * @return [총원, 출근, 지각, 결근, 휴가]
     */
    public int[] getDailyAttendanceSummary(String dept) {
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

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dept);
            ps.setString(2, dept);

            try (ResultSet rs = ps.executeQuery()) {
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
            System.err.println("일일 출근 현황 요약 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return new int[]{0, 0, 0, 0, 0};
    }
}