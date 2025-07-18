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

    public String getTodayAccessTime (long id) {
        LocalDateTime accessTime = dashboardDao.findFirstAccessLogByDateAndEmpId(id);
        int hour = accessTime.getHour();
        int minute = accessTime.getMinute();
        int second = accessTime.getSecond();

        return String.format("%02d : %02d : %02d", hour, minute, second);
    }

    public String getTodayLeaveTime(long id) {
        LocalDateTime leaveTime = dashboardDao.findLastLeaveLogByDateAndEmpId(id);

        int hour = leaveTime.getHour();
        int minute = leaveTime.getMinute();
        int second = leaveTime.getSecond();
        int day = leaveTime.getDayOfMonth();
        int today = LocalDateTime.now().getDayOfMonth();

        if (day == today) {
            return " - - : - - : - - ";
        } else {
            return String.format("%02d : %02d : %02d", hour, minute, second);
        }
    }

    public String[] getWorkingTime(long id) {
        String[] timeArray = new String[3];
        LocalDateTime time = dashboardDao.findTodayWorkTimeByDateAndEmpId(id);

        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(time, currentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        if (minutes % 60 == 0) {
            timeArray[0] = (hours - 1)+ "시간";
            timeArray[1] = (10 - hours) + "시간";
        } else if (hours < 8) {
            timeArray[0] = (hours - 2) + "시간 " + (minutes % 60) + "분";
            timeArray[1] = (9 - hours) + "시간 " + (60 - ((minutes % 60))) + "분";
        } else {
            timeArray[0] = (hours - 2) + "시간 " + (minutes % 60) + "분";
            timeArray[1] = "0시간";
        }
        timeArray[2] = String.valueOf(minutes - 60);
        return timeArray;
    }

    public int getVacationDays(long id) {
        return dashboardDao.findVacationDaysByEmpId(id);
    }

    public int getUsedVacationDays(long id) {
        return dashboardDao.findUsedVacationDaysByEmpId(id);
    }

    public int getTotalWorkingHours(long id) {
        return dashboardDao.findAllWorkTimeByDateAndEmpId(id);
    }

    public int getTotalExtendWorkingHours(long id) {
        return dashboardDao.findAllExtendWorkTimeByDateAndEmpId(id) * 3;
    }

    public int getTotalWeekendWorkingHours(long id) {
        return dashboardDao.findAllWeekendWorkTimeByDateAndEmpId(id) * 8;
    }

    public List<String> getWeekSchedules(long id) {
       return dashboardDao.findSchedulesByDateAndEmpId(id);
    }
}
