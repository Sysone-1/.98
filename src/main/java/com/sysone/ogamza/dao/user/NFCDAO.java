package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeCreateDTO;
import com.sysone.ogamza.sql.user.NFCSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NFCDAO {

    @Getter
    private static final NFCDAO instance = new NFCDAO();
    private NFCDAO() {}

    public String insertEmployee(EmployeeCreateDTO dto) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.INSERT_EMPLOYEE)
        ) {

            pstmt.setInt(1, dto.getDepartmentId());
            pstmt.setString(2, dto.getName());
            pstmt.setString(3, dto.getEmail());
            pstmt.setString(4, dto.getPosition());
            pstmt.setString(5, dto.getTelNum());
            pstmt.setString(6, dto.getPassword());
            pstmt.setString(7, dto.getPicDir());
            pstmt.setBytes(8, dto.getCardId());
            pstmt.setInt(9, dto.getVacNum());

            int result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return findEmpIdByEmail(dto.getEmail());
    }

    public String deleteEmployee(EmployeeCreateDTO dto) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.DELETE_EMPLOYEE)
        ) {

            pstmt.setBytes(1, dto.getCardId());

            int result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return findEmpIdByEmail(dto.getEmail());
    }

    public List<DepartmentDTO> findDepartment() {
        List<DepartmentDTO> list = new ArrayList<>();
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FETCH_DEPARTMENT);
        ) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                        list.add(new DepartmentDTO(
                                rs.getInt("ID"),
                                rs.getString("NAME")
                        ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String findEmpIdByEmail(String email) {
        int empId = 0;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FETCH_EMP_ID);
        ) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    empId = rs.getInt("ID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(empId);
    }

    public String findEmployeeById(byte[] cardId) {
        String name = null;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FETCH_EMP_INFO);
        ) {
            pstmt.setBytes(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                     name = rs.getString("NAME");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public boolean insertAccessTime(int empId) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.INSERT_ACCESS_TIME)
        ) {

            pstmt.setLong(1, empId);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertUnauthorizedAccessTime() {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.INSERT_UNAUTHORIZED_ACCESS_LOG)
        ) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String findPicdir(int empId) {
        String dir = null;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FETCH_EMP_IMAGE);
        ) {

            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    dir = rs.getString("PIC_DIR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir;
    }
}
