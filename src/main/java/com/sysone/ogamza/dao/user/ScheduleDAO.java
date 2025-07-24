package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.sql.user.ScheduleSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 일정 관련 데이터를 처리하는 DAO 클래스입니다.
 * <p>
 * 일정 상신, 조회, 취소 등과 관련된 데이터베이스 작업을 수행합니다.
 * OracleConnector를 사용하여 DB와 통신하며, 싱글턴 패턴으로 구현되어 있습니다.
 *
 * @author 김민호
 */
public class ScheduleDAO {
    @Getter
    private static final ScheduleDAO instance = new ScheduleDAO();

    private ScheduleDAO() {}

    /**
     * 해당 사원의 이번 주 승인된 일정 중 특정 인덱스에 해당하는 상세 일정을 조회합니다.
     *
     * @param id 사원 ID
     * @param index 인덱스 (0부터 시작)
     * @return 해당 인덱스의 ScheduleContentDTO 또는 null
     */
    public ScheduleContentDTO findWeeklyGrantedScheduleByEmpIdAndIndex(long id, int index) {
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
     * 해당 사원의 전체 결재 내역을 조회합니다.
     *
     * @param id 사원 ID
     * @return 일정 리스트 DTO 목록
     */
    public List<ScheduleListDTO> findAllSchedulesByEmpId(long id) {
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
     * 일정 결재 상신 요청을 데이터베이스에 저장합니다.
     *
     * @param dto 일정 DTO
     * @return 저장 성공 여부
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
     * 해당 사원의 특정 일정 결재 상신 요청을 취소합니다.
     *
     * @param id 사원 ID
     * @param scheduleId 일정 ID
     * @return 취소 성공 여부
     */
    public boolean cancelScheduleRequestByEmpIdAndScheduleId (long id, long scheduleId) {

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ScheduleSql.UPDATE_CANCEL_SCHEDULE)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, scheduleId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 승인된 일정 중 해당 사원의 이번 주 일정 목록을 조회합니다.
     *
     * @param id 사원 ID
     * @return 일정 DTO 리스트
     */
    public List<ScheduleContentDTO> findThisWeekGrantedSchedulesByEmpId (long id) {
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

    /**
     * 매일 23:50에 근무 시간을 일괄 저장하는 쿼리를 실행합니다.
     */
    public void batchInsertDailyWorkingTime () {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ScheduleSql.INSERT_WORKING_TIME)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
