package com.sysone.ogamza;
/**
 * ===========================================
 * 전역 세션 관리 클래스 (Session)
 * ===========================================
 * - Singleton 패턴으로 로그인 사용자 정보를 애플리케이션 전체에서 공유
 * - 로그인 시 사용자 정보를 저장하고, 로그아웃 시 초기화
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class Session {

    // ▶ Singleton 인스턴스
    public static Session instance;

    // ▶ 현재 로그인한 사용자 정보
    private LoginUserDTO loginUser;

    // ▶ 생성자를 private 으로 막아 외부에서 직접 생성 못하도록 함
    private Session() {}

    /**
     * ▶ 싱글톤 인스턴스 반환
     * - 최초 요청 시 인스턴스를 생성하고, 이후에는 동일 객체 반환
     *
     * @return Session 인스턴스
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * ▶ 로그인 사용자 정보 저장
     *
     * @param loginUser 로그인한 사용자 DTO 객체
     */
    public void setLoginUser(LoginUserDTO loginUser) {
        this.loginUser = loginUser;
    }

    /**
     * ▶ 현재 로그인한 사용자 정보 반환
     *
     * @return 로그인 사용자 DTO
     */
    public LoginUserDTO getLoginUser() {
        return loginUser;
    }

    /**
     * ▶ 세션 초기화 (로그아웃 시 사용)
     * - 로그인 사용자 정보를 제거
     */
    public void clear() {
        loginUser = null;
    }
}
