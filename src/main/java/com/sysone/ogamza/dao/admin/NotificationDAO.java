package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.sql.admin.NotificationSql;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NotificationDAO클래스는 출입 기록 중 미인증(사원 ID가 없는) 로그를 조회하고, 해당 로그를 읽음 처리하는 데이터 액세스 객체입니다.
 *이 클래스는 Oracle 데이터베이스와 통신하여 알림 관련 쿼리를 실행합니다.
 *
 * @author 조윤상
 * @since 2025-07-22
 */
public class NotificationDAO {

    /**
     * 사원 ID가 없는 출입 로그의 개수를 반환합니다.
     *
     * @return 출입 로그 중 사원 ID가 NULL인 행의 수
     */
    public int getNullEmployeeIdAccessLogCount() {
        return OracleConnector.fetchData(NotificationSql.GET_NULL_EMPLOYEE_ID_ACCESS_LOG_COUNT, Integer.class).orElse(0);
    }

    /**
     * 사원 ID가 없는 출입 로그의 발생 시각 리스트를 반환합니다.
     *
     * @return 출입 거부가 발생한 시간의 리스트
     */
    public List<LocalDateTime> getNullEmployeeIdAccessTimes() {
        return OracleConnector.fetchList(NotificationSql.GET_NULL_EMPLOYEE_ID_ACCESS_TIMES, LocalDateTime.class);
    }

    /**
     * 사원 ID가 없는 출입 로그를 읽음 처리(확인 처리)합니다.
     * 해당 로그는 더 이상 미확인 상태로 간주되지 않습니다.
     */
    public void markUnauthorizedAccessLogAsRead() {
        OracleConnector.executeDML(NotificationSql.MARK_UNAUTHORIZED_ACCESS_LOG_AS_READ);
    }
}
