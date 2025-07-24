package com.sysone.ogamza.dto.admin;

public class ClockChangeRequestDTO extends BaseRequestDTO {

    /**
     * @param requestId S.ID
     * @param employeeId S.EMPLOYEE_ID
     * @param employeeName E.NAME
     * @param approvalDate S.APPROVAL_DATE (결재일자)
     * @param originalTime 변경 전 시각 (HH:mm)
     * @param requestedTime 요청 시각 (HH:mm)
     * @param scheduleType DB 실제값 (연장근무, 휴일)
     * @param content S.CONTENT
     * @param isGranted 0=대기, 1=승인, 2=거절
     */
    public ClockChangeRequestDTO(int requestId,
                                 int employeeId,
                                 String employeeName,
                                 String department,
                                 String position,
                                 String approvalDate,
                                 String originalTime,
                                 String requestedTime,
                                 String scheduleType, // DB 실제값
                                 String content,
                                 int isGranted) {
        super(
                requestId,
                employeeId,
                employeeName,
                department,
                position,
                approvalDate,
                originalTime,
                requestedTime,
                "출퇴근변경", // requestType (화면 표시용)
                scheduleType, // scheduleType (DB 실제값: 연장근무, 휴일)
                "", // title (없으면 빈 문자열)
                content,
                isGranted
        );
    }
}
