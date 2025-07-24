package com.sysone.ogamza.sql.user;

/**
 * 대시보드 기능에서 사용되는 SQL 쿼리들을 상수로 정의한 클래스입니다.
 * <p>출퇴근 시간, 연차, 근무 시간 등의 데이터를 조회하는 데 사용됩니다.</p>
 *
 * @author 김민호
 */
public class DashboardSql {

    public static final String FIND_FIRST_ACCESS_LOG =
                "SELECT ACCESS_TIME " +
                "FROM ACCESS_LOG " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ACCESS_TIME) = TRUNC(CURRENT_DATE) " +
                "ORDER BY ACCESS_TIME " +
                "FETCH FIRST 1 ROWS ONLY";

    public static final String FIND_LAST_ACCESS_LOG =
                "SELECT ACCESS_TIME " +
                "FROM ACCESS_LOG " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ACCESS_TIME) = TRUNC(CURRENT_DATE) " +
                "ORDER BY ACCESS_TIME DESC " +
                "FETCH FIRST 1 ROWS ONLY";

    public static final String FIND_VACATION_NUM =
                "SELECT TOTAL_VAC_NUM " +
                "FROM EMPLOYEE " +
                "WHERE ID = ?";

    public static final String FIND_USED_VACATION_NUM =
                "SELECT SCHEDULE_TYPE, START_DATE, END_DATE " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ?";

    public static final String FIND_TOTAL_WORKING_TIME =
                "SELECT SUM(WORKING_TIME) AS TOTAL_WORKING_TIME " +
                "FROM ATTENDANCE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(ATTENDANCE_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6";

    public static final String FIND_TOTAL_EXTEND_WORKING_TIME =
                "SELECT COUNT(SCHEDULE_TYPE) AS EXTEND_WORK_COUNT " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "SCHEDULE_TYPE ='연장 근무' AND " +
                "IS_GRANTED = 1 AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6";

    public static final String FIND_TOTAL_WEEKEND_WORKING_TIME =
                "SELECT COUNT(SCHEDULE_TYPE) AS WEEKEND_WORK_COUNT " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "SCHEDULE_TYPE ='휴일' AND " +
                "IS_GRANTED = 1 AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6";

    public static final String FIND_SCHEDULE_LIST =
                "SELECT * " +
                "FROM SCHEDULE " +
                "WHERE EMPLOYEE_ID = ? AND " +
                "TRUNC(START_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6 " +
                "ORDER BY START_DATE";
}
