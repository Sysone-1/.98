
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;
/**
 * 사용자 홈 화면에 표시되는 정보를 담는 DTO 클래스입니다.
 *
 *
 * 사용자의 이름, 부서명, 행운 정보(색상, 숫자, 도형), 랜덤 메시지,
 * 기분 이모지, 프로필 이미지 경로 등을 캡슐화하여 전달합니다.
 *
 *
 * @author 서샘이
 * @since 2025-07-27
 */
@Builder
@Data
public class UserInfoDTO {

    /** 사용자 이름 */
    private String name;

    /** 소속 부서명 */
    private String departmentName;

    /** 오늘의 행운 색상 */
    private String luckyColor;

    /** 오늘의 행운 도형 */
    private String luckyShape;

    /** 오늘의 행운 숫자 */
    private int luckyNumber;

    /** GPT 기반 랜덤 응원 메시지 */
    private String randomMessage;

    /** 사용자가 선택한 기분 이모지 */
    private String emoji;

    /** 프로필 이미지 경로 */
    private String profile;
}
