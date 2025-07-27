package com.sysone.ogamza.dto.admin;

/**
 * 연차·반차·휴일 요청을 표현하는 DTO 클래스
 * - BaseRequestDTO 클래스를 상속
 * - DB 실제 scheduleType 값을 기반으로 연차, 반차, 휴일 구분 가능
 * - 화면에는 고정으로 "휴가" 요청 타입 표시
 * @author 허겸
 * @since 2025-07-18
 */
public class VacationRequestDTO extends BaseRequestDTO {

    /**
     * 생성자: 연차 관련 요청 정보를 초기화하며 부모 생성자 호출
     *
     * @param requestId      요청 ID (S.ID)
     * @param employeeId     사원 ID (S.EMPLOYEE_ID)
     * @param employeeName   사원 이름 (E.NAME)
     * @param department     부서명
     * @param position       직급명
     * @param approvalDate   결재일자 (S.APPROVAL_DATE, TO_CHAR로 포맷된 문자열)
     * @param startDate      휴가 시작일 (YYYY-MM-DD 형식)
     * @param endDate        휴가 종료일 (YYYY-MM-DD 형식)
     * @param scheduleType   DB 실제 스케줄 종류 (연차, 반차, 휴일)
     * @param content        휴가 사유 또는 상세 내용 (S.CONTENT)
     * @param isGranted      결재 상태 (0=대기, 1=승인, 2=거절)
     */
    public VacationRequestDTO(int requestId,
                              int employeeId,
                              String employeeName,
                              String department,
                              String position,
                              String approvalDate,
                              String startDate,
                              String endDate,
                              String scheduleType, // DB 실제값 (연차, 반차, 휴일)
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
                "휴가",       // 화면에 표시될 요청 타입 고정값 ("휴가")
                scheduleType, // DB 실제 스케줄 타입 값 (연차, 반차, 휴일)
                "",           // title 필드는 휴가 신청에서 사용하지 않으므로 빈 문자열 전달
                content,
                isGranted
        );
    }
}
