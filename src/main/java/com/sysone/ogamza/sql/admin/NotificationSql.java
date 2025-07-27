package com.sysone.ogamza.sql.admin;

/**
 * {@code NotificationSql} 클래스는 관리자 알림 기능에 사용되는
 * 출입 기록 관련 SQL 쿼리를 상수로 정의한 클래스입니다.
 * <p>
 * 주로 직원 미등록 상태의 접근 기록(EMPLOYEE_ID가 NULL인 경우)에 대한
 * 조회 및 업데이트 처리에 사용됩니다.
 *
 * @author 조윤상
 * @since 2025-07-25
 */
public class NotificationSql {

    /**
     * 직원 ID가 NULL이고 읽지 않은(IS_READ = 0) 접근 로그의 개수를 조회하는 SQL입니다.
     * <p>
     * 알림 배지에 표시할 미확인 접근 로그 수 계산 등에 사용됩니다.
     */
    public static final String GET_NULL_EMPLOYEE_ID_ACCESS_LOG_COUNT =
            "SELECT COUNT(*) FROM ACCESS_LOG WHERE EMPLOYEE_ID IS NULL AND IS_READ = 0";

    /**
     * 직원 ID가 NULL이고 읽지 않은(IS_READ = 0) 접근 로그의 시간 목록을 조회하는 SQL입니다.
     * <p>
     * 접근 시간(ACCESS_TIME)을 기준으로 내림차순 정렬되어 반환됩니다.
     */
    public static final String GET_NULL_EMPLOYEE_ID_ACCESS_TIMES =
            "SELECT ACCESS_TIME FROM ACCESS_LOG WHERE EMPLOYEE_ID IS NULL AND IS_READ = 0 ORDER BY ACCESS_TIME DESC";

    /**
     * 직원 ID가 NULL이고 읽지 않은(IS_READ = 0) 접근 로그를 모두 읽음(IS_READ = 1) 처리하는 SQL입니다.
     * <p>
     * 알림 확인 또는 일괄 승인 처리 이후 상태 업데이트에 사용됩니다.
     */
    public static final String MARK_UNAUTHORIZED_ACCESS_LOG_AS_READ =
            "UPDATE ACCESS_LOG SET IS_READ = 1 WHERE EMPLOYEE_ID IS NULL AND IS_READ = 0";
}
