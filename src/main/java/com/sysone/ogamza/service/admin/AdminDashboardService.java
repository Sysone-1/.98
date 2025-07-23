package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.DashboardDAO;
import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;

import java.util.List; // List 인터페이스 임포트
import java.util.Map; // Map 인터페이스 임포트

public class AdminDashboardService {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public AttendanceStatusDTO getAttendanceStatusData() {
        // 1. DAO를 통해 DB에서 데이터를 가져옵니다.
        AttendanceStatusDTO dto =
                dashboardDAO.getAttendanceStatus();

        // 2. 추가적인 비즈니스 로직을처리합니다.
        // 출근율 계산
        if (dto.getTotalEmployees() > 0) {
            double rate = (double)
                    dto.getPresentCount() /
                    dto.getTotalEmployees() * 100;

            dto.setAttendanceRate(String.format("%.1f%%", rate));
        } else {
            dto.setAttendanceRate("0%");
        }

        // '기타' 계산 (총원 - 출근 - 지각 -결근 - 외근 - 휴가)
        int etc = dto.getTotalEmployees() -
                (dto.getPresentCount() + dto.getLateCount() +
                        dto.getAbsentCount() + dto.getTripCount() +
                        dto.getVacationCount());
        dto.setEtcCount(Math.max(0, etc)); //음수가 되지 않도록

        return dto;
    }

    public Map<String, List<OvertimeData>> getDepartmentOvertimeData() {

        return dashboardDAO.getDepartmentOvertimeData();
    }
}