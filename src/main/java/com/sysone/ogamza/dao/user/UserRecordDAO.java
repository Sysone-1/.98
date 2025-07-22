package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.UserRecordDTO;
import com.sysone.ogamza.sql.user.UserRecordSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRecordDAO {

    private static final UserRecordDAO instance = new UserRecordDAO();
    private UserRecordDAO(){}

    public static UserRecordDAO getInstance(){return instance;}

    public List<UserRecordDTO> getWorkingRecord (int employeeId) throws SQLException {
        String sql = UserRecordSQL.SELECT_RECORD;
        try(Connection conn = OracleConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, employeeId);
            ResultSet resultSet = pstmt.executeQuery();
            List<UserRecordDTO> response = new ArrayList<>();
            while (resultSet.next()){
                response.add(UserRecordDTO.builder()
                        .employeeId(resultSet.getInt("EMPLOYEE_ID"))
                        .name(resultSet.getString("NAME"))
                        .workDate(resultSet.getDate("ATTENDANCE_DATE"))
                        .checkInTime(resultSet.getString("CHECK_IN"))
                        .checkOutTime(resultSet.getString("CHECK_OUT"))
                        .workStatus(resultSet.getString("ATTENDANCE_STATUS"))
                        .build());
            }
            return response;
        }
    }
}
