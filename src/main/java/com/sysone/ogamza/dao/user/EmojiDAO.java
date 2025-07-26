
package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.sql.user.UserHomeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * 사용자 이모지 업데이트 기능을 담당하는 DAO 클래스입니다.
 * 주로 사용자 홈 화면에서 기분 이모지 선택 시 호출되어 해당 이모지를 DB에 반영합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class EmojiDAO {
    private static final EmojiDAO instance = new EmojiDAO();
    private EmojiDAO(){}
    public static EmojiDAO getInstance(){return instance;}

    /**
     * 사용자의 이모지를 업데이트합니다.
     *
     * @param employeeId 사원 ID
     * @param emoji 선택된 이모지 문자열
     * @return DB에 반영된 행 수 (1: 성공, 0: 실패)
     * @throws SQLException DB 작업 중 오류 발생 시
     */
    public int updateEmoji(int employeeId, String emoji) throws SQLException {
        String sql = UserHomeSQL.UPDATE_EMOJI;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emoji);
            pstmt.setInt(2, employeeId);
            return pstmt.executeUpdate();
        }
    }
}
