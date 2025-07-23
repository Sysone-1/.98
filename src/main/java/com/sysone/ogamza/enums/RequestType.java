package com.sysone.ogamza.enums;

public enum RequestType {
    ANNUAL ("연차"), // 휴일, 연차, 반차 통합
    HALFDAY("반차"),
    HOLIDAY("휴일"), // 연장 근무 매핑
    OVERTIME("연장 근무 "),
    FIELDWORK("외근");            // 외근 매핑

    private final String displayName;

    RequestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
