package com.sysone.ogamza.dto.user;

import lombok.Getter;
/**
 * ===========================================
 * 알림 설정 DTO (AlarmSettingDTO)
 * ===========================================
 * - 사용자별 알림 설정 정보를 담는 데이터 객체
 * - alarm_1, alarm_2, alarm_3: 각각 3분 전, 5분 전, 10분 전 알림 여부
 *   (1: 활성화, 0: 비활성화)
 * - DB → DAO → Controller 간 알림 설정 전달 용도로 사용
 * - Lombok의 @Getter로 모든 필드에 대한 Getter 자동 생성
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

@Getter // ▶ 모든 필드에 대한 Getter 메서드 자동 생성
public class AlarmSettingDTO {

    private int id;      // 사용자 ID
    private int alarm1;  // 3분 전 알림 여부 (1 or 0)
    private int alarm2;  // 5분 전 알림 여부 (1 or 0)
    private int alarm3;  // 10분 전 알림 여부 (1 or 0)

    /**
     * ▶ 모든 알림 필드를 초기화하는 생성자
     *
     * @param id 사용자 ID
     * @param alarm1 3분 전 알림 여부
     * @param alarm2 5분 전 알림 여부
     * @param alarm3 10분 전 알림 여부
     */
    public AlarmSettingDTO(int id, int alarm1, int alarm2, int alarm3) {
        this.id = id;
        this.alarm1 = alarm1;
        this.alarm2 = alarm2;
        this.alarm3 = alarm3;
    }
}
