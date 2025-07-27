package com.sysone.ogamza.sql.user;
/**
 * ============================================
 * 쪽지 전송 관련 SQL 정의 클래스 (MessageSenderSQL)
 * ============================================
 * - 메시지 발송 시 사용할 INSERT SQL 문을 정의
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ============================================
 */

public class MessageSenderSQL {

    /**
     * ▶ 쪽지 등록 SQL
     * - message_seq 시퀀스를 통해 고유 ID 생성
     * - sender_id: 발신자 ID
     * - receiver_id: 수신자 ID
     * - content: 쪽지 내용
     */
    public static final String INSERT_MESSAGE =
            "INSERT INTO MESSAGE (id, sender_id, receiver_id, content) " +
                    "VALUES (message_seq.nextval, ?, ?, ?)";
}
