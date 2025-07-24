package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.DashboardDAO;
import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;

import java.util.List;
import java.util.Map;

/**
 * AdminDashboardService 클래스는 관리자 대시보드에서 사용할
 * 근태 현황, 부서별 초과근무 데이터, 주간 출입 거절 로그를 제공하는
 * 비즈니스 로직 계층입니다.
 * 
 * @author 조윤상
 */
public class AdminDashboardService {

    /** 대시보드 관련 데이터를 조회하는 DAO 객체 */
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    /**
     * 근무 현황 데이터를 조회하고 출근율 및 기타 항목을 계산합니다.
     *
     * @return 계산된 출근율과 기타 정보를 포함한 {@link AttendanceStatusDTO} 객체
     */
    public AttendanceStatusDTO getAttendanceStatusData() {
        // 1. DAO를 통해 DB에서 데이터를 가져옵니다.
        AttendanceStatusDTO dto = dashboardDAO.getAttendanceStatus();

        // 2. 출근율 계산
        if (dto.getTotalEmployees() > 0) {
            double rate = (double)
                    dto.getPresentCount() /
                    dto.getTotalEmployees() * 100;

            dto.setAttendanceRate(String.format("%.1f%%", rate));
        } else {
            dto.setAttendanceRate("0%");
        }

        // 3. '기타' 계산: 총원 - 출근 - 지각 - 결근 - 외근 - 휴가
        int etc = dto.getTotalEmployees() -
                (dto.getPresentCount() + dto.getLateCount() +
                        dto.getAbsentCount() + dto.getTripCount() +
                        dto.getVacationCount());
        dto.setEtcCount(Math.max(0, etc)); // 음수가 되지 않도록 보정

        return dto;
    }

    /**
     * 부서별 주간 초과근무 데이터를 조회합니다.
     *
     * @return 부서명을 key로 하고, 해당 부서의 주차별 초과근무 데이터를 리스트로 가지는 맵
     */
    public Map<String, List<OvertimeData>> getDepartmentOvertimeData() {
        return dashboardDAO.getDepartmentOvertimeData();
    }

    /**
     * 최근 4주간 주간 출입 거절 횟수를 조회합니다.
     *
     * @return 주차를 key로 하고, 해당 주차의 출입 거절 횟수를 값으로 가지는 맵
     */
    public Map<String, Integer> getDeniedAccessLogWeekly() {
        return dashboardDAO.getDeniedAccessLogWeekly();
    }
}
