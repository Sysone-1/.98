package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.sql.user.ScheduleSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    @Getter
    private static final ScheduleDAO instance = new ScheduleDAO();

    private ScheduleDAO() {}

    /**
        해당 주에 등록된 것 중 승인 된 리스트 조회
    */
    public ScheduleContentDTO findScheduleContentById(long id, int index) {
        List<ScheduleContentDTO> scheduleList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.FIND_SCHEDULE_GRANTED_CONTENT);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    scheduleList.add(new ScheduleContentDTO(
                            id,
                            rs.getString("TITLE"),
                            rs.getString("SCHEDULE_TYPE"),
                            rs.getTimestamp("START_DATE").toLocalDateTime(),
                            rs.getTimestamp("END_DATE").toLocalDateTime(),
                            rs.getString("CONTENT"),
                            rs.getInt("IS_GRANTED")
                    ));
                }
            }
            return (index >= 0 && index < scheduleList.size()) ? scheduleList.get(index) : null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
        결재 내역 조회
     */
    public List<ScheduleListDTO> findScheduleListById(long id) {
        List<ScheduleListDTO> scheduleList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.FIND_SCHEDULE_LIST);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    scheduleList.add(new ScheduleListDTO(
                            rs.getInt("ID"),
                            id,
                            rs.getString("TITLE"),
                            rs.getString("SCHEDULE_TYPE"),
                            rs.getTimestamp("APPROVAL_DATE").toLocalDateTime(),
                            rs.getTimestamp("START_DATE").toLocalDateTime(),
                            rs.getTimestamp("END_DATE").toLocalDateTime(),
                            rs.getInt("IS_GRANTED")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }

    /**
        일정 결재 상신
     */
    public boolean insertSchedule(ScheduleContentDTO dto) {

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.INSERT_SCHEDULE)) {

            pstmt.setLong(1, dto.getEmpId());
            pstmt.setDate(2, Date.valueOf(dto.getStartDate().toLocalDate()));
            pstmt.setDate(3, Date.valueOf(dto.getEndDate().toLocalDate()));
            pstmt.setString(4, dto.getScheduleType());
            pstmt.setString(5, dto.getTitle());
            pstmt.setString(6, dto.getContent());

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
        일정 결재 상신 취소
     */
    public boolean deleteScheduleById(long id, long scheduleId) {

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.CANCEL_SCHEDULE)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, scheduleId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     List<String>로 해당 주에 등록된 일정 반환 하는 함수
     */
    public List<ScheduleContentDTO> findSchedulesByEmpId(long id) {
        List<ScheduleContentDTO> scheduleList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.FIND_SCHEDULE_GRANTED_LIST);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scheduleList.add(new ScheduleContentDTO(
                            rs.getInt("ID"),
                            rs.getString("TITLE"),
                            rs.getString("SCHEDULE_TYPE"),
                            rs.getTimestamp("START_DATE").toLocalDateTime(),
                            rs.getTimestamp("END_DATE").toLocalDateTime(),
                            rs.getString("CONTENT"),
                            rs.getInt("IS_GRANTED")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }

    public void insertWorkingTime() {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ScheduleSql.INSERT_WORKING_TIME)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
