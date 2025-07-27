package com.sysone.ogamza.dto.user;

import lombok.*;

/**
 * =============================================
 * 쪽지 데이터 전송 객체 (MessageDTO)
 * =============================================
 * - 쪽지 전송 시 필요한 정보를 담는 DTO 클래스
 * - senderId: 보낸 사람 ID
 * - receiverId: 받는 사람 ID
 * - content: 쪽지 내용
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
@Data                   // getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor      // 기본 생성자 생성
@AllArgsConstructor     // 모든 필드를 초기화하는 생성자 생성
public class MessageDTO {
    private int senderId;     // 보낸 사람 ID
    private int receiverId;   // 받는 사람 ID
    private String content;   // 쪽지 내용
}
