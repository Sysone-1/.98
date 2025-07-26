
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data

/**
 * 쪽지 상세 내용을 담는 DTO 클래스입니다.
 *
 * - 발신자 이름
 * - 쪽지 내용
 * - 읽음 여부
 * - 발송 일자
 *
 * 주로 쪽지 상세 보기 모달에서 사용됩니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class MessageDetailDTO {
    /** 발신자 이름 */
    private String name;

    /** 쪽지 내용 */
    private String content;

    /** 읽음 여부 (0: 안읽음, 1: 읽음) */
    private int isRead;

    /** 쪽지 발송 날짜 */
    private LocalDate sendDate;
}
