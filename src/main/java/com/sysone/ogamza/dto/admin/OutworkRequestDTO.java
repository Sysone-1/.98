package com.sysone.ogamza.dto.admin;

/**
 * 외근 요청을 표현하는 OutworkRequest 클래스
 */
public class OutworkRequestDTO extends BaseRequestDTO {

    private final String location; // 외근 장소

    /**
     * @param requestId S.ID
     * @param employeeId S.EMPLOYEE_ID
     * @param employeeName E.NAME
     * @param department D.NAME
     * @param position E.POSITION
     * @param approvalDate S.APPROVAL_DATE (YYYY-MM-DD)
     * @param startTime 외근 시작 시각 (HH:mm)
     * @param endTime 외근 종료 시각 (HH:mm)
     * @param location 외근 장소 (title 필드로 사용)
     * @param content S.CONTENT (사유)
     * @param isGranted 0=대기, 1=승인, 2=거절
     */
    public OutworkRequestDTO(int requestId,
                             int employeeId,
                             String employeeName,
                             String department,
                             String position,
                             String approvalDate,
                             String startTime,
                             String endTime,
                             String location,
                             String content,
                             int isGranted) {
        super(
                requestId,
                employeeId,
                employeeName,
                department,
                position,
                approvalDate,
                startTime,
                endTime,
                "출장", // requestType (화면 표시용)
                "외근", // scheduleType (DB 실제값)
                location, // title
                content, // content
                isGranted // status
        );
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
