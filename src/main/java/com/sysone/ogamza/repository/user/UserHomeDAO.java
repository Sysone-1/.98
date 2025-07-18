package com.sysone.ogamza.repository.user;

import com.sysone.ogamza.model.user.UserInfo;
import com.sysone.ogamza.sql.user.UserHomeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHomeDAO {
    private static final UserHomeDAO instance = new UserHomeDAO();
    private UserHomeDAO(){}

    public static UserHomeDAO getInstance(){return instance;}



    public int updateFortune(int num, String shape, String color, String msg, int employeeId)throws SQLException{

        String sql = UserHomeSQL.UPDATE_USER;

        try(Connection conn = OracleConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setString(1, color );
            pstmt.setInt(2,num);
            pstmt.setString(3, shape);
            pstmt.setString(4, msg);
            pstmt.setInt(5,employeeId);
            return pstmt.executeUpdate();
        }
    }

    public UserInfo getUserHome(int userId) throws SQLException{
        String sql = UserHomeSQL.SELECT_HOME;

        try(Connection conn = OracleConnector.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);){
            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if(resultSet.next()) {
                return UserInfo.builder()
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
