package com.sysone.ogamza.sql.user;
/**
 * ===========================================
 * 회원 관련 SQL 정의 클래스 (MemberSQL)
 * ===========================================
 * - DB에서 회원 정보를 조회 및 수정하는 SQL 쿼리 정의
 * - 각 쿼리는 DAO에서 참조되어 사용됨
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class MemberSQL {

    // ▶ 이메일로 회원 정보 조회 (이름, 직책, 연락처, 비밀번호)
    public static final String SELECT_EMPLOYEE =
            "SELECT name, position, tel, password FROM employee WHERE email = ?";

    // ▶ 이메일을 기준으로 비밀번호 및 연락처 수정
    public static final String UPDATE_EMPLOYEE =
            "UPDATE employee SET password = ?, tel = ? WHERE email = ?";
}
