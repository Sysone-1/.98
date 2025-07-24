package com.sysone.ogamza.dto.admin;

public class EmployeeDTO {
    private int employeeId;
    private String employeeName;
    private String department;
    private String position;
    private String email;
    private String tel;
    private String cardUid;
    private int isDeleted;

    // 기본 생성자
    public EmployeeDTO() {}

    // DAO 조회 결과용 생성자 (순서 중요!)
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

    // Getter & Setter 메서드들
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getCardUid() { return cardUid; }
    public void setCardUid(String cardUid) { this.cardUid = cardUid; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

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
