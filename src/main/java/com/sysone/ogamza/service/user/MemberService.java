package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.MemberDAO;
import com.sysone.ogamza.dto.user.MemberDetailDTO;

import java.sql.SQLException;

public class MemberService {
    private final MemberDAO memberDAO = new MemberDAO();
    
    //이메일로 회원 정보 조회
    public MemberDetailDTO getMemberDetail(String email) throws SQLException {
        return memberDAO.findByEmail(email);
    }

    public void updateMember(String email, String newPassword, String tel) throws SQLException{
        //비밀번호가 공란이면, 비밀번호 제외하고 연락처만 업데이트

        memberDAO.updateByEmail(email,newPassword,tel);
    }
}
