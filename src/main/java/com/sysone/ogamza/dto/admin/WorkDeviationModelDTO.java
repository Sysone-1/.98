package com.sysone.ogamza.dto.admin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * WorkDeviationModel
 * – 일일 근무 이탈자 통계 조회 기능용 모델 클래스
 */
public class WorkDeviationModelDTO {
    private final IntegerProperty empId = new SimpleIntegerProperty();
    private final StringProperty empName = new SimpleStringProperty();
    private final StringProperty deptName = new SimpleStringProperty();
    private final StringProperty deviationType = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    // 기본 생성자
    public WorkDeviationModelDTO() {}

    // 전체 필드 생성자
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

    // empId 프로퍼티
    public IntegerProperty empIdProperty() {
        return empId;
    }
    public int getEmpId() {
        return empId.get();
    }
    public void setEmpId(int empId) {
        this.empId.set(empId);
    }

    // empName 프로퍼티
    public StringProperty empNameProperty() {
        return empName;
    }
    public String getEmpName() {
        return empName.get();
    }
    public void setEmpName(String empName) {
        this.empName.set(empName);
    }

    // deptName 프로퍼티
    public StringProperty deptNameProperty() {
        return deptName;
    }
    public String getDeptName() {
        return deptName.get();
    }
    public void setDeptName(String deptName) {
        this.deptName.set(deptName);
    }

    // deviationType 프로퍼티
    public StringProperty deviationTypeProperty() {
        return deviationType;
    }
    public String getDeviationType() {
        return deviationType.get();
    }
    public void setDeviationType(String deviationType) {
        this.deviationType.set(deviationType);
    }

    // date 프로퍼티
    public StringProperty dateProperty() {
        return date;
    }
    public String getDate() {
        return date.get();
    }
    public void setDate(String date) {
        this.date.set(date);
    }
}
