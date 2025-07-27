package com.sysone.ogamza.sql.admin;

/**
 * {@code RecordSql} 클래스는 관리자 기능에서 사용되는
 * 출퇴근 기록(접근 로그) 관련 SQL 쿼리를 상수로 정의합니다.
 * <p>
 * 이 클래스는 주로 {@code AdminRecordDao} 또는 서비스 계층에서
 * SQL을 호출할 때 사용됩니다.
 *
 * @author 조윤상
 * @since 2025-07-25
 */
public class RecordSql {

    /**
     * 모든 출퇴근 기록을 조회하는 SQL 쿼리입니다.
     * <p>
     * - 직원 ID, 이름, 부서명, 직급, 태깅 시간(access_time), 승인 상태를 포함합니다.<br>
     * - 직원 정보가 없는 경우, 기본값(-1 또는 '???')으로 표시되며, 승인 상태는 '미등록'으로 표시됩니다.
     * <p>
     * 정렬 기준: 태깅 시간 내림차순
     */
    public static final String FIND_ALL_RECORDS =
            "SELECT " +
                    "    NVL(e.id, -1) AS emp_id, " +
                    "    NVL(e.name, '???') AS employee_name, " +
                    "    NVL(d.name, '???') AS department_name, " +
                    "    NVL(e.position, '???') AS position, " +
                    "    a.access_time AS tagging_time, " +
                    "    CASE WHEN e.id IS NULL THEN '미등록' ELSE '등록됨' END AS approval_status " +
                    "FROM access_log a " +
                    "LEFT JOIN employee e ON a.employee_id = e.id " +
                    "LEFT JOIN department d ON e.department_id = d.id " +
                    "ORDER BY a.access_time DESC";
}
