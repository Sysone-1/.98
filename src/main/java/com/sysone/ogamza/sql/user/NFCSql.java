package com.sysone.ogamza.sql.user;

public class NFCSql {
    public static final String INSERT_EMPLOYEE =
            "INSERT INTO EMPLOYEE (DEPARTMENT_ID, NAME, EMAIL, POSITION, TEL, PASSWORD, " +
            "PIC_DIR, CARD_UID, TOTAL_VAC_NUM) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String DELETE_EMPLOYEE =
            "DELETE FROM EMPLOYEE " +
            "WHERE DBMS_LOB.COMPARE(CARD_UID, ?) = 0";

    public static final String FETCH_DEPARTMENT =
            "SELECT * " +
            "FROM DEPARTMENT";

    public static final String FETCH_EMP_ID =
            "SELECT ID " +
            "FROM EMPLOYEE " +
            "WHERE EMAIL = ?";

    public static final String FETCH_EMP_INFO =
            "SELECT NAME " +
            "FROM EMPLOYEE " +
            "WHERE DBMS_LOB.COMPARE(CARD_UID, ?) = 0";

    public static final String INSERT_ACCESS_TIME =
            "INSERT INTO ACCESS_LOG (EMPLOYEE_ID, ACCESS_TIME) " +
            "VALUES (?, ?)";

    public static final String INSERT_UNAUTHORIZED_ACCESS_LOG =
            "INSERT INTO ACCESS_LOG (EMPLOYEE_ID, ACCESS_TIME) " +
            "VALUES (null, ?)";

    public static final String FETCH_EMP_IMAGE =
            "SELECT PIC_DIR " +
            "FROM EMPLOYEE " +
            "WHERE ID = ?";
}
