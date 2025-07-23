package com.sysone.ogamza.sql.user;

public class UserRecordSQL {

    public static final String SELECT_RECORD=
            """
                    WITH employee_logs AS (
                        SELECT
                            employee_id,
                            TRUNC(access_time) AS access_date,
                            MIN(access_time) AS 출근시간,
                            MAX(access_time) AS 퇴근시간
                        FROM access_log
                        WHERE access_time >= TRUNC(SYSDATE, 'MM')
                          AND access_time < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1)
                        GROUP BY employee_id, TRUNC(access_time)
                    )
                    SELECT
                        a.employee_id,
                        e.name,
                        a.attendance_date,
                        TO_CHAR(l.출근시간, 'HH24:MI:SS') AS CHECK_IN,
                        TO_CHAR(l.퇴근시간, 'HH24:MI:SS') AS CHECK_OUT,
                        a.attendance_status
                    FROM attendance a
                    JOIN employee e ON a.employee_id = e.id
                    LEFT JOIN employee_logs l ON a.employee_id = l.employee_id
                     AND TRUNC(a.attendance_date) = l.access_date
                    WHERE a.employee_id = ? 
                    ORDER BY l.access_date
                    """;
}
