package com.sysone.ogamza.dto.admin;

public class ClockChangeRequestDTO extends BaseRequestDTO {

    /**
     * @param requestId      S.ID
     * @param employeeId     S.EMPLOYEE_ID
     * @param employeeName   E.NAME
     * @param approvalDate   S.APPROVAL_DATE (결재일자)
     * @param originalTime   변경 전 시각 (HH:mm)
     * @param requestedTime  요청 시각 (HH:mm)
     * @param isGranted         “대기중”/“승인”/“거절”
     */
    public ClockChangeRequestDTO(int requestId,
                                 int employeeId,
                                 String employeeName,
                                 String department,
                                 String position,
                                 String approvalDate,
                                 String originalTime,
                                 String requestedTime,
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
                "출퇴근변경",  // requestType
                "",            // title (없으면 빈 문자열)
                content,
                isGranted);
    }
}
