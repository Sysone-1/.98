package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
