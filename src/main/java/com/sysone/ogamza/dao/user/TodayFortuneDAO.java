package com.sysone.ogamza.dao.user;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TodayFortuneDAO {
    private int employeeId;
    private int luckyNumber;
    private String luckyShape;
    private String luckyColor;
    private String randomMessage;
}
