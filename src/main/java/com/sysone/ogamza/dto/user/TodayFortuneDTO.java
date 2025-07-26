
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;
/**
 * 오늘의 행운 정보를 담는 DTO 클래스입니다.
 *
 * 해당 클래스는 시스템에서 매일 갱신되는 사원의 행운 정보를
 * 전달하거나 DB에 저장할 때 사용됩니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
@Builder
@Data
public class TodayFortuneDTO {

    /** 사원 ID */
    private int employeeId;

    /** 행운 숫자 */
    private int luckyNumber;

    /** 행운 도형 */
    private String luckyShape;

    /** 행운 색상 */
    private String luckyColor;

    /** 응원 메시지 */
    private String randomMessage;
}
