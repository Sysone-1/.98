package com.sysone.ogamza.dto.admin;

public class OvertimeData {
    /**
     * 부서별 주간 초과 근무 현황 데이터를 담는 DTO 클래스입니다.
     */

    private String weekLabel;    // 예: "29주차 (07.15~...)"
    private int range0to4;       // 0~4시간 초과 근무 인원 (백분율)
    private int range4to8;       // 4~8시간 초과 근무 인원 (백분율)
    private int range8to12;      // 8~12시간 초과 근무 인원 (백분율)
    private int range12plus;     // 12시간 이상 초과 근무 인원 (백분율)

    public OvertimeData(String weekLabel, int r0, int r4, int r8, int r12) {
        this.weekLabel = weekLabel;
        this.range0to4 = r0;
        this.range4to8 = r4;
        this.range8to12 = r8;
        this.range12plus = r12;
    }

    public String getWeekLabel() {
        return weekLabel;
    }

    public int getRange0to4() {
        return range0to4;
    }

    public int getRange4to8() {
        return range4to8;
    }

    public int getRange8to12() {
        return range8to12;
    }

    public int getRange12plus() {
        return range12plus;
    }
}

