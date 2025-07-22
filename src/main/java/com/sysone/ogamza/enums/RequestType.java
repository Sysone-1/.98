package com.sysone.ogamza.enums;

public enum RequestType {
    VACATION("휴가"),           // 휴일, 연차, 반차 통합
    CLOCK_CHANGE("출퇴근 변경신청"), // 연장 근무 매핑
    OUTWORK("출장");            // 외근 매핑

    private final String displayName;

    RequestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
