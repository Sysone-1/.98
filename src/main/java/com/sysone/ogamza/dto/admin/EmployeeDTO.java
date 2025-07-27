package com.sysone.ogamza.dto.admin;

/**
 * 사원 정보 DTO (Data Transfer Object)
 * - DB에서 조회한 사원 정보를 저장하는 객체
 * - 사원 기본 정보, 부서, 직급, 연락처, NFC 카드 UID 및 삭제 여부 포함
 * @author 허겸
 * @since 2025-07-21
 */
public class EmployeeDTO {

    // ===================== 사원 필드 =====================
    private int employeeId;          // 사원 ID (기본 키)
    private String employeeName;     // 사원 이름
    private String department;       // 소속 부서명
    private String position;         // 직급명
    private String email;            // 이메일 주소
    private String tel;              // 전화번호
    private String cardUid;          // NFC 카드 고유번호 (근태관리용)
    private int isDeleted;           // 삭제 여부 (0: 정상, 1: 삭제됨)

    // ===================== 기본 생성자 =====================
    /**
     * 기본 생성자
     * - 프레임워크나 라이브러리에서 필요할 경우 사용
     */
    public EmployeeDTO() {}

    // ===================== 필드 초기화용 생성자 =====================
    /**
     * DAO 조회 결과를 바탕으로 모든 필드를 초기화하는 생성자
     * - 파라미터 순서가 DB 쿼리 결과 순서와 일치해야 함
     *
     * @param employeeId   사원 ID
     * @param employeeName 사원 이름
     * @param department   부서명
     * @param position     직급명
     * @param email        이메일
     * @param tel          전화번호
     * @param cardUid      NFC 카드 UID
     * @param isDeleted    삭제 여부 (0=정상, 1=삭제됨)
     */
    public EmployeeDTO(int employeeId, String employeeName, String department,
                       String position, String email, String tel, String cardUid, int isDeleted) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.tel = tel;
        this.cardUid = cardUid;
        this.isDeleted = isDeleted;
    }

    // ===================== Getter & Setter =====================
    /** 사원 ID 반환 */
    public int getEmployeeId() { return employeeId; }
    /** 사원 ID 설정 */
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    /** 사원 이름 반환 */
    public String getEmployeeName() { return employeeName; }
    /** 사원 이름 설정 */
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    /** 부서명 반환 */
    public String getDepartment() { return department; }
    /** 부서명 설정 */
    public void setDepartment(String department) { this.department = department; }

    /** 직급명 반환 */
    public String getPosition() { return position; }
    /** 직급명 설정 */
    public void setPosition(String position) { this.position = position; }

    /** 이메일 반환 */
    public String getEmail() { return email; }
    /** 이메일 설정 */
    public void setEmail(String email) { this.email = email; }

    /** 전화번호 반환 */
    public String getTel() { return tel; }
    /** 전화번호 설정 */
    public void setTel(String tel) { this.tel = tel; }

    /** NFC 카드 UID 반환 */
    public String getCardUid() { return cardUid; }
    /** NFC 카드 UID 설정 */
    public void setCardUid(String cardUid) { this.cardUid = cardUid; }

    /** 삭제 여부 반환 (0=정상, 1=삭제됨) */
    public int getIsDeleted() { return isDeleted; }
    /** 삭제 여부 설정 */
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

    // ===================== 디버그용 문자열 변환 =====================
    /**
     * 객체 정보 문자열 반환 (주요 정보만 포함)
     * - 로그 출력, 디버깅 목적으로 사용
     */
    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", cardUid='" + cardUid + '\'' +
                '}';
    }
}
