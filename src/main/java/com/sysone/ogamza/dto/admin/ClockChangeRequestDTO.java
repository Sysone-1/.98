package com.sysone.ogamza.dto.admin;

/**
 * 출퇴근변경(연장근무, 휴일 등) 요청 DTO 클래스
 * - BaseRequestDTO 상속
 * - 출퇴근 변경 요청 시 필요한 추가 필드(originalTime, requestedTime) 포함
 * - requestType 은 "출퇴근변경"으로 고정하여 화면에 표시
 * - scheduleType 은 DB 실제 값으로 연장근무, 휴일 등을 나타냄
 * @author 허겸
 * @since 2025-07-18
 */
public class ClockChangeRequestDTO extends BaseRequestDTO {

    /**
     * 생성자: 출퇴근변경 요청에 필요한 모든 필드 초기화하며
     * BaseRequestDTO 생성자에 적절히 값 전달
     *
     * @param requestId      요청 ID (S.ID)
     * @param employeeId     사원 ID (S.EMPLOYEE_ID)
     * @param employeeName   사원명 (E.NAME)
     * @param department     부서명
     * @param position       직급명
     * @param approvalDate   결재일자 (S.APPROVAL_DATE)
     * @param originalTime   변경 전 시각 (HH:mm 형식)
     * @param requestedTime  요청한 변경 시각 (HH:mm 형식)
     * @param scheduleType   DB 실제 스케줄 종류 (예: 연장근무, 휴일)
     * @param content        요청 상세 내용/사유 (S.CONTENT)
     * @param isGranted      결재 상태 (0=대기, 1=승인, 2=거절)
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
        // BaseRequestDTO 생성자 호출
        // - startDate 필드에는 originalTime (변경 전 시각) 전달
        // - endDate 필드에는 requestedTime (요청 시각) 전달
        // - requestType 은 "출퇴근변경"으로 고정 (화면 표시 목적)
        // - title 은 사용하지 않으므로 빈 문자열로 전달
        super(
                requestId,
                employeeId,
                employeeName,
                department,
                position,
                approvalDate,
                originalTime,      // 출퇴근 변경 전 시각 → startDate 필드로 활용
                requestedTime,     // 요청 시각 → endDate 필드로 활용
                "출퇴근변경",      // 화면에 표시될 요청 구분명
                scheduleType,      // DB 실제값 (연장근무, 휴일 등)
                "",                // 타이틀 없음 (빈 문자열)
                content,
                isGranted
        );
    }
}
