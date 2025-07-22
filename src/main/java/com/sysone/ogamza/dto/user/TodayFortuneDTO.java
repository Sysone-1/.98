package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TodayFortuneDTO {

    private int employeeId;
    private int luckyNumber;
    private String luckyShape;
    private String luckyColor;
    private String randomMessage;
}
