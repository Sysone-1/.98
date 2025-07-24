package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일정 등록 및 조회 시 사용되는 DTO 클래스입니다.
 * <p>
 * 사원의 일정 제목, 종류, 시작일, 종료일, 내용, 승인 상태 등의 정보를 포함합니다.
 * 주로 일정 등록, 수정, 상세 조회 등의 기능에서 사용됩니다.
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
public class ScheduleContentDTO {
    private long empId;
    private String title;
    private String scheduleType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String content;
    private int isGranted;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param empId 사원 ID
     * @param title 일정 제목
     * @param scheduleType 일정 종류
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param content 상세 내용
     * @param isGranted 승인 상태
     */
    public ScheduleContentDTO(long empId, String title, String scheduleType, LocalDateTime startDate, LocalDateTime endDate, String content, int isGranted) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scheduleType = scheduleType;
        this.title = title;
        this.content = content;
        this.isGranted = isGranted;
    }
}
