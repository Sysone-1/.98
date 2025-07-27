package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.EmployeeDTO;
import com.sysone.ogamza.sql.user.EmployeeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * =============================================
 * 사원 DAO (EmployeeDAO)
 * =============================================
 * - DB에서 사원 정보를 조회하는 클래스
 * - 부서 ID를 기준으로 해당 부서의 모든 사원을 조회
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
public class EmployeeDAO {

    /**
     * ▶ 부서 ID로 사원 목록 조회
     *
     * @param deptId 조회할 부서 ID
     * @return 해당 부서에 소속된 사원 리스트
     */
    public List<EmployeeDTO> findByDepartmentId(int deptId) {
        List<EmployeeDTO> list = new ArrayList<>();
        String sql = EmployeeSQL.SELECT_EMPLOYEE;

        try (Connection conn = OracleConnector.getConnection();                  // DB 연결
             PreparedStatement pstmt = conn.prepareStatement(sql))              // SQL 준비
        {
            pstmt.setInt(1, deptId);                                             // 부서 ID 바인딩
            ResultSet rs = pstmt.executeQuery();                                 // 쿼리 실행

            while (rs.next()) {
                // 결과셋에서 DTO로 변환 후 리스트에 추가
                list.add(new EmployeeDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("department_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }

        return list;
    }

}
