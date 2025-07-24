package com.sysone.ogamza.dto.admin;

/**
 * AttendanceStatusDTO 클래스는 관리자 대시보드에서 사용하는
 * 근태 현황 정보를 담는 데이터 전송 객체입니다.
 * 총 인원, 출근, 지각, 결근, 외근, 휴가, 기타 인원 수와 출근율을 포함합니다.
 *
 * @author 조윤상
 * @since 2025-07-20
 */
public class AttendanceStatusDTO {

    /** 총 인원 수 */
    private int totalEmployees;

    /** 출근 인원 수 */
    private int presentCount;

    /** 지각 인원 수 */
    private int lateCount;

    /** 결근 인원 수 */
    private int absentCount;

    /** 외근 인원 수 */
    private int tripCount;

    /** 휴가 인원 수 */
    private int vacationCount;

    /** 기타 사유 인원 수 (기타 상태는 서버 또는 클라이언트 로직에서 계산될 수 있음) */
    private int etcCount;

    /** 출근율 (예: "85.3%") */
    private String attendanceRate;

    /**
     * 총 인원 수를 반환합니다.
     *
     * @return 총 인원 수
     */
    public int getTotalEmployees() {
        return totalEmployees;
    }

    /**
     * 총 인원 수를 설정합니다.
     *
     * @param totalEmployees 총 인원 수
     */
    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    /**
     * 출근 인원 수를 반환합니다.
     *
     * @return 출근 인원 수
     */
    public int getPresentCount() {
        return presentCount;
    }

    /**
     * 출근 인원 수를 설정합니다.
     *
     * @param presentCount 출근 인원 수
     */
    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    /**
     * 지각 인원 수를 반환합니다.
     *
     * @return 지각 인원 수
     */
    public int getLateCount() {
        return lateCount;
    }

    /**
     * 지각 인원 수를 설정합니다.
     *
     * @param lateCount 지각 인원 수
     */
    public void setLateCount(int lateCount) {
        this.lateCount = lateCount;
    }

    /**
     * 결근 인원 수를 반환합니다.
     *
     * @return 결근 인원 수
     */
    public int getAbsentCount() {
        return absentCount;
    }

    /**
     * 결근 인원 수를 설정합니다.
     *
     * @param absentCount 결근 인원 수
     */
    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    /**
     * 외근 인원 수를 반환합니다.
     *
     * @return 외근 인원 수
     */
    public int getTripCount() {
        return tripCount;
    }

    /**
     * 외근 인원 수를 설정합니다.
     *
     * @param tripCount 외근 인원 수
     */
    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }

    /**
     * 휴가 인원 수를 반환합니다.
     *
     * @return 휴가 인원 수
     */
    public int getVacationCount() {
        return vacationCount;
    }

    /**
     * 휴가 인원 수를 설정합니다.
     *
     * @param vacationCount 휴가 인원 수
     */
    public void setVacationCount(int vacationCount) {
        this.vacationCount = vacationCount;
    }

    /**
     * 기타 인원 수를 반환합니다.
     *
     * @return 기타 인원 수
     */
    public int getEtcCount() {
        return etcCount;
    }

    /**
     * 기타 인원 수를 설정합니다.
     *
     * @param etcCount 기타 인원 수
     */
    public void setEtcCount(int etcCount) {
        this.etcCount = etcCount;
    }

    /**
     * 출근율을 반환합니다.
     *
     * @return 출근율 (예: "92.4%")
     */
    public String getAttendanceRate() {
        return attendanceRate;
    }

    /**
     * 출근율을 설정합니다.
     *
     * @param attendanceRate 출근율 (예: "92.4%")
     */
    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
