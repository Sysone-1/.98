package com.sysone.ogamza.sql.user;

public class MemberSQL {
    public static final String SELECT_EMPLOYEE = "SELECT name, position, tel, password FROM employee WHERE email = ?";
    public static final String UPDATE_EMPLOYEE = "UPDATE employee SET password = ?, tel = ? WHERE email = ?";
}
