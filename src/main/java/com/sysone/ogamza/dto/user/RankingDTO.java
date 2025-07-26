
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
/**
 * 부서별 근태 점수 랭킹 정보를 담는 DTO 클래스입니다.
 *
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class RankingDTO {

    /** 부서 ID */
    private int deptId;

    /** 부서 이름 */
    private String deptName;

    /** 누적 점수 */
    private int score;

    /** 랭킹 */
    private int ranking;
}
