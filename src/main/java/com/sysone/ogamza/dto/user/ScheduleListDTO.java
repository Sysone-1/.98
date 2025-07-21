package com.sysone.ogamza.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ScheduleListDto {
    private long scheduleId;
    private long empId;
    private String title;
    private String scheduleType;
    private LocalDateTime approvalDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String content;
    private int isGranted;

    public ScheduleListDto(long scheduleId, long empId, String title, String scheduleType, LocalDateTime approvalDate, LocalDateTime startDate, LocalDateTime endDate, int isGranted) {
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
