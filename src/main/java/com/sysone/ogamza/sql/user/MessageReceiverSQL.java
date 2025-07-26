
package com.sysone.ogamza.sql.user;

/**
 * 쪽지 관련 SQL 쿼리들을 정의한 클래스입니다.
 * 받은 쪽지 목록, 보낸 쪽지 목록, 쪽지 상세 조회, 읽음 처리 및 읽지 않은 쪽지 개수 조회 쿼리를 포함합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class MessageReceiverSQL {

    /** 받은 쪽지함 목록 조회 쿼리 */
    public static final String SELECT_RECEIVER=
            """
                SELECT m.id, e.pic_dir, e.name, d.name AS dept_name, m.content, m.send_date, m.is_read
                FROM message m
                JOIN employee e ON e.id = m.sender_id
                JOIN department d ON e.department_id = d.id
                WHERE m.receiver_id = ?
                ORDER BY m.send_date DESC
            """;

    /** 쪽지 상세 조회 쿼리 */
    public static final String SELECT_MESSAGE=
            """
                SELECT e.name, m.content, m.is_read, m.send_date
                FROM message m
                JOIN employee e ON e.id = m.sender_id
                WHERE m.id = ?    
            """;

    /** 쪽지를 읽음 처리하는 쿼리 */
    public static final String UPDATE_READ=
            """
                UPDATE message
                SET is_read = 1
                WHERE id = ?
                  AND is_read = 0
            """;

    /** 읽지 않은 쪽지 개수 조회 쿼리 */
    public static final String SELECT_COUNT=
            """
               SELECT COUNT(*) AS count
               FROM message 
               WHERE receiver_id = ? 
                 AND is_read = 0
            """;

    /** 보낸 쪽지함 목록 조회 쿼리 */
    public static final String SELECT_SENT=
            """
                SELECT m.id, e.name, d.name AS dept_name, m.content, m.send_date, m.is_read
                FROM message m
                JOIN employee e ON e.id = m.receiver_id
                JOIN department d ON d.id = e.department_id
                WHERE sender_id = ?    
            """;
}
