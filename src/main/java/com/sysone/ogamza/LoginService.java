/**
 * ===========================================
 * 로그인 서비스 (LoginService)
 * ===========================================
 * - 로그인 요청을 처리하는 서비스 계층 클래스
 * - DAO를 호출하여 사용자 정보를 받아옴
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

package com.sysone.ogamza;

import java.sql.SQLException;

public class LoginService {

    // ▶ 로그인 처리를 위한 DAO 의존성
    private final LoginDAO loginDAO = new LoginDAO();

    /**
     * ▶ 로그인 요청 처리
     *
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @return LoginUserDTO 로그인 성공 시 사용자 정보 객체, 실패 시 null
     * @throws SQLException DAO 내부에서 발생할 수 있는 DB 예외
     */
    public LoginUserDTO login(String email, String password) throws SQLException {
        return loginDAO.getUserInfo(email, password);
    }
}
