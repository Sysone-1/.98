package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.AlarmDAO;
import com.sysone.ogamza.dto.user.AlarmSettingDTO;
/**
 * ===========================================
 * 알림 설정 서비스 (AlarmService)
 * ===========================================
 * - Controller와 DAO 사이에서 비즈니스 로직 처리
 * - 알림 조회 및 저장/업데이트 기능 담당
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */


public class AlarmService {
    private final AlarmDAO alarmDAO = new AlarmDAO();

    /**
     * 사용자 ID로 알림 설정 조회
     */
    public AlarmSettingDTO getAlarmSetting(int userId) {
        return alarmDAO.findByUserId(userId);
    }

    /**
     * 알림 설정 저장 또는 수정
     */
    public void saveOrUpdate(int userId, int minutesBefore) {
        alarmDAO.saveOrUpdate(userId, minutesBefore);
    }
}
