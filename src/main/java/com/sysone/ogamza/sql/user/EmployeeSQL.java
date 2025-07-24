package com.sysone.ogamza.sql.user;

public class EmployeeSQL {
    public static final String SELECT_EMPLOYEE = "SELECT id, name, department_id FROM EMPLOYEE WHERE department_id = ?";
}
