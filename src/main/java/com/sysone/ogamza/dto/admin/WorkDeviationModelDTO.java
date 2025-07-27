package com.sysone.ogamza.dto.admin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * WorkDeviationModelDTO
 * – 일일 근무 이탈자 (근태 이탈) 통계 조회용 모델 클래스
 * – JavaFX Property 를 이용한 TableView 등 UI 바인딩 지원용 DTO
 * – 사원 정보, 근무 부서, 이탈 유형, 이탈 날짜 정보를 담음
 * @author 허겸
 * @since 2025-07-19
 */
public class WorkDeviationModelDTO {

    // ===================== 프로퍼티 필드 정의 (JavaFX 바인딩용) =====================
    private final IntegerProperty empId = new SimpleIntegerProperty();       // 사원 ID
    private final StringProperty empName = new SimpleStringProperty();       // 사원 이름
    private final StringProperty deptName = new SimpleStringProperty();      // 부서명
    private final StringProperty deviationType = new SimpleStringProperty(); // 근무 이탈 유형 (예: 지각, 조퇴 등)
    private final StringProperty date = new SimpleStringProperty();          // 이탈 발생 일자 (YYYY-MM-DD 등 문자열)

    // ===================== 생성자 =====================
    /**
     * 기본 생성자
     * - JavaFX Property 초기값은 기본형 기본값으로 세팅됨 (0 또는 빈 문자열)
     * - 후속 setter 호출 또는 프로퍼티 바인딩으로 값 설정 가능
     */
    public WorkDeviationModelDTO() {}

    /**
     * 모든 필드 값을 직접 초기화하는 생성자
     *
     * @param empId          사원 ID
     * @param empName        사원 이름
     * @param deptName       소속 부서명
     * @param deviationType  근무 이탈 유형 (ex: 지각, 무단 결근 등)
     * @param date           이탈 발생 날짜 (포맷 예: "2025-07-27")
     */
    public WorkDeviationModelDTO(int empId,
                                 String empName,
                                 String deptName,
                                 String deviationType,
                                 String date) {
        this.empId.set(empId);
        this.empName.set(empName);
        this.deptName.set(deptName);
        this.deviationType.set(deviationType);
        this.date.set(date);
    }

    // ===================== empId 프로퍼티 =====================

    /** empId 프로퍼티(JavaFX 바인딩용) 반환 */
    public IntegerProperty empIdProperty() {
        return empId;
    }

    /** empId 값 반환 */
    public int getEmpId() {
        return empId.get();
    }

    /** empId 값 설정 */
    public void setEmpId(int empId) {
        this.empId.set(empId);
    }

    // ===================== empName 프로퍼티 =====================

    /** empName 프로퍼티(JavaFX 바인딩용) 반환 */
    public StringProperty empNameProperty() {
        return empName;
    }

    /** empName 값 반환 */
    public String getEmpName() {
        return empName.get();
    }

    /** empName 값 설정 */
    public void setEmpName(String empName) {
        this.empName.set(empName);
    }

    // ===================== deptName 프로퍼티 =====================

    /** deptName 프로퍼티(JavaFX 바인딩용) 반환 */
    public StringProperty deptNameProperty() {
        return deptName;
    }

    /** deptName 값 반환 */
    public String getDeptName() {
        return deptName.get();
    }

    /** deptName 값 설정 */
    public void setDeptName(String deptName) {
        this.deptName.set(deptName);
    }

    // ===================== deviationType 프로퍼티 =====================

    /** deviationType 프로퍼티(JavaFX 바인딩용) 반환 */
    public StringProperty deviationTypeProperty() {
        return deviationType;
    }

    /** deviationType 값 반환 */
    public String getDeviationType() {
        return deviationType.get();
    }

    /** deviationType 값 설정 */
    public void setDeviationType(String deviationType) {
        this.deviationType.set(deviationType);
    }

    // ===================== date 프로퍼티 =====================

    /** date 프로퍼티(JavaFX 바인딩용) 반환 */
    public StringProperty dateProperty() {
        return date;
    }

    /** date 값 반환 */
    public String getDate() {
        return date.get();
    }

    /** date 값 설정 */
    public void setDate(String date) {
        this.date.set(date);
    }
}
