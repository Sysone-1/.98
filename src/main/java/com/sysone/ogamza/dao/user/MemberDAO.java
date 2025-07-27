

package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.MemberDetailDTO;
import com.sysone.ogamza.sql.user.MemberSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;
/**
 * ===========================================
 * 회원 정보 DAO (MemberDAO)
 * ===========================================
 * - DB와 직접 연결하여 회원 정보 조회 및 수정 수행
 * - 이메일로 사원 정보 조회
 * - 연락처 및 비밀번호 수정
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class MemberDAO {

    /**
     * ▶ 이메일을 기준으로 회원 상세 정보 조회
     *
     * @param email 조회할 사원의 이메일
     * @return 회원 정보 DTO (name, position, tel 포함)
     * @throws SQLException SQL 실행 중 예외 발생 시
     */
    public MemberDetailDTO findByEmail(String email) throws SQLException {
        String query = MemberSQL.SELECT_EMPLOYEE;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email); // 이메일 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 결과 존재 시 DTO 생성 후 반환
                    return new MemberDetailDTO(
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getString("tel"),
                            null // 비밀번호는 노출하지 않음
                    );
                }
            }
        }
        return null; // 결과 없을 경우 null 반환
    }

    /**
     * ▶ 이메일을 기준으로 회원 정보(비밀번호, 연락처) 수정
     *
     * @param email 수정할 대상 이메일
     * @param password 변경할 비밀번호
     * @param tel 변경할 연락처
     * @throws SQLException SQL 실행 중 예외 발생 시
     */
    public void updateByEmail(String email, String password, String tel) throws SQLException {
        String sql = MemberSQL.UPDATE_EMPLOYEE;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, password); // 첫 번째 파라미터: 비밀번호
            pstmt.setString(2, tel);      // 두 번째 파라미터: 연락처
            pstmt.setString(3, email);    // 세 번째 파라미터: 대상 이메일
            pstmt.executeUpdate();        // 업데이트 실행
        }
    }
}
