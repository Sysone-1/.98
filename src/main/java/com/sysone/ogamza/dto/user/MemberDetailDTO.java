package com.sysone.ogamza.dto.user;

import lombok.*;
/**
 * ===========================================
 * 회원 상세 정보 DTO (MemberDetailDTO)
 * ===========================================
 * - 회원 정보 수정을 위한 DTO
 * - 이름과 직급은 불변 (final), 연락처와 비밀번호는 수정 가능
 * - lombok 어노테이션으로 보일러플레이트 코드 최소화
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

@Getter                 // 모든 필드에 대한 Getter 생성
@AllArgsConstructor     // 모든 필드를 파라미터로 받는 생성자 생성
public class MemberDetailDTO {

    private final String name;      // 이름 (불변)
    private final String position;  // 직급 (불변)

    @Setter
    private String tel;             // 연락처 (수정 가능)

    @Setter
    private String password;        // 비밀번호 (수정 가능)
}
