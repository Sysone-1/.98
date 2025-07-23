package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.DashboardScheduleDTO;
import com.sysone.ogamza.sql.user.DashboardSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DashboardDAO {
    @Getter
    private static final DashboardDAO instance = new DashboardDAO();

    private DashboardDAO() {}

    public Optional<LocalDateTime> findFirstAccessTimeByDateAndId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_FIRST_ACCESS_LOG, LocalDateTime.class, id);
    }

    public Optional<LocalDateTime> findLastLeaveTimeByDateAndId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_LAST_ACCESS_LOG, LocalDateTime.class, id);
    }

    public int findVacationDaysByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_VACATION_NUM, Integer.class, id).orElse(15);
    }

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

    public int findAllWorkTimeByDateAndEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_WORKING_TIME, Integer.class, id).orElse(0);
    }

    public int findAllExtendWorkTimeByDateAndEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_EXTEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    public int findAllWeekendWorkTimeByDateAndEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_TOTAL_WEEKEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    /**
        List<String>로 해당 주에 등록된 일정 반환 하는 함수
    */
    public List<DashboardScheduleDTO> findSchedulesByDateAndEmpId(long id) {
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
