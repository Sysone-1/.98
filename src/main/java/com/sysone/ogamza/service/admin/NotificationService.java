package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.NotificationDAO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 미승인 출입 기록 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 NotificationDAO를 이용해 데이터베이스에서 미승인 출입 기록을 조회하고,
 * 미승인 출입 기록을 '읽음' 상태로 업데이트하는 기능을 제공합니다.
 *
 * @author 조윤상
 * @since 2025-07-22
 */
public class NotificationService {

    private NotificationDAO notificationDAO;

    /**
     * 기본 생성자 - NotificationDAO 객체를 초기화합니다.
     */
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    /**
     * 미승인 출입 기록(사원ID가 NULL인 출입 기록)의 총 개수를 조회합니다.
     *
     * @return 미승인 출입 기록의 개수
     */
    public int getUnauthorizedAccessCount() {
        return notificationDAO.getNullEmployeeIdAccessLogCount();
    }

    /**
     * 미승인 출입 기록의 출입 시간을 모두 조회합니다.
     *
     * @return 미승인 출입 시간 목록 (LocalDateTime 리스트)
     */
    public List<LocalDateTime> getUnauthorizedAccessTimes() {
        return notificationDAO.getNullEmployeeIdAccessTimes();
    }

    /**
     * 모든 미승인 출입 기록을 '읽음' 상태로 업데이트합니다.
     */
    public void markAllUnauthorizedAccessLogAsRead() {
        notificationDAO.markUnauthorizedAccessLogAsRead();
    }
}
