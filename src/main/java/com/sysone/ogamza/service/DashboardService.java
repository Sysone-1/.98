package com.sysone.ogamza.service;

import com.sysone.ogamza.repository.DashboardDAO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardService {

    private static final DashboardService instance = new DashboardService();
    private static final DashboardDAO dashboardDao = DashboardDAO.getInstance();

    private DashboardService() {}


    public static DashboardService getInstance() {
        return instance;
    }

    /*
        출근 시간 조회
    */
    public String getTodayAccessTime (long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(null);
        return formatTime(accessTime);
    }

    /*
        퇴근 시간 조회
    */
    public String getTodayLeaveTime(long id) {
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(null);

        int today = LocalDateTime.now().getDayOfMonth();
        return (leaveTime.getDayOfMonth() == today) ? "- - : - - : - -" : formatTime(leaveTime);
    }

    /*
        근로 시간 및 잔여 근로 시간 조회
    */
    public String[] getWorkingTime(long id) {
        String[] timeArray = new String[3];
        LocalDateTime accessTime = dashboardDao.findFirstAccessTimeByDateAndId(id).orElse(null);
        LocalDateTime leaveTime = dashboardDao.findLastLeaveTimeByDateAndId(id).orElse(null);

        int today = LocalDateTime.now().getDayOfMonth();
        LocalDateTime currentTime = (leaveTime.getDayOfMonth() == today) ? LocalDateTime.now() : leaveTime;

        Duration duration = Duration.between(accessTime, currentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        // timeArray[0] : 근로 시간, timeArray[1] : 잔여 근로 시간, timeArray[2] : 총근로시간(totalMinutes)
        timeArray[0] = (minutes % 60 == 0) ? (hours - 1) + "시간" : (hours - 1) + "시간 " + (minutes % 60) + "분";
        timeArray[1] = (hours >= 9) ? "0시간 0분" : (8 - hours) + "시간 " + (60 - ((minutes % 60))) + "분";
        timeArray[2] = String.valueOf(minutes);

        return timeArray;
    }

    /*
        총연차 조회
    */
    public int getVacationDays(long id) {
        return dashboardDao.findVacationDaysByEmpId(id);
    }

    /*
        사용 연차 조회
    */
    public int getUsedVacationDays(long id) {
        return dashboardDao.findUsedVacationDaysByEmpId(id);
    }

    /*
        총 근무 시간 조회
    */
    public int getTotalWorkingHours(long id) {
        return dashboardDao.findAllWorkTimeByDateAndEmpId(id);
    }

    /*
        총 연장 근무 시간 조회 (하루 최대 3시간 기준)
    */
    public int getTotalExtendWorkingHours(long id) {
        return dashboardDao.findAllExtendWorkTimeByDateAndEmpId(id) * 3;
    }

    /*
        총 주말 근무 시간 조회 (하루 최대 8시간 기준)
    */
    public int getTotalWeekendWorkingHours(long id) {
        return dashboardDao.findAllWeekendWorkTimeByDateAndEmpId(id) * 8;
    }

    /*
        주 기준 일정 조회
    */
    public List<String> getWeekSchedules(long id) {
       return dashboardDao.findSchedulesByDateAndEmpId(id);
    }

    /*
        시간 포맷팅
    */
    private String formatTime(LocalDateTime time) {
        return String.format("%02d : %02d : %02d", time.getHour(), time.getMinute(), time.getSecond());
    }
}
