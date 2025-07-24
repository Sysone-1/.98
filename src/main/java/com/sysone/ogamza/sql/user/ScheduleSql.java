package com.sysone.ogamza.sql.user;

/**
 * 일정(Schedule) 관련 기능에서 사용되는 SQL 쿼리들을 상수로 정의한 클래스입니다.
 * <p>일정 생성, 승인 일정 조회, 출근 상태 기록 등의 작업에 사용됩니다.</p>
 *
 * @author 김민호
 */
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
            "WHERE EMPLOYEE_ID = ?";

    public static final String FIND_SCHEDULE_GRANTED_LIST =
            "SELECT * " +
            "FROM SCHEDULE " +
            "WHERE EMPLOYEE_ID = ? AND " +
            "IS_GRANTED = 1";

    public static final String INSERT_SCHEDULE =
            "INSERT INTO SCHEDULE (EMPLOYEE_ID, START_DATE, END_DATE, SCHEDULE_TYPE, TITLE, CONTENT) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_CANCEL_SCHEDULE =
            "UPDATE SCHEDULE " +
            "SET IS_GRANTED = 3 " +
            "WHERE EMPLOYEE_ID = ? AND " +
            "ID = ?";

    public static final String INSERT_WORKING_TIME = """
            BEGIN
                FOR emp IN (SELECT ID FROM EMPLOYEE) LOOP
                    DECLARE
                        v_first ACCESS_LOG.ACCESS_TIME%TYPE;
                        v_last ACCESS_LOG.ACCESS_TIME%TYPE;
                        v_status VARCHAR2(10);
                        v_working_time NUMBER;
                    BEGIN
                        SELECT MIN(ACCESS_TIME), MAX(ACCESS_TIME)
                        INTO v_first, v_last
                        FROM ACCESS_LOG
                        WHERE EMPLOYEE_ID = emp.ID
                          AND TRUNC(ACCESS_TIME) = TRUNC(SYSDATE);
        
                        IF v_first IS NULL THEN
                            v_status := '결근';
                            v_working_time := 0;
                        ELSE
                            IF TO_CHAR(v_first, 'HH24:MI') < '09:00' THEN
                                v_status := '출근';
                            ELSE
                                v_status := '지각';
                            END IF;
        
                            v_working_time := TRUNC((CAST(v_last AS DATE) - CAST(v_first AS DATE)) * 1440) - 60;
        
                            IF v_working_time < 0 THEN
                                v_working_time := 0;
                            END IF;
                        END IF;
        
                        INSERT INTO ATTENDANCE (
                            EMPLOYEE_ID, ATTENDANCE_DATE, WORKING_TIME, ATTENDANCE_STATUS
                        )
                        VALUES (
                            emp.ID,
                            TRUNC(SYSDATE),
                            v_working_time,
                            v_status
                        );
        
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            NULL;
                    END;
                END LOOP;
            END;
            """;
}
