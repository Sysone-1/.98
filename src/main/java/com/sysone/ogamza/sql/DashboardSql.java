package com.sysone.ogamza.sql;

public class DashboardSql {

    public static final String FIND_FIRST_ACCESS_LOG =
                "SELECT ACCESS_TIME " +
                "FROM ACCESS_LOG " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ACCESS_TIME) = TRUNC(SYSDATE) " +
                "ORDER BY ACCESS_TIME " +
                "FETCH FIRST 1 ROWS ONLY";

    public static final String FIND_LAST_ACCESS_LOG =
                "SELECT ACCESS_TIME " +
                "FROM ACCESS_LOG " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ACCESS_TIME) = TRUNC(SYSDATE) " +
                "ORDER BY ACCESS_TIME DESC " +
                "FETCH FIRST 1 ROWS ONLY";

    public static final String FIND_VACATION_NUM =
                "SELECT TOTAL_VAC_NUM " +
                "FROM EMPLOYEE " +
                "WHERE ID = ?";

    public static final String FIND_USED_VACATION_NUM =
                "SELECT COUNT(*) AS TOTAL_USED_VACATION " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "SCHEDULE_TYPE = '연차'";

    public static final String FIND_TOTAL_WORKING_TIME =
                "SELECT SUM(WORKING_TIME) AS TOTAL_WORKING_TIME " +
                "FROM ATTENDANCE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ATTENDANCE_DATE) BETWEEN TRUNC(SYSDATE, 'D') AND TRUNC(SYSDATE, 'D') + 6";

    public static final String FIND_TOTAL_EXTEND_WORKING_TIME =
                "SELECT COUNT(SCHEDULE_TYPE) AS EXTEND_WORK_COUNT " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "SCHEDULE_TYPE ='연장 근무' AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(SYSDATE, 'D') AND TRUNC(SYSDATE, 'D') + 6";

    public static final String FIND_TOTAL_WEEKEND_WORKING_TIME =
                "SELECT COUNT(SCHEDULE_TYPE) AS WEEKEND_WORK_COUNT " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "SCHEDULE_TYPE ='휴일' AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(SYSDATE, 'D') AND TRUNC(SYSDATE, 'D') + 6";

    public static final String FIND_SCHEDULE_LIST =
                "SELECT START_DATE, TITLE " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(SYSDATE, 'D') AND TRUNC(SYSDATE, 'D') + 6";
}
