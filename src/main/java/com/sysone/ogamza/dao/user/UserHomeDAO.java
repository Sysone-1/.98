
package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.TodayFortuneDTO;
import com.sysone.ogamza.dto.user.UserInfoDTO;
import com.sysone.ogamza.sql.user.UserHomeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 유저 홈 화면에 필요한 정보를 조회 및 갱신하는 DAO 클래스입니다.
 *
 * - 전체 사원의 ID 목록 조회 (럭키 정보 갱신용)
 * - 사원의 행운 정보 일괄 업데이트 (GTT → MERGE)
 * - 로그인 사용자의 홈 화면 정보 조회
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class UserHomeDAO {
    private static final UserHomeDAO instance = new UserHomeDAO();
    private UserHomeDAO() {}
    public static UserHomeDAO getInstance() { return instance; }

    /**
     * 전체 사원의 ID 목록을 조회합니다.
     * (오늘의 행운 데이터 갱신을 위해 사용)
     *
     * @return 사원 ID 목록
     * @throws SQLException DB 조회 오류
     */
    public List<Integer> findAllId() throws SQLException {
        String selectAll = UserHomeSQL.SELECT_ALL;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectAll)) {

            ResultSet result = pstmt.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (result.next()) {
                ids.add(result.getInt("ID"));
            }
            return ids;
        }
    }

    /**
     * 전달받은 오늘의 행운 리스트를 기반으로 GTT에 insert 후 MERGE로 일괄 업데이트합니다.
     *
     * @param todayFortunes 오늘의 행운 데이터 리스트
     * @return 업데이트된 사원 수
     * @throws SQLException DB 업데이트 오류
     */
    public int updateFortune(List<TodayFortuneDTO> todayFortunes) throws SQLException {
        String insertTemp = UserHomeSQL.INSERT_TEMP;
        String mergeData = UserHomeSQL.MERGE_DATA;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement insertPstmt = conn.prepareStatement(insertTemp);
             PreparedStatement mergePstmt = conn.prepareStatement(mergeData)) {

            conn.setAutoCommit(false);

            for (TodayFortuneDTO data : todayFortunes) {
                insertPstmt.setInt(1, data.getEmployeeId());
                insertPstmt.setInt(2, data.getLuckyNumber());
                insertPstmt.setString(3, data.getLuckyShape());
                insertPstmt.setString(4, data.getLuckyColor());
                insertPstmt.setString(5, data.getRandomMessage());
                insertPstmt.addBatch();
            }
            insertPstmt.executeBatch(); // GTT에 데이터 삽입

            int response = mergePstmt.executeUpdate(); // MERGE 실행
            conn.commit();
            return response;
        }
    }

    /**
     * 로그인 사용자의 홈 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 유저 홈 화면에 필요한 정보 DTO
     * @throws SQLException DB 조회 오류
     */
    public UserInfoDTO getUserHome(int userId) throws SQLException {
        String sql = UserHomeSQL.SELECT_HOME;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return UserInfoDTO.builder()
                        .name(resultSet.getString("employeeName"))
                        .departmentName(resultSet.getString("deptName"))
                        .profile(resultSet.getString("pic_dir"))
                        .luckyColor(resultSet.getString("lucky_color"))
                        .luckyNumber(resultSet.getInt("lucky_number"))
                        .luckyShape(resultSet.getString("lucky_shape"))
                        .randomMessage(resultSet.getString("random_message"))
                        .emoji(resultSet.getString("mood_emoji"))
                        .build();
            }
            return null;
        }
    }
}
