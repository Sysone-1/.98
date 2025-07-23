package com.sysone.ogamza.dto.admin;

public class AttendanceStatusDTO {
    private int totalEmployees;
    private int presentCount;
    private int lateCount;
    private int absentCount;
    private int tripCount;
    private int vacationCount;
    private int etcCount; // '기타'는 DTO에서 계산될 수 있습니다.
    private String attendanceRate; // 출근율

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getLateCount() {
        return lateCount;
    }

    public void setLateCount(int lateCount) {
        this.lateCount = lateCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public int getTripCount() {
        return tripCount;
    }

    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }

    public int getVacationCount() {
        return vacationCount;
    }

    public void setVacationCount(int vacationCount) {
        this.vacationCount = vacationCount;
    }

    public int getEtcCount() {
        return etcCount;
    }

    public void setEtcCount(int etcCount) {
        this.etcCount = etcCount;
    }

    public String getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}