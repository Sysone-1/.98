package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 결재 내역 조회에 사용되는 일정 정보 DTO 클래스입니다.
 * <p>
 * 각 일정의 ID, 사원 ID, 제목, 종류, 승인 일자, 시작/종료 일자, 승인 상태 등의 정보를 포함합니다.
 * 결재 목록 화면에서 사용됩니다.
 *
 * 승인 상태 값은 다음과 같습니다:
 * <ul>
 *     <li>0 - 승인 대기</li>
 *     <li>1 - 승인 완료</li>
 *     <li>2 - 승인 거절</li>
 *     <li>3 - 상신 취소</li>
 * </ul>
 *
 * @author 김민호
 */
@Data
@NoArgsConstructor
public class ScheduleListDTO {
    private long scheduleId;
    private long empId;
    private String title;
    private String scheduleType;
    private LocalDateTime approvalDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String content;
    private int isGranted;

    /**
     * 일정 정보를 초기화하는 생성자입니다.
     *
     * @param scheduleId 일정 ID
     * @param empId 사원 ID
     * @param title 일정 제목
     * @param scheduleType 일정 종류
     * @param approvalDate 결재 승인 일시
     * @param startDate 일정 시작 일시
     * @param endDate 일정 종료 일시
     * @param isGranted 승인 상태
     */
    public ScheduleListDTO(long scheduleId, long empId, String title, String scheduleType, LocalDateTime approvalDate, LocalDateTime startDate, LocalDateTime endDate, int isGranted) {
        this.scheduleId = scheduleId;
        this.empId = empId;
        this.title = title;
        this.scheduleType = scheduleType;
        this.approvalDate = approvalDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isGranted = isGranted;
    }
}
