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

/**
 * NFC 관련 기능 (사원 등록, 카드 정보 조회, 출입 로그 기록 등)을 처리하는 DAO 클래스입니다.
 * <p>
 * DB 연결 및 쿼리 실행을 통해 카드 UID 기반 사원 정보 관리, 접근 로그 저장 등을 수행합니다.
 *
 * @author 김민호
 */
public class NFCDAO {

    @Getter
    private static final NFCDAO instance = new NFCDAO();
    private NFCDAO() {}

    /**
     * 사원 정보를 데이터베이스에 저장합니다.
     *
     * @param dto 등록할 사원 정보 DTO
     * @return 등록된 사원의 사번 (email 기준 조회)
     */
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

    /**
     * 등록된 사원 정보를 카드 ID를 기준으로 삭제합니다.
     *
     * @param dto 삭제할 사원 정보 DTO
     * @return 삭제 후 해당 사원의 사번
     */
    public String deleteEmployeeByCardId(EmployeeCreateDTO dto) {
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

    /**
     * 부서 목록을 조회합니다.
     *
     * @return 부서 DTO 리스트
     */
    public List<DepartmentDTO> findAllDepartments() {
        List<DepartmentDTO> list = new ArrayList<>();
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FIND_DEPARTMENT);
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

    /**
     * 이메일을 기준으로 사번(ID)를 조회합니다.
     *
     * @param email 사원의 이메일
     * @return 사번 (문자열)
     */
    public String findEmpIdByEmail(String email) {
        int empId = 0;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FIND_EMP_ID);
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

    /**
     * 카드 ID로 사원의 이름을 조회합니다.
     *
     * @param cardId 카드 UID
     * @return 사원 이름 (존재하지 않으면 null)
     */
    public String findEmployeeNameByCardId(byte[] cardId) {
        String name = null;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FIND_EMP_INFO);
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

    /**
     * 정상 출입 로그를 저장합니다.
     *
     * @param empId 사원 ID
     * @return 저장 성공 여부
     */
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

    /**
     * 미인가 카드 접근 로그를 저장합니다.
     *
     * @return 저장 성공 여부
     */
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

    /**
     * 사원의 프로필 사진 경로를 조회합니다.
     *
     * @param empId 사원 ID
     * @return 프로필 이미지 경로 (null일 수 있음)
     */
    public String findProfileImagePath(int empId) {
        String dir = null;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(NFCSql.FIND_EMP_IMAGE);
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
