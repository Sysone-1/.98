package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoDTO {

    private String name;
    private String departmentName;
    private String luckyColor;
    private String luckyShape;
    private int luckyNumber;
    private String randomMessage;
    private String emoji;
    private String profile;
}
