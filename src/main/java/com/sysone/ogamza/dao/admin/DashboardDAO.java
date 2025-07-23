package com.sysone.ogamza.dao.admin;


import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;
import com.sysone.ogamza.utils.db.OracleConnector;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
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
        // 최종적으로 반환할 부서별 초과 근무 데이터 맵
        Map<String, List<OvertimeData>> finalDepartmentData = new LinkedHashMap<>();

        // DB에서 가져온 원본 데이터를 임시로 저장할 맵 (부서명 -> 주차 식별자 -> OvertimeData)
        Map<String, Map<String, OvertimeData>> rawDepartmentData = new HashMap<>();

        String procedureCall = "{call sp_get_overtime_stats_by_dept(?)}";

        try (Connection conn = OracleConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(procedureCall)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs.next()) {
                    String departmentName = rs.getString("name");
                    String weekLabel = rs.getString("week_label"); // 예: "2024-30"
                    int r0 = rs.getInt("range0to4");
                    int r4 = rs.getInt("range4to8");
                    int r8 = rs.getInt("range8to12");
                    int r12 = rs.getInt("range12plus");

                    OvertimeData data = new OvertimeData(weekLabel, r0, r4, r8, r12);

                    // rawDepartmentData 맵에 데이터 추가
                    rawDepartmentData
                            .computeIfAbsent(departmentName, k -> new HashMap<>())
                            .put(weekLabel, data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 오류 발생 시 빈 맵을 반환
            return finalDepartmentData;
        }

        // --- 누락된 주차 데이터 채우기 로직 ---

        // 최근 4주차의 ISO 주차 식별자 (예: "2024-30")를 계산
        List<String> recentWeekIdentifiers = new ArrayList<>();
        WeekFields weekFields = WeekFields.ISO; // ISO 주차 기준
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 4; i++) {
            LocalDate weekDate = today.minusWeeks(i);
            int year = weekDate.get(weekFields.weekBasedYear());
            int weekOfYear = weekDate.get(weekFields.weekOfWeekBasedYear());
            recentWeekIdentifiers.add(String.format("%d-%02d", year, weekOfYear));
        }
        // 가장 최근 주차가 먼저 오도록 역순 정렬 (UI 표시 순서에 맞춤)
        Collections.reverse(recentWeekIdentifiers);


        // 모든 부서에 대해 4주치 데이터를 채웁니다.
        // rawDepartmentData에 있는 부서들만 처리하거나, 모든 부서 목록을 별도로 가져와 처리할 수 있습니다.
        // 여기서는 DB에서 데이터가 반환된 부서들만 처리합니다.
        for (Map.Entry<String, Map<String, OvertimeData>> entry : rawDepartmentData.entrySet()) {
            String departmentName = entry.getKey();
            Map<String, OvertimeData> departmentWeeklyData = entry.getValue();
            List<OvertimeData> departmentFullWeekList = new ArrayList<>();

            for (String weekIdentifier : recentWeekIdentifiers) {
                // 해당 주차의 데이터가 DB 결과에 있는지 확인
                if (departmentWeeklyData.containsKey(weekIdentifier)) {
                    departmentFullWeekList.add(departmentWeeklyData.get(weekIdentifier));
                } else {
                    // 데이터가 없으면 0%로 채워진 OvertimeData 객체 생성
                    departmentFullWeekList.add(new OvertimeData(weekIdentifier, 0, 0, 0, 0));
                }
            }
            finalDepartmentData.put(departmentName, departmentFullWeekList);
        }

        return finalDepartmentData;
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