package com.sysone.ogamza;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * ===========================================
 * 로그인 사용자 DTO (LoginUserDTO)
 * ===========================================
 * - 로그인 성공 시 사용자 정보를 담는 데이터 전달 객체
 * - Controller → Service → View 간 사용자 정보 전달 용도로 사용
 * - Lombok의 @Getter, @AllArgsConstructor를 사용하여
 *   getter 메서드 및 전체 필드 생성자 자동 생성
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

@Getter // ▶ 모든 필드에 대한 Getter 메서드를 자동 생성
@AllArgsConstructor // ▶ 모든 필드를 매개변수로 받는 생성자 자동 생성
public class LoginUserDTO {

    // ▶ 사용자 고유 ID (Primary Key)
    private int id;

    // ▶ 사용자의 소속 부서명
    private String deptName;

    // ▶ 사용자의 직책 또는 직위
    private String position;

    // ▶ 로그인 ID로 사용되는 이메일
    private String email;

    // ▶ 사용자 이름
    private String name;

    // ▶ 관리자 여부 (1: 관리자, 0: 일반 사용자)
    private int isAdmin;

    // ▶ 사원증 UID (DB에서 BLOB → String 변환)
    private String cardUid;

    // ▶ 프로필 사진 경로 (이미지 파일 위치)
    private String profile;
}
