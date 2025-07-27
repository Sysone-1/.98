package com.sysone.ogamza.sql.user;
/**
 * ===========================================
 * 알림 설정 관련 SQL 모음 클래스 (AlarmSQL)
 * ===========================================
 * - 알림 관련 SELECT 및 PL/SQL UPSERT 쿼리를 정의
 * - 알림 설정은 employee 테이블의 alarm_1, alarm_2, alarm_3 컬럼에 저장됨
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */


public class AlarmSQL {

    /**
     * ▶ 사원의 알림 설정 상태 조회
     * - 특정 사원 ID를 기반으로 alarm_1, alarm_2, alarm_3 값을 조회
     * - 삭제된 사원은 제외 (is_deleted = 0 조건 포함)
     */
    public static final String SELECT_ALARM = """
        SELECT alarm_1, alarm_2, alarm_3
        FROM employee
        WHERE id = ? AND is_deleted = 0
        """;

    /**
     * ▶ 알림 설정 저장/수정 (PL/SQL)
     * - 주어진 사원 ID가 존재하면 UPDATE
     * - 존재하지 않으면 INSERT (is_deleted = 0 기본값 포함)
     * - JDBC에서 CallableStatement 로 사용 가능
     *
     * PL/SQL 설명:
     * 1. v_count 변수로 해당 사원이 존재하는지 COUNT 확인
     * 2. 존재하면 UPDATE
     * 3. 존재하지 않으면 INSERT
     */
    public static final String UPSERT_ALARM_PL_SQL = """
        DECLARE
            v_count NUMBER;  -- 사원 존재 여부 확인용 변수
        BEGIN
            -- ID 존재 여부 확인
            SELECT COUNT(*) INTO v_count FROM employee WHERE id = ?;

            IF v_count > 0 THEN
                -- 사원이 존재할 경우 알림 정보 UPDATE
                UPDATE employee
                SET alarm_1 = ?, alarm_2 = ?, alarm_3 = ?
                WHERE id = ?;
            ELSE
                -- 사원이 존재하지 않을 경우 새 레코드 INSERT
                INSERT INTO employee (id, alarm_1, alarm_2, alarm_3, is_deleted)
                VALUES (?, ?, ?, ?, 0);
            END IF;
        END;
        """;
}
