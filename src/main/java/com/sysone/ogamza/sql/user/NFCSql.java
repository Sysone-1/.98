package com.sysone.ogamza.sql.user;

/**
 * NFC 관련 기능에서 사용되는 SQL 쿼리들을 상수로 정의한 클래스입니다.
 * <p>사원 등록, 카드 UID 조회, 출입 로그 기록 등의 작업에 활용됩니다.</p>
 *
 * @author 김민호
 */
public class NFCSql {
    public static final String INSERT_EMPLOYEE =
            "INSERT INTO EMPLOYEE (DEPARTMENT_ID, NAME, EMAIL, POSITION, TEL, PASSWORD, " +
            "PIC_DIR, CARD_UID, TOTAL_VAC_NUM) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String DELETE_EMPLOYEE =
            "DELETE FROM EMPLOYEE " +
            "WHERE DBMS_LOB.COMPARE(CARD_UID, ?) = 0";

    public static final String FIND_DEPARTMENT =
            "SELECT * " +
            "FROM DEPARTMENT";

    public static final String FIND_EMP_ID =
            "SELECT ID " +
            "FROM EMPLOYEE " +
            "WHERE EMAIL = ?";

    public static final String FIND_EMP_INFO =
            "SELECT NAME " +
            "FROM EMPLOYEE " +
            "WHERE DBMS_LOB.COMPARE(CARD_UID, ?) = 0";

    public static final String INSERT_ACCESS_TIME =
            "INSERT INTO ACCESS_LOG (EMPLOYEE_ID, ACCESS_TIME) " +
            "VALUES (?, ?)";

    public static final String INSERT_UNAUTHORIZED_ACCESS_LOG =
            "INSERT INTO ACCESS_LOG (EMPLOYEE_ID, ACCESS_TIME) " +
            "VALUES (null, ?)";

    public static final String FIND_EMP_IMAGE =
            "SELECT PIC_DIR " +
            "FROM EMPLOYEE " +
            "WHERE ID = ?";
}
