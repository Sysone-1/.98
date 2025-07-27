package com.sysone.ogamza.sql.user;

/**
 * =============================================
 * EmployeeSQL
 * =============================================
 * - 사원(Employee) 관련 SQL 쿼리 상수를 정의하는 클래스
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
public class EmployeeSQL {

    /**
     * ▶ 특정 부서에 속한 사원 목록 조회 쿼리
     * - 부서 ID를 조건으로 사원의 ID, 이름, 부서 ID를 조회
     * - 쪽지 보내기 등 기능에서 부서별 사원 목록을 불러올 때 사용
     */
    public static final String SELECT_EMPLOYEE =
            "SELECT id, name, department_id FROM EMPLOYEE WHERE department_id = ?";

}
