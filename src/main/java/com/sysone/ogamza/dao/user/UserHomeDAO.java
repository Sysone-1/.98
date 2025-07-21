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

public class UserHomeDAO {
    private static final UserHomeDAO instance = new UserHomeDAO();
    private UserHomeDAO(){}

    public static UserHomeDAO getInstance(){return instance;}


    // 럭키 데이터 업데이트를 위한 모든 사원의 id
    public List<Integer> findAllId() throws SQLException{
        String selectAll = UserHomeSQL.SELECT_ALL;

        try(Connection conn = OracleConnector.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(selectAll)){
            ResultSet result = pstmt.executeQuery();

            List<Integer> ids = new ArrayList<>();
            while (result.next()){
                int id = result.getInt("ID");
                ids.add(id);
            }
            return ids;
        }
    }


    // 럭키 데이터 모든 사원의 정보 bulk update
    public int updateFortune(List<TodayFortuneDTO> todayFortuneDTOS)throws SQLException{

        String insertTemp = UserHomeSQL.INSERT_TEMP;
        String mergeData = UserHomeSQL.MERGE_DATA;

        try(Connection conn = OracleConnector.getConnection();
            PreparedStatement insertPstmt = conn.prepareStatement(insertTemp);
            PreparedStatement mergePstmt = conn.prepareStatement(mergeData)){
            conn.setAutoCommit(false);

            for(TodayFortuneDTO data : todayFortuneDTOS) {
                insertPstmt.setInt(1, data.getEmployeeId());
                insertPstmt.setInt(2, data.getLuckyNumber());
                insertPstmt.setString(3, data.getLuckyShape());
                insertPstmt.setString(4, data.getLuckyColor());
                insertPstmt.setString(5, data.getRandomMessage());
                insertPstmt.addBatch();
            }
            // GTT에 반영
            insertPstmt.executeBatch();

            // bulk update
            int response = mergePstmt.executeUpdate();
            // 커밋해줘야 gtt 리셋 됨
            conn.commit();
            return response;
        }
    }
    // 유저 홈 정보 출력
    public UserInfoDTO getUserHome(int userId) throws SQLException{
        String sql = UserHomeSQL.SELECT_HOME;

        try(Connection conn = OracleConnector.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if(resultSet.next()) {
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
