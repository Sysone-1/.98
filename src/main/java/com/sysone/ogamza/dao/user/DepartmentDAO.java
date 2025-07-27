package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.sql.user.DepartmentSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * =============================================
 * 부서 DAO 클래스 (DepartmentDAO)
 * =============================================
 * - 부서 관련 DB 접근 기능 담당
 * - 현재는 전체 부서 목록 조회 기능만 구현되어 있음
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */

public class DepartmentDAO {

    /**
     * ▶ 전체 부서 조회
     * - `employee_department` 테이블에서 모든 부서를 조회하여 리스트로 반환
     *
     * @return List<DepartmentDTO> : 부서 목록
     */
    public List<DepartmentDTO> findAll() {
        List<DepartmentDTO> list = new ArrayList<>();
        String sql = DepartmentSQL.SELECT_DEPARTMENT;

        try (
                Connection conn = OracleConnector.getConnection();              // DB 연결
                PreparedStatement pstmt = conn.prepareStatement(sql);          // 쿼리 준비
                ResultSet rs = pstmt.executeQuery()                             // 실행 및 결과 저장
        ) {
            // 결과 순회하며 DTO로 변환
            while (rs.next()) {
                list.add(new DepartmentDTO(
                        rs.getInt("id"),        // 부서 ID
                        rs.getString("name")    // 부서 이름
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 발생 시 콘솔 출력
        }

        return list;
    }
}
