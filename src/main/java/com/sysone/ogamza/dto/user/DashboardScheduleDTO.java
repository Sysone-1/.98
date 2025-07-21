package com.sysone.ogamza.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DashboardScheduleDto {
    private String title;
    private LocalDateTime startDate;
    /**
        0 - 승인 대기, 1 - 승인 완료, 2 - 승인 거절, 3 - 상신 취소
    */
    private int isGranted;

    public DashboardScheduleDto(String title, LocalDateTime startDate, int isGranted) {
        this.title = title;
        this.startDate = startDate;
        this.isGranted = isGranted;
    }
}
