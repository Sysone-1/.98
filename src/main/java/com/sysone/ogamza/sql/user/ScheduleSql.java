package com.sysone.ogamza.sql.user;

public class ScheduleSql {
    public static final String FIND_SCHEDULE_GRANTED_CONTENT =
            "SELECT * " +
            "FROM SCHEDULE " +
            "WHERE EMPLOYEE_ID = ? AND " +
            "TRUNC(START_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6 AND " +
            "IS_GRANTED = 1";

    public static final String FIND_SCHEDULE_LIST =
            "SELECT * " +
                    "FROM SCHEDULE " +
                    "WHERE EMPLOYEE_ID = ? AND " +
                    "TRUNC(START_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D') AND TRUNC(CURRENT_DATE, 'D') + 6";

    public static final String INSERT_SCHEDULE =
            "INSERT INTO SCHEDULE (EMPLOYEE_ID, START_DATE, END_DATE, SCHEDULE_TYPE, TITLE, CONTENT) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String CANCEL_SCHEDULE =
            "UPDATE SCHEDULE " +
            "SET IS_GRANTED = 3 " +
            "WHERE EMPLOYEE_ID = ? AND " +
            "ID = ?";
}
