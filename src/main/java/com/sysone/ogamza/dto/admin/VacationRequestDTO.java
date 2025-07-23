package com.sysone.ogamza.dto.admin;

/**
 * 연차 요청을 표현하는 VacationRequest 클래스
 * BaseRequest의 생성자 시그니처(10개 파라미터)에 맞추어 수정
 */
public class VacationRequestDTO extends BaseRequestDTO {

    /**
     * @param requestId     S.ID
     * @param employeeId    S.EMPLOYEE_ID
     * @param employeeName  E.NAME
     * @param approvalDate  S.APPROVAL_DATE (결재일자, TO_CHAR 포맷)
     * @param startDate     휴가 시작일 (YYYY-MM-DD)
     * @param endDate       휴가 종료일 (YYYY-MM-DD)
     * @param isGranted     "0=대기, 1=승인, 2=거절
     */
    public VacationRequestDTO(int requestId,
                              int employeeId,
                              String employeeName,
                              String department,
                              String position,
                              String approvalDate,
                              String startDate,
                              String endDate,
                              String content,
                              int isGranted) {
        super(
                requestId,
                employeeId,
                employeeName,
                department,
                position,
                approvalDate,
                startDate,
                endDate,
                "연차",   // requestType
                "",      // title (휴가신청은 title 미사용)
                content,
                isGranted);
    }
}
