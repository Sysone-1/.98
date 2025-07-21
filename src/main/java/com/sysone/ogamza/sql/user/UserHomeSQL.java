package com.sysone.ogamza.sql.user;

public class UserHomeSQL {

    // home data
    public static final String SELECT_HOME=
            """
            SELECT e.name AS employeeName, d.name AS deptName, e.pic_dir, e.lucky_color, e.lucky_number
            , e.lucky_shape, e.random_message, e.mood_emoji
            FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE e.ID = ?
                    
             """;


    // lucky update
    public static final String SELECT_ALL=
            """
            SELECT id
            FROM employee       
            """;

    public static final String INSERT_TEMP=
            """
            INSERT INTO luck_update_temp (employee_id, lucky_number, lucky_shape, lucky_color, random_message)
            VALUES (?, ?, ? , ? ,?)
            """;

    public static final String MERGE_DATA=
            """
             BEGIN
                 MERGE INTO employee e
                 USING luck_update_temp t
                 ON(e.ID = t.employee_id)
                 WHEN MATCHED THEN
                    UPDATE SET
                        e.lucky_number = t.lucky_number,
                        e.lucky_shape = t.lucky_shape,
                        e.lucky_color = t.lucky_color,
                        e.random_message = t.random_message;
            END;
            """;

    public static final String UPDATE_EMOJI=
            """
                UPDATE employee
                SET mood_emoji = ?
                WHERE id = ?        
            """;


    public static final String SELECT_RANKING=
            """
                    SELECT RANK() OVER (ORDER BY TOTAL_SCORE ASC) AS RANKING,
                                   DEPARTMENT_ID,
                                   NAME,
                                   TOTAL_SCORE
                    FROM DEPT_RANKING
                    ORDER BY TOTAL_SCORE ASC
                    FETCH FIRST 3 ROWS ONLY;
            """;
}
