package com.sysone.ogamza.dao.admin;


import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;
import com.sysone.ogamza.utils.db.OracleConnector;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.*;

public class DashboardDAO {

    public AttendanceStatusDTO getAttendanceStatus() {
        String procedureCall = "{call sp_get_attendance_status( ?, ?, ?, ?, ?, ?)}";
        AttendanceStatusDTO dto = new AttendanceStatusDTO();

        // try-with-resources로 DB 연결 및 자동 리소스 해제
        try (
                Connection conn = OracleConnector.getConnection();
                CallableStatement cstmt = conn.prepareCall(procedureCall)) {

            // OUT 파라미터 등록
            cstmt.registerOutParameter(1, Types.NUMERIC); // p_total_employees
            cstmt.registerOutParameter(2, Types.NUMERIC); // p_present_count
            cstmt.registerOutParameter(3, Types.NUMERIC); // p_late_count
            cstmt.registerOutParameter(4, Types.NUMERIC); // p_absent_count
            cstmt.registerOutParameter(5, Types.NUMERIC); // p_trip_count
            cstmt.registerOutParameter(6, Types.NUMERIC); // p_vacation_count

            // 프로시저 실행
            cstmt.execute();

            // 결과 DTO에 매핑
            dto.setTotalEmployees(cstmt.getInt(1));
            dto.setPresentCount(cstmt.getInt(2));
            dto.setLateCount(cstmt.getInt(3));
            dto.setAbsentCount(cstmt.getInt(4));
            dto.setTripCount(cstmt.getInt(5));
            dto.setVacationCount(cstmt.getInt(6));

        } catch (
                SQLException e) {
            e.printStackTrace();

        }
        return dto;
    }



    public Map<String, List<OvertimeData>> getDepartmentOvertimeData() {
        // 최종적으로 반환할 부서별 초과 근무 데이터 맵 (순서 보장을 위해 LinkedHashMap 사용)
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

                    // 해당 부서의 리스트가 없으면 새로 만들고, 데이터 추가
                    departmentData.computeIfAbsent(departmentName, k -> new ArrayList<>()).add(data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 오류 발생 시에도 현재까지 처리된 데이터라도 반환하도록 함
            return departmentData;
        }

        return departmentData;
    }

    /**
     * 데이터베이스에서 모든 부서의 이름을 가져옵니다.
     * @return 모든 부서 이름의 리스트
     */
    public List<String> getAllDepartmentNames() {
        List<String> departmentNames = new ArrayList<>();
        String sql = "SELECT name FROM department ORDER BY name"; // department 테이블의 부서명 컬럼이 'name'이라고 가정

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                departmentNames.add(rs.getString("name")); // 컬럼 이름이 'name'인지 'department_name'인지 확인 필요
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departmentNames;
    }
}