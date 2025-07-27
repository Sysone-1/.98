package com.sysone.ogamza.dto.admin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * SCHEDULE 테이블의 주요 컬럼을 그대로 매핑하는 DTO 클래스
 * - JavaFX TableView 용도(속성 바인딩)
 * - 화면 표시값과 실제 DB 값(scheduleType) 모두 분리 관리
 *
 * @author 허겸
 * @since 2025-07-18
 */
public class BaseRequestDTO {

    // ===================== 주요 속성(테이블 컬럼 매핑) =====================
    private final IntegerProperty requestId;         // 요청 ID (S.ID)
    private final IntegerProperty employeeId;        // 사원번호 (S.EMPLOYEE_ID)
    private final StringProperty employeeName;       // 사원명 (S.EMPLOYEE.NAME)
    private final StringProperty department;         // 부서명
    private final StringProperty position;           // 직급
    private final StringProperty approvalDate;       // 결재일시 (S.APPROVAL_DATE)
    private final StringProperty startDate;          // 시작일자 (S.START_DATE)
    private final StringProperty endDate;            // 종료일자 (S.END_DATE)
    private final StringProperty requestType;        // 요청 화면 구분명(휴가/출장 등)
    private final StringProperty scheduleType;       // 실제 DB 내 스케줄 종류(연차/반차 등)
    private final StringProperty title;              // 신청 제목
    private final StringProperty content;            // 상세 사유/비고
    private final IntegerProperty isGranted;         // 결재상태: 0=대기/1=승인/2=거절

    // ===================== 생성자 =====================
    public BaseRequestDTO(int requestId,
                          int employeeId,
                          String employeeName,
                          String department,
                          String position,
                          String approvalDate,
                          String startDate,
                          String endDate,
                          String requestType,
                          String scheduleType,    // ⬅️ 실제 스케줄타입(DB값)
                          String title,
                          String content,
                          int isGranted) {

        this.requestId = new SimpleIntegerProperty(requestId);
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.approvalDate = new SimpleStringProperty(approvalDate);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.requestType = new SimpleStringProperty(requestType);
        this.scheduleType = new SimpleStringProperty(scheduleType);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.isGranted = new SimpleIntegerProperty(isGranted);
    }

    // ===================== Getter/Setter (JavaFX TableView용 Property) =====================
    /** 요청 ID 반환 */
    public int getRequestId() { return requestId.get(); }
    public IntegerProperty requestIdProperty() { return requestId; }

    /** 사원번호 반환 */
    public int getEmployeeId() { return employeeId.get(); }
    public IntegerProperty employeeIdProperty() { return employeeId; }

    /** 사원명 반환 */
    public String getEmployeeName() { return employeeName.get(); }
    public StringProperty employeeNameProperty() { return employeeName; }

    /** 부서명 반환 */
    public String getDepartment() { return department.get(); }
    public StringProperty departmentProperty() { return department; }

    /** 직급 반환 */
    public String getPosition() { return position.get(); }
    public StringProperty positionProperty() { return position; }

    /** 결재일시 반환 */
    public String getApprovalDate() { return approvalDate.get(); }
    public StringProperty approvalDateProperty() { return approvalDate; }

    /** 시작일자 반환 */
    public String getStartDate() { return startDate.get(); }
    public StringProperty startDateProperty() { return startDate; }

    /** 종료일자 반환 */
    public String getEndDate() { return endDate.get(); }
    public StringProperty endDateProperty() { return endDate; }

    /** 화면 표시용 요청타입(휴가/출장 등) 반환 */
    public String getRequestType() { return requestType.get(); }
    public StringProperty requestTypeProperty() { return requestType; }

    /** 실제 DB기반 스케줄 종류(연차, 반차, 외근 등) 반환/설정 */
    public String getScheduleType() { return scheduleType.get(); }
    public StringProperty scheduleTypeProperty() { return scheduleType; }
    public void setScheduleType(String value) { scheduleType.set(value); }

    /** 제목 반환 */
    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }

    /** 상세 사유/비고 반환 */
    public String getContent() { return content.get(); }
    public StringProperty contentProperty() { return content; }

    /** 결재상태 반환/설정 (0=대기, 1=승인, 2=거절) */
    public int getIsGranted() { return isGranted.get(); }
    public void setIsGranted(int value) { isGranted.set(value); }
    public IntegerProperty isGrantedProperty() { return isGranted; }

    // ===================== 화면 표시용 상태 문자열 변환 =====================
    /**
     * 결재상태를 한글 문자로 반환: 대기/승인/거절 등
     */
    public String getStatusString() {
        switch (getIsGranted()) {
            case 0: return "대기";
            case 1: return "승인";
            case 2: return "거절";
            default: return "알 수 없음";
        }
    }
}
