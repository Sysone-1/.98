package com.sysone.ogamza.model.user;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TodayFortune {
    private int employeeId;
    private int luckyNumber;
    private String luckyShape;
    private String luckyColor;
    private String randomMessage;
}
