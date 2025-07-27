package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.AttendanceStatsDAO;

import java.util.List;

/**
 * AttendanceStatsService
 * – DAO 호출을 래핑하여 컨트롤러에 제공
 * @author 허겸
 */
public class AttendanceStatsService {
    private final AttendanceStatsDAO dao = new AttendanceStatsDAO();

    /**
     * 전체 당일 통계 조회
     */
    public int[] getTodayStats() {
        return dao.fetchTodayStats();
    }

    /**
     * 부서별 당일 통계 조회
     */
    public int[] getTodayStatsByDept(String departmentName) {
        return dao.fetchTodayStatsByDept(departmentName);
    }

    /**
     * 모든 부서 목록 조회
     */
    public List<String> getAllDepartments() {
        return dao.getAllDepartments();
    }
}