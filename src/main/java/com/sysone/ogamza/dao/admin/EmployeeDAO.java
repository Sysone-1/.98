package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.dto.admin.EmployeeDTO;
import com.sysone.ogamza.utils.db.OracleConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /**
     * 모든 사원 조회 (BLOB 처리 개선)
     */
    public List<EmployeeDTO> getAllEmployees() {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = """
            SELECT 
                E.ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department_name,
                E.POSITION,
                E.EMAIL,
                E.TEL,
                E.CARD_UID,
                E.IS_DELETED
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE NVL(E.IS_DELETED, 0) = 0
            ORDER BY E.ID ASC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // BLOB 타입 안전 처리
                String cardUid = getCardUidSafely(rs, "CARD_UID");

                employees.add(new EmployeeDTO(
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("department_name"),
                        rs.getString("position"),
                        rs.getString("email"),
                        rs.getString("tel"),
                        cardUid,
                        rs.getInt("is_deleted")
                ));
            }
        } catch (SQLException e) {
            System.err.println("사원 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * 부서별 사원 조회 (BLOB 처리 개선)
     */
    public List<EmployeeDTO> getEmployeesByDepartment(String departmentName) {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = """
            SELECT 
                E.ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department_name,
                E.POSITION,
                E.EMAIL,
                E.TEL,
                E.CARD_UID,
                E.IS_DELETED
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE NVL(E.IS_DELETED, 0) = 0 AND D.NAME = ?
            ORDER BY E.ID ASC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departmentName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String cardUid = getCardUidSafely(rs, "CARD_UID");

                    employees.add(new EmployeeDTO(
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department_name"),
                            rs.getString("position"),
                            rs.getString("email"),
                            rs.getString("tel"),
                            cardUid,
                            rs.getInt("is_deleted")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("부서별 사원 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * 사원 검색 (BLOB 처리 개선)
     */
    public List<EmployeeDTO> searchEmployees(String keyword) {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = """
            SELECT 
                E.ID AS employee_id,
                E.NAME AS employee_name,
                D.NAME AS department_name,
                E.POSITION,
                E.EMAIL,
                E.TEL,
                E.CARD_UID,
                E.IS_DELETED
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT_ID = D.ID
            WHERE NVL(E.IS_DELETED, 0) = 0 
            AND (UPPER(E.NAME) LIKE UPPER(?) OR CAST(E.ID AS VARCHAR2(20)) LIKE ?)
            ORDER BY E.ID ASC
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String cardUid = getCardUidSafely(rs, "CARD_UID");

                    employees.add(new EmployeeDTO(
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("department_name"),
                            rs.getString("position"),
                            rs.getString("email"),
                            rs.getString("tel"),
                            cardUid,
                            rs.getInt("is_deleted")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("사원 검색 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * BLOB 타입 CARD_UID 안전 처리
     */
    private String getCardUidSafely(ResultSet rs, String columnName) {
        try {
            // BLOB 타입인 경우 바이트로 읽어서 문자열로 변환
            Blob blob = rs.getBlob(columnName);
            if (blob != null && blob.length() > 0) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                return new String(bytes, "UTF-8");
            }
            return ""; // null이나 빈 BLOB인 경우 빈 문자열 반환
        } catch (SQLException | java.io.UnsupportedEncodingException e) {
            // BLOB 처리 실패 시 문자열로 다시 시도
            try {
                String result = rs.getString(columnName);
                return result != null ? result : "";
            } catch (SQLException ex) {
                System.err.println("CARD_UID 읽기 실패: " + ex.getMessage());
                return ""; // 모든 방법이 실패하면 빈 문자열
            }
        }
    }

    /**
     * NFC 카드 UID 등록/수정 (BLOB 처리)
     */
    public boolean updateCardUid(int employeeId, String cardUid) {
        String sql = "UPDATE EMPLOYEE SET CARD_UID = ? WHERE ID = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 문자열을 BLOB으로 변환하여 저장
            if (cardUid != null && !cardUid.trim().isEmpty()) {
                byte[] bytes = cardUid.getBytes("UTF-8");
                stmt.setBytes(1, bytes);
            } else {
                stmt.setNull(1, Types.BLOB);
            }
            stmt.setInt(2, employeeId);

            int updatedRows = stmt.executeUpdate();
            return updatedRows > 0;

        } catch (SQLException | java.io.UnsupportedEncodingException e) {
            System.err.println("NFC 카드 등록 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 새 사원 등록 (BLOB 처리 개선)
     */
    public boolean insertEmployee(EmployeeDTO employee, int departmentId) {
        String sql = """
            INSERT INTO EMPLOYEE (
                ID, DEPARTMENT_ID, NAME, EMAIL, IS_ADMIN, POSITION, TEL, 
                PASSWORD, PIC_DIR, CARD_UID, TOTAL_VAC_NUM, 
                ALARM_1, ALARM_2, ALARM_3, IS_DELETED
            ) VALUES (
                ?, ?, ?, ?, 0, ?, ?, 'defaultpass123', '/images/default.jpg', 
                ?, 15, 0, 0, 0, 0
            )
            """;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employee.getEmployeeId());
            stmt.setInt(2, departmentId);
            stmt.setString(3, employee.getEmployeeName());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getPosition());
            stmt.setString(6, employee.getTel());

            // CARD_UID BLOB 처리
            String cardUid = employee.getCardUid();
            if (cardUid != null && !cardUid.trim().isEmpty()) {
                byte[] bytes = cardUid.getBytes("UTF-8");
                stmt.setBytes(7, bytes);
            } else {
                stmt.setNull(7, Types.BLOB);
            }

            int insertedRows = stmt.executeUpdate();
            return insertedRows > 0;

        } catch (SQLException | java.io.UnsupportedEncodingException e) {
            System.err.println("사원 등록 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 모든 부서 목록 조회
     */
    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT NAME FROM DEPARTMENT ORDER BY ID";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                departments.add(rs.getString("NAME"));
            }
        } catch (SQLException e) {
            System.err.println("부서 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return departments;
    }

    /**
     * 부서 ID 조회
     */
    public int getDepartmentId(String departmentName) {
        String sql = "SELECT ID FROM DEPARTMENT WHERE NAME = ?";

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departmentName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("부서 ID 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
