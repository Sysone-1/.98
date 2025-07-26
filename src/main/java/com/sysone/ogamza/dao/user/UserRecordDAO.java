
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
/**
 * 근무자 개인의 근태 기록을 조회하는 DAO 클래스입니다.
 * 사원의 출퇴근 시간, 근무일, 근태 상태 등의 정보를 조회하여 반환합니다.
 *
 * - 직원 ID를 기준으로 해당 사원의 근태 이력을 가져옵니다.
 * - 근무일, 출근시간, 퇴근시간, 근태상태 등의 정보를 포함합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class UserRecordDAO {

    private static final UserRecordDAO instance = new UserRecordDAO();
    private UserRecordDAO() {}

    public static UserRecordDAO getInstance() { return instance; }

    /**
     * 해당 사원의 근무 이력을 조회합니다.
     *
     * @param employeeId 조회할 사원의 ID
     * @return UserRecordDTO 리스트 (근무일, 출퇴근 시간, 근태 상태 등)
     * @throws SQLException DB 조회 중 오류 발생 시
     */
    public List<UserRecordDTO> getWorkingRecord(int employeeId) throws SQLException {
        String sql = UserRecordSQL.SELECT_RECORD;
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet resultSet = pstmt.executeQuery();
            List<UserRecordDTO> response = new ArrayList<>();
            while (resultSet.next()) {
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
