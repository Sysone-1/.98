package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.DashboardScheduleDTO;
import com.sysone.ogamza.sql.user.DashboardSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 대시보드 관련 데이터를 조회하는 DAO 클래스입니다.
 * <p>
 * 출퇴근 시간, 연차 정보, 근무 시간, 일정 등을 데이터베이스에서 조회합니다.
 * 싱글턴 패턴으로 구현되어 있으며, OracleConnector를 통해 쿼리를 실행합니다.
 *
 * @author 김민호
 */
public class DashboardDAO {
    @Getter
    private static final DashboardDAO instance = new DashboardDAO();

    private DashboardDAO() {}

    /**
     * 해당 사원의 금일 최초 출근 시간을 조회합니다.
     *
     * @param id 사원 ID
     * @return Optional 형태의 출근 시간
     */
    public Optional<LocalDateTime> findFirstAccessTimeByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_FIRST_ACCESS_LOG, LocalDateTime.class, id);
    }

    /**
     * 해당 사원의 금일 최종 퇴근 시간을 조회합니다.
     *
     * @param id 사원 ID
     * @return Optional 형태의 퇴근 시간
     */
    public Optional<LocalDateTime> findLastLeaveTimeByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_LAST_ACCESS_LOG, LocalDateTime.class, id);
    }

    /**
     * 해당 사원의 총 연차 개수를 조회합니다.
     * 값이 없으면 기본값 15를 반환합니다.
     *
     * @param id 사원 ID
     * @return 연차 개수
     */
    public int findVacationDaysByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_VACATION_NUM, Integer.class, id).orElse(15);
    }

    /**
     * 해당 사원이 사용한 연차 목록을 조회합니다.
     * 연차 타입과 시작/종료일을 포함합니다.
     *
     * @param id 사원 ID
     * @return 사용한 연차 정보를 담은 리스트
     */
    public List<HashMap<String, String>> findUsedVacationDaysByEmpId(long id) {
        List<HashMap<String, String>> usedVacationDaysList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DashboardSql.FIND_USED_VACATION_NUM);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usedVacationDaysList.add(new HashMap<String, String>() {{
                        put("type", rs.getString("SCHEDULE_TYPE"));
                        put("duration", rs.getTimestamp("START_DATE").toLocalDateTime() + "," + rs.getTimestamp("END_DATE").toLocalDateTime());
                    }});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usedVacationDaysList;
    }

    /**
     * 해당 사원의 이번 달 총 근무 시간을 분 단위로 조회합니다.
     *
     * @param id 사원 ID
     * @return 총 근무 시간 (분 단위)
     */
    public int findAllWorkTimeByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_WORKING_TIME, Integer.class, id).orElse(0);
    }

    /**
     * 해당 사원의 이번 달 연장 근무 시간을 분 단위로 조회합니다.
     *
     * @param id 사원 ID
     * @return 연장 근무 시간 (분 단위)
     */
    public int findAllExtendWorkTimeByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_EXTEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    /**
     * 해당 사원의 이번 달 휴일 근무 시간을 분 단위로 조회합니다.
     *
     * @param id 사원 ID
     * @return 휴일 근무 시간 (분 단위)
     */
    public int findAllWeekendWorkTimeByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_WEEKEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    /**
     * 해당 사원의 금주 일정 목록을 조회합니다.
     * 제목, 시작일, 승인 여부를 포함합니다.
     *
     * @param id 사원 ID
     * @return 일정 DTO 리스트
     */
    public List<DashboardScheduleDTO> findSchedulesByEmpId(long id) {
        List<DashboardScheduleDTO> scheduleList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DashboardSql.FIND_SCHEDULE_LIST);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scheduleList.add(new DashboardScheduleDTO(
                            rs.getString("TITLE"),
                            rs.getTimestamp("START_DATE").toLocalDateTime(),
                            rs.getInt("IS_GRANTED")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }
}
