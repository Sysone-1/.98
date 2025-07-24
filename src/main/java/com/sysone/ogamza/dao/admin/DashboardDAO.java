package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;
import com.sysone.ogamza.utils.db.OracleConnector;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.*;

/**
 * 대시보드 관련 데이터베이스 접근 객체(DAO) 클래스입니다.
 * 출결 현황, 초과근무 통계, 접근 차단 로그, 부서 목록 등을 조회하는 기능을 제공합니다.
 *
 * @author 조윤상
 * @since 2025-07-24
 */
public class DashboardDAO {

    /**
     * 출결 현황 데이터를 가져옵니다.
     * 해당 프로시저는 전체 인원, 출근 인원, 지각 인원, 결근 인원, 외근 인원, 휴가 인원을 반환합니다.
     *
     * @return 출결 현황 정보를 담은 {@link AttendanceStatusDTO} 객체
     */
    public AttendanceStatusDTO getAttendanceStatus() {
        String procedureCall = "{call sp_get_attendance_status( ?, ?, ?, ?, ?, ?)}";
        AttendanceStatusDTO dto = new AttendanceStatusDTO();

        try (
                Connection conn = OracleConnector.getConnection();
                CallableStatement cstmt = conn.prepareCall(procedureCall)) {

            cstmt.registerOutParameter(1, Types.NUMERIC); // p_total_employees
            cstmt.registerOutParameter(2, Types.NUMERIC); // p_present_count
            cstmt.registerOutParameter(3, Types.NUMERIC); // p_late_count
            cstmt.registerOutParameter(4, Types.NUMERIC); // p_absent_count
            cstmt.registerOutParameter(5, Types.NUMERIC); // p_trip_count
            cstmt.registerOutParameter(6, Types.NUMERIC); // p_vacation_count

            cstmt.execute();

            dto.setTotalEmployees(cstmt.getInt(1));
            dto.setPresentCount(cstmt.getInt(2));
            dto.setLateCount(cstmt.getInt(3));
            dto.setAbsentCount(cstmt.getInt(4));
            dto.setTripCount(cstmt.getInt(5));
            dto.setVacationCount(cstmt.getInt(6));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    /**
     * 부서별 주간 초과근무 통계 데이터를 가져옵니다.
     * 각 부서에 대해 주차별로 0~4시간, 4~8시간, 8~12시간, 12시간 초과 근무자 수를 포함한 데이터를 제공합니다.
     *
     * @return 부서명을 키로, 주차별 {@link OvertimeData} 리스트를 값으로 가지는 Map
     */
    public Map<String, List<OvertimeData>> getDepartmentOvertimeData() {
        Map<String, List<OvertimeData>> departmentData = new LinkedHashMap<>();
        String procedureCall = "{call sp_get_overtime_stats_by_dept(?)}";

        try (Connection conn = OracleConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(procedureCall)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs.next()) {
                    String departmentName = rs.getString("name");
                    String weekLabel = rs.getString("week_label");
                    int r0 = rs.getInt("range0to4");
                    int r4 = rs.getInt("range4to8");
                    int r8 = rs.getInt("range8to12");
                    int r12 = rs.getInt("range12plus");

                    OvertimeData data = new OvertimeData(weekLabel, r0, r4, r8, r12);
                    departmentData.computeIfAbsent(departmentName, k -> new ArrayList<>()).add(data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return departmentData;
        }

        return departmentData;
    }

    /**
     * 데이터베이스에서 모든 부서명을 조회합니다.
     *
     * @return 부서 이름 목록
     */
    public List<String> getAllDepartmentNames() {
        List<String> departmentNames = new ArrayList<>();
        String sql = "SELECT name FROM department ORDER BY name";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                departmentNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departmentNames;
    }

    /**
     * 주간 단위의 접근 차단 로그 횟수를 조회합니다.
     * 각 주차별로 접근 차단이 발생한 횟수를 반환합니다.
     *
     * @return 주차 라벨(예: "2025년 7월 3주차")을 키로, 해당 주차의 차단 횟수를 값으로 가지는 Map
     */
    public Map<String, Integer> getDeniedAccessLogWeekly() {
        Map<String, Integer> weeklyDeniedCounts = new LinkedHashMap<>();
        String procedureCall = "{call sp_get_denied_access_log_weekly(?)}";

        try (Connection conn = OracleConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(procedureCall)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs.next()) {
                    String weekLabel = rs.getString("week_label");
                    int count = rs.getInt("count");
                    weeklyDeniedCounts.put(weekLabel, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return weeklyDeniedCounts;
    }
}
