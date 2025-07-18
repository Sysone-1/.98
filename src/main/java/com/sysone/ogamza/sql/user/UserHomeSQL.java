package com.sysone.ogamza.sql.user;

public class UserHomeSQL {

    public static final String UPDATE_USER=
            """
           UPDATE EMPLOYEE
                SET
                    LUCKY_COLOR = ?,
                    LUCKY_NUMBER = ?,
                    LUCKY_SHAPE = ?,
                    RANDOM_MESSAGE =?
               WHERE ID = ?  
            """;
    public static final String SELECT_HOME=
            """
            SELECT e.name AS employeeName, d.name AS deptName, e.pic_dir, e.lucky_color, e.lucky_number
            , e.lucky_shape, e.random_message, e.mood_emoji
            FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE e.ID = ?
                    
             """;
}
