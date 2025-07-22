package com.sysone.ogamza.dto.admin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * SCHEDULE 테이블의 주요 컬럼을 그대로 매핑하는 BaseRequest 클래스 (isGranted 기반)
 */
public class BaseRequestDTO {
    private final IntegerProperty requestId;      // S.ID
    private final IntegerProperty employeeId;     // S.EMPLOYEE_ID
    private final StringProperty employeeName;    // S.EMPLOYEE.NAME
    private final StringProperty department;
    private final StringProperty position;
    private final StringProperty approvalDate;    // S.APPROVAL_DATE
    private final StringProperty startDate;       // S.START_DATE
    private final StringProperty endDate;         // S.END_DATE
    private final StringProperty requestType;     // S.SCHEDULE_TYPE (연차, 출퇴근변경, 출장)
    private final StringProperty title;           // S.TITLE
    private final StringProperty content;         // S.CONTENT
    private final IntegerProperty isGranted;      // S.IS_GRANTED (0=대기, 1=승인, 2=거절)

    public BaseRequestDTO(int requestId,
                          int employeeId,
                          String employeeName,
                          String department,
                          String position,
                          String approvalDate,
                          String startDate,
                          String endDate,
                          String requestType,
                          String title,
                          String content,
                          int isGranted) // <- status가 아니라 int isGranted로 변경
    {
        this.requestId     = new SimpleIntegerProperty(requestId);
        this.employeeId    = new SimpleIntegerProperty(employeeId);
        this.employeeName  = new SimpleStringProperty(employeeName);
        this.department    = new SimpleStringProperty(department);
        this.position      = new SimpleStringProperty(position);
        this.approvalDate  = new SimpleStringProperty(approvalDate);
        this.startDate     = new SimpleStringProperty(startDate);
        this.endDate       = new SimpleStringProperty(endDate);
        this.requestType   = new SimpleStringProperty(requestType);
        this.title         = new SimpleStringProperty(title);
        this.content       = new SimpleStringProperty(content);
        this.isGranted     = new SimpleIntegerProperty(isGranted);
    }

    public int getRequestId() {
        return requestId.get();
    }
    public IntegerProperty requestIdProperty() {
        return requestId;
    }

    public int getEmployeeId() {
        return employeeId.get();
    }
    public IntegerProperty employeeIdProperty() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName.get();
    }
    public StringProperty employeeNameProperty() {
        return employeeName;
    }

    public String getDepartment() {
        return department.get();
    }
    public StringProperty departmentProperty() {
        return department;
    }

    public String getPosition() {
        return position.get();
    }
    public StringProperty positionProperty() {
        return position;
    }

    public String getApprovalDate() {
        return approvalDate.get();
    }
    public StringProperty approvalDateProperty() {
        return approvalDate;
    }

    public String getStartDate() {
        return startDate.get();
    }
    public StringProperty startDateProperty() {
        return startDate;
    }

    public String getEndDate() {
        return endDate.get();
    }
    public StringProperty endDateProperty() {
        return endDate;
    }

    public String getRequestType() {
        return requestType.get();
    }
    public StringProperty requestTypeProperty() {
        return requestType;
    }

    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }

    public String getContent() {
        return content.get();
    }
    public StringProperty contentProperty() {
        return content;
    }

    // === 결재 상태 ===

    public int getIsGranted() {
        return isGranted.get();
    }
    public void setIsGranted(int value) {
        isGranted.set(value);
    }
    public IntegerProperty isGrantedProperty() {
        return isGranted;
    }

    /**
     * 테이블 및 화면표시용 상태 문자열 반영: 0=대기, 1=승인, 2=거절
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
