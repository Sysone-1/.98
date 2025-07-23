package com.sysone.ogamza.sql.user;

public class MessageReceiverSQL {

    public static final String SELECT_RECEIVER=
            """
                    SELECT m.id, e.pic_dir,e.name, d.name AS dept_name, m.content, m.send_date, m.is_read
                    FROM message m
                    JOIN employee e ON e.id = m.sender_id
                    JOIN department d ON e.department_id = d.id
                    WHERE m.receiver_id = ?
                    ORDER BY m.send_date DESC
            """;

    public static final String SELECT_MESSAGE=
            """
                SELECT e.name, m.content , m.is_read, m.send_date
                FROM message m
                JOIN employee e ON e.id = m.sender_id
                WHERE m.id = ?    
            """;


    public static final String UPDATE_READ=
            """
                UPDATE message
                SET is_read = 1
                WHERE id = ?
                    AND is_read = 0
            """;

    public static final String SELECT_COUNT=
            """
               SELECT COUNT(*) AS count
               FROM message 
               WHERE receiver_id = ? 
               AND is_read = 0
            """;
}
