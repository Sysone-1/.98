/**
 * ===========================================
 * 로그인 DAO (LoginDAO)
 * ===========================================
 * - DB에 접속하여 사용자의 로그인 정보를 조회하는 클래스
 * - 이메일과 비밀번호를 바탕으로 로그인 사용자 정보 반환
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

package com.sysone.ogamza;

import com.sysone.ogamza.sql.LoginSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.*;

public class LoginDAO {

    /**
     * ▶ 사용자의 로그인 정보를 데이터베이스에서 조회
     *
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return LoginUserDTO 로그인 성공 시 사용자 정보 객체, 실패 시 null
     * @throws SQLException DB 연결 및 쿼리 수행 중 오류 발생 가능
     */
    public LoginUserDTO getUserInfo(String email, String password) throws SQLException {
        // SQL 쿼리문
        String query = LoginSQL.SELECT_LOGIN;

        // DB 연결 및 자원 자동 반환 처리
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 쿼리 파라미터 바인딩
            stmt.setString(1, email);
            stmt.setString(2, password);

            // 쿼리 실행 및 결과 처리
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // card_uid가 BLOB 타입일 경우 변환 처리
                    Blob blob = rs.getBlob("card_uid");
                    String cardUid = blob != null
                            ? new String(blob.getBytes(1, (int) blob.length()))
                            : null;

                    // 사용자 정보 객체 생성 및 반환
                    return new LoginUserDTO(
                            rs.getInt("id"),
                            rs.getString("dept_name"),
                            rs.getString("position"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getInt("is_admin"),
                            cardUid,
                            rs.getString("pic_dir")
                    );
                }
            }
        }

        // 로그인 실패 시 null 반환
        return null;
    }
}
