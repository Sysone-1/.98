package com.sysone.ogamza.dto.admin;

/**
 * 부서별 주간 초과 근무 현황 데이터를 담는 DTO 클래스입니다.
 * 각 필드는 특정 시간 구간별 초과 근무 인원의 백분율을 나타냅니다.
 *
 *
 * @author 조윤상
 * @since  2025-07-22
 */
public class OvertimeData {

    /** 주차 라벨 (예: "29주차 (07.15~...)") */
    private String weekLabel;

    /** 0~4시간 초과 근무 인원 백분율 */
    private int range0to4;

    /** 4~8시간 초과 근무 인원 백분율 */
    private int range4to8;

    /** 8~12시간 초과 근무 인원 백분율 */
    private int range8to12;

    /** 12시간 이상 초과 근무 인원 백분율 */
    private int range12plus;

    /**
     * 생성자 - 주차 라벨과 각 시간 구간별 초과 근무 백분율을 설정합니다.
     *
     * @param weekLabel 주차 라벨
     * @param r0 0~4시간 초과 근무 백분율
     * @param r4 4~8시간 초과 근무 백분율
     * @param r8 8~12시간 초과 근무 백분율
     * @param r12 12시간 이상 초과 근무 백분율
     */
    public OvertimeData(String weekLabel, int r0, int r4, int r8, int r12) {
        this.weekLabel = weekLabel;
        this.range0to4 = r0;
        this.range4to8 = r4;
        this.range8to12 = r8;
        this.range12plus = r12;
    }

    /**
     * 주차 라벨을 반환합니다.
     *
     * @return 주차 라벨
     */
    public String getWeekLabel() {
        return weekLabel;
    }

    /**
     * 0~4시간 초과 근무 인원 백분율을 반환합니다.
     *
     * @return 0~4시간 초과 근무 백분율
     */
    public int getRange0to4() {
        return range0to4;
    }

    /**
     * 4~8시간 초과 근무 인원 백분율을 반환합니다.
     *
     * @return 4~8시간 초과 근무 백분율
     */
    public int getRange4to8() {
        return range4to8;
    }

    /**
     * 8~12시간 초과 근무 인원 백분율을 반환합니다.
     *
     * @return 8~12시간 초과 근무 백분율
     */
    public int getRange8to12() {
        return range8to12;
    }

    /**
     * 12시간 이상 초과 근무 인원 백분율을 반환합니다.
     *
     * @return 12시간 이상 초과 근무 백분율
     */
    public int getRange12plus() {
        return range12plus;
    }
}
