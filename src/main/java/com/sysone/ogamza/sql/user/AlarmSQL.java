package com.sysone.ogamza.sql.user;

public class AlarmSQL {
    public static final String SELECT_ALARM = "SELECT alarm_1, alarm_2, alarm_3 FROM employee WHERE id = ? AND IS_DELETED = 0";
    public static final String UPSERT_ALARM_PL_SQL = """
        DECLARE
            v_count NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_count FROM employee WHERE id = ?;

            IF v_count > 0 THEN
                UPDATE employee
                SET alarm_1 = ?, alarm_2 = ?, alarm_3 = ?
                WHERE id = ?;
            ELSE
                INSERT INTO employee (id, alarm_1, alarm_2, alarm_3, is_deleted)
                VALUES (?, ?, ?, ?, 0);
            END IF;
        END;
        """;
}
