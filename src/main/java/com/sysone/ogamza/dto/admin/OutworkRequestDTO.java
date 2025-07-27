package com.sysone.ogamza.dto.admin;

/**
 * 외근(출장) 요청을 표현하는 DTO 클래스
 * - BaseRequestDTO 클래스를 상속하여 주요 공통 필드 사용
 * - 외근 장소 정보를 별도 필드(location)로 가지고 있음
 * - requestType은 "출장"으로 고정하여 화면에 표시
 * - scheduleType은 DB 실제값으로 "외근" 고정
 * - title 필드에는 외근 장소(location)를 저장하여 UI에 노출
 * @author 허겸
 * @since 2025-07-18
 */
public class OutworkRequestDTO extends BaseRequestDTO {

    // ===================== 외근 추가 필드 =====================
    private final String location; // 외근 장소 (BaseRequestDTO의 title 필드로도 사용됨)

    /**
     * 생성자: 외근 요청에 필요한 모든 필드 초기화 및 부모 생성자 호출
     *
     * @param requestId      요청ID (S.ID)
     * @param employeeId     사원ID (S.EMPLOYEE_ID)
     * @param employeeName   사원이름 (E.NAME)
     * @param department     부서명 (D.NAME)
     * @param position       직급명 (E.POSITION)
     * @param approvalDate   결재일자 (S.APPROVAL_DATE, 형식: YYYY-MM-DD)
     * @param startTime      외근 시작 시각 (HH:mm)
     * @param endTime        외근 종료 시각 (HH:mm)
     * @param location       외근 장소 (BaseRequestDTO의 title 필드와 중복으로 사용)
     * @param content        요청 사유 및 내용 (S.CONTENT)
     * @param isGranted      결재 상태 (0=대기, 1=승인, 2=거절)
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
                startTime,      // 출근 전 필드 용도로 외근 시작 시각 사용
                endTime,        // 출근 후 필드 용도로 외근 종료 시각 사용
                "출장",         // 화면에 표시할 요청 타입 고정 (출장)
                "외근",         // DB 실제 스케줄 타입 (외근)
                location,       // 타이틀 필드에 외근 장소 저장
                content,        // 사유 내용
                isGranted
        );
        this.location = location; // 별도 필드로 외근 장소 저장
    }

    /**
     * 외근 장소 반환
     * @return 외근 장소 문자열
     */
    public String getLocation() {
        return location;
    }
}
