package com.sysone.ogamza.sql.user;

/**
 * =============================================
 * DepartmentSQL
 * =============================================
 * - 부서 관련 SQL 쿼리 상수를 정의하는 클래스
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
public class DepartmentSQL {

    /**
     * ▶ 부서 전체 조회 쿼리
     * - 부서 ID와 이름을 모두 조회함
     * - 주로 콤보박스 등에서 부서 목록을 보여줄 때 사용
     */
    public static final String SELECT_DEPARTMENT =
            "SELECT id, name FROM DEPARTMENT";

}
