package com.sysone.ogamza.model.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfo {

    private String name;
    private String departmentName;
    private String luckyColor;
    private String luckyShape;
    private int luckyNumber;
    private String randomMessage;
    private String emoji;
    private String profile;
}
