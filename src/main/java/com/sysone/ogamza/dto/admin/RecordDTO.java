package com.sysone.ogamza.dto.admin;

import java.time.LocalDateTime;

/**
 * 출입 기록 정보를 담는 엔티티 클래스
 *
 * <p>해당 클래스는 DB에서 조회한 출입 기록 데이터를 보관하며,
 * JavaFX TableView에 바인딩되도록 Getter만 정의되어 있습니다.</p>
 *
 * @author 조윤상
 */
public class RecordDTO {

    private long employeeId;
    private String employeeName;
    private String departmentName;
    private String position;
    private LocalDateTime taggingTime;
    private String approvalStatus;

    /**
     * 모든 필드를 포함하는 생성자
     *
     * @param employeeId     사원 ID
     * @param employeeName   사원 이름
     * @param departmentName 부서 이름
     * @param position       직급
     * @param taggingTime    출입 시각
     * @param approvalStatus 출입 승인 여부
     */
    public RecordDTO(long employeeId, String employeeName, String departmentName,
                     String position, LocalDateTime taggingTime, String approvalStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.position = position;
        this.taggingTime = taggingTime;
        this.approvalStatus = approvalStatus;
    }

    /** 기본 생성자 (직렬화 혹은 JavaFX 내부 처리용) */
    public RecordDTO() {}

    /** @return 사원 ID */
    public long getEmployeeId() {
        return employeeId;
    }

    /** @return 사원 이름 */
    public String getEmployeeName() {
        return employeeName;
    }

    /** @return 부서 이름 */
    public String getDepartmentName() {
        return departmentName;
    }

    /** @return 직급 */
    public String getPosition() {
        return position;
    }

    /** @return 출입 시각 */
    public LocalDateTime getTaggingTime() {
        return taggingTime;
    }

    /** @return 출입 승인 여부 */
    public String getApprovalStatus() {
        return approvalStatus;
    }
}
