package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 대시보드에 표시할 일정 정보를 담는 DTO 클래스입니다.
 * <p>
 * 일정의 제목, 시작일, 승인 상태 등의 정보를 포함합니다.
 * 승인 상태는 다음과 같이 정의됩니다:
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
public class DashboardScheduleDTO {
    private String title;
    private LocalDateTime startDate;
    private int isGranted;

    /**
     * 필드 전체를 초기화하는 생성자입니다.
     *
     * @param title 일정 제목
     * @param startDate 일정 시작일
     * @param isGranted 승인 상태 코드
     */
    public DashboardScheduleDTO(String title, LocalDateTime startDate, int isGranted) {
        this.title = title;
        this.startDate = startDate;
        this.isGranted = isGranted;
    }
}
