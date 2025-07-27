package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.MemberDAO;
import com.sysone.ogamza.dto.user.MemberDetailDTO;

import java.sql.SQLException;
/**
 * ===========================================
 * 회원 서비스 클래스 (MemberService)
 * ===========================================
 * - DAO와 Controller 사이에서 비즈니스 로직을 담당
 * - 회원 정보 조회 및 수정 기능 제공
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */

public class MemberService {

    // 회원 관련 DB 작업을 수행하는 DAO
    private final MemberDAO memberDAO = new MemberDAO();

    /**
     * ▶ 이메일로 회원 상세 정보 조회
     *
     * @param email 사용자 이메일
     * @return MemberDetailDTO (이름, 직책, 전화번호 등 포함)
     * @throws SQLException DB 조회 중 오류 발생 시
     */
    public MemberDetailDTO getMemberDetail(String email) throws SQLException {
        return memberDAO.findByEmail(email);
    }

    /**
     * ▶ 회원 정보 수정 (비밀번호 및 연락처)
     *
     * @param email 사용자 이메일 (기준 값)
     * @param newPassword 새 비밀번호
     * @param tel 새 연락처
     * @throws SQLException DB 업데이트 중 오류 발생 시
     */
    public void updateMember(String email, String newPassword, String tel) throws SQLException {
        // 비밀번호가 공란이라면 DAO에서 내부 처리 (해당 로직은 DAO 쪽에 위임)
        memberDAO.updateByEmail(email, newPassword, tel);
    }
}
