package com.sysone.ogamza.repository;

import com.sysone.ogamza.dto.DashboardScheduleDto;
import com.sysone.ogamza.sql.DashboardSql;
import com.sysone.ogamza.utils.db.OracleConnector;
import lombok.Getter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public int findUsedVacationDaysByEmpId(long id) {
        return OracleConnector.fetchData(DashboardSql.FIND_USED_VACATION_NUM, Integer.class, id).orElse(0);
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
    public List<DashboardScheduleDto> findSchedulesByDateAndEmpId(long id) {
        List<DashboardScheduleDto> scheduleList = new ArrayList<>();

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DashboardSql.FIND_SCHEDULE_LIST);
        ) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scheduleList.add(new DashboardScheduleDto(
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
