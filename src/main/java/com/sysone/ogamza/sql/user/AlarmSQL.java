package com.sysone.ogamza.sql.user;

public class AlarmSQL {
    public static final String SELECT_ALARM = "SELECT alarm_1, alarm_2, alarm_3 FROM employee WHERE id = ? AND IS_DELETED = 0";
    public static final String UPDATE_ALARM = """
                MERGE INTO employee a
                USING (SELECT ? AS ID FROM dual) b
                ON (a.ID = b.ID)
                WHEN MATCHED THEN
                    UPDATE SET ALARM_1 = ?, ALARM_2 = ?, ALARM_3 = ?
                WHEN NOT MATCHED THEN
                    INSERT (ID, ALARM_1, ALARM_2, ALARM_3, IS_DELETED)
                    VALUES (?, ?, ?, ?, 0)
            """;
}
