package com.sysone.ogamza.repository;

import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {
    private static final DashboardDAO instance = new DashboardDAO();

    private DashboardDAO() {}

    public static DashboardDAO getInstance() {
        return instance;
    }

    public LocalDateTime findFirstAccessLogByDateAndEmpId(long id) {
        String sql = "SELECT * FROM ACCESS_LOG WHERE EMPLOYEE_ID = ? ORDER BY ACCESS_TIME FETCH FIRST 1 ROWS ONLY";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            LocalDateTime time = null;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    time = rs.getTimestamp("ACCESS_TIME").toLocalDateTime();
                }
            }

            return time;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalDateTime findLastLeaveLogByDateAndEmpId(long id) {
        return getLocalDateTime(id);
    }

    private static LocalDateTime getLocalDateTime(long id) {
        String sql = "SELECT ACCESS_TIME FROM ACCESS_LOG WHERE EMPLOYEE_ID = ? ORDER BY ACCESS_TIME DESC FETCH FIRST 1 ROWS ONLY";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            LocalDateTime time = null;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    time = rs.getTimestamp("ACCESS_TIME").toLocalDateTime();
                }
            }
            return time;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalDateTime findTodayWorkTimeByDateAndEmpId(long id) {
        String sql = "SELECT ACCESS_TIME FROM ACCESS_LOG WHERE EMPLOYEE_ID = ? ORDER BY ACCESS_TIME FETCH FIRST 1 ROWS ONLY";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            LocalDateTime time = null;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    time = rs.getTimestamp("ACCESS_TIME").toLocalDateTime();
                }
            }
            return time;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int findVacationDaysByEmpId(long id) {
        String sql = "SELECT TOTAL_VAC_NUM FROM EMPLOYEE WHERE ID = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            int vacationNum = 0;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    vacationNum = rs.getInt("TOTAL_VAC_NUM");
                }
            }
            return vacationNum;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int findUsedVacationDaysByEmpId(long id) {
        String sql = "SELECT COUNT(*) AS TOTAL_USED_VACATION FROM SCHEDULE WHERE EMPLOYEE_ID = ? AND SCHEDULE_TYPE = '연차'";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            int usedVacationNum = 0;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    usedVacationNum = rs.getInt("TOTAL_USED_VACATION");
                }
            }
            return usedVacationNum;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int findAllWorkTimeByDateAndEmpId(long id) {
        String sql = "SELECT SUM(WORKING_TIME) AS TOTAL_WORKING_TIME FROM ATTENDANCE WHERE EMPLOYEE_ID = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            int totalWorkingTime = 0;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    totalWorkingTime = rs.getInt("TOTAL_WORKING_TIME");
                }
            }
            return totalWorkingTime;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int findAllExtendWorkTimeByDateAndEmpId(long id) {
        String sql = "SELECT COUNT(SCHEDULE_TYPE) AS EXTEND_WORK_COUNT FROM SCHEDULE WHERE EMPLOYEE_ID = ? AND SCHEDULE_TYPE ='연장 근무'";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            int totalExtendWorkingTime = 0;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    totalExtendWorkingTime = rs.getInt("EXTEND_WORK_COUNT");
                }
            }
            return totalExtendWorkingTime;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int findAllWeekendWorkTimeByDateAndEmpId(long id) {
        String sql = "SELECT COUNT(SCHEDULE_TYPE) AS WEEKEND_WORK_COUNT FROM SCHEDULE WHERE EMPLOYEE_ID = ? AND SCHEDULE_TYPE ='휴일'";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            int totalWeekendWorkingTime = 0;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    totalWeekendWorkingTime = rs.getInt("WEEKEND_WORK_COUNT");
                }
            }
            return totalWeekendWorkingTime;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> findSchedulesByDateAndEmpId(long id) {
        String sql = "SELECT * FROM SCHEDULE WHERE EMPLOYEE_ID = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            List<String> scheduleList = new ArrayList<>();
            LocalDateTime startDate = null;
            String title = null;
            pstmt.setLong(1, id);
            int index = 0;

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    startDate = rs.getTimestamp("START_DATE").toLocalDateTime();
                    title = rs.getString("TITLE");
                    int month = startDate.getMonthValue();
                    int day = startDate.getDayOfMonth();

                    scheduleList.add(String.format("%02d월 %02d일 %s", month, day, title));
                }
            }
            return scheduleList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
