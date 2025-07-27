package com.sysone.ogamza.sql;
/**
 * ===========================================
 * 로그인 관련 SQL 쿼리 모음 클래스 (LoginSQL)
 * ===========================================
 * - 로그인 기능에 필요한 SQL 문을 상수로 정의
 * - SQL과 Java 코드의 분리로 유지보수 용이성 향상
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class LoginSQL {

    /**
     * ▶ 로그인 사용자 정보 조회 쿼리
     * - 이메일과 비밀번호를 기준으로 employee 테이블에서 사용자 정보 조회
     * - 사용자의 부서 이름은 department 테이블과 JOIN하여 가져옴
     *
     * 반환 컬럼:
     * - e.id: 사원 ID
     * - d.name: 부서명 (AS dept_name)
     * - e.position: 직책
     * - e.email: 이메일
     * - e.name: 사원 이름
     * - e.is_admin: 관리자 여부 (1 or 0)
     * - e.card_uid: 사원증 UID (BLOB)
     * - e.pic_dir: 프로필 사진 경로
     */
    public static final String SELECT_LOGIN = """
        SELECT 
            e.id, 
            d.name AS dept_name, 
            e.position, 
            e.email, 
            e.name, 
            e.is_admin, 
            e.card_uid, 
            e.pic_dir
        FROM employee e
        JOIN department d ON d.id = e.department_id
        WHERE email = ? AND password = ?
        """;
}
