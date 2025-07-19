package com.sysone.ogamza.repository;

import com.sysone.ogamza.sql.DashboardSql;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DashboardDAO {
    private static final DashboardDAO instance = new DashboardDAO();

    private DashboardDAO() {}

    public static DashboardDAO getInstance() {
        return instance;
    }

    public Optional<LocalDateTime> findFirstAccessTimeByDateAndId(long id) {
        return fetchData(DashboardSql.FIND_FIRST_ACCESS_LOG, LocalDateTime.class, id);
    }

    public Optional<LocalDateTime> findLastLeaveTimeByDateAndId(long id) {
        return fetchData(DashboardSql.FIND_LAST_ACCESS_LOG, LocalDateTime.class, id);
    }

    public int findVacationDaysByEmpId(long id) {
        return fetchData(DashboardSql.FIND_VACATION_NUM, Integer.class, id).orElse(15);
    }

    public int findUsedVacationDaysByEmpId(long id) {
        return fetchData(DashboardSql.FIND_USED_VACATION_NUM, Integer.class, id).orElse(0);
    }

    public int findAllWorkTimeByDateAndEmpId(long id) {
        return fetchData(DashboardSql.FIND_TOTAL_WORKING_TIME, Integer.class, id).orElse(0);
    }

    public int findAllExtendWorkTimeByDateAndEmpId(long id) {
        return fetchData(DashboardSql.FIND_TOTAL_EXTEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    public int findAllWeekendWorkTimeByDateAndEmpId(long id) {
        return fetchData(DashboardSql.FIND_TOTAL_WEEKEND_WORKING_TIME, Integer.class, id).orElse(0);
    }

    /*
        List<String>로 해당 주에 등록된 일정 반환 하는 함수
    */
    public List<String> findSchedulesByDateAndEmpId(long id) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DashboardSql.FIND_SCHEDULE_LIST);
        ) {
            List<String> scheduleList = new ArrayList<>();
            LocalDateTime startDate = null;
            String title = null;
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    startDate = rs.getTimestamp("START_DATE").toLocalDateTime();
                    title = rs.getString("TITLE");
                    int month = startDate.getMonthValue();
                    int day = startDate.getDayOfMonth();

                    scheduleList.add(String.format("%02d월 %02d일 %s", month, day, title));
                }
            }
            return scheduleList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /*
        DB 에서 데이터 조회 후 데이터 값 반환
        Optional 사용 NullPointerException 방지
    */
    private <T> Optional<T> fetchData(String sql, Class<T> type, Object... params) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject(1);

                    // 타입 변환 : oracle.sql.TIMESTAMP -> java.sql.Timestam -> LocalDateTime
                    if (type == LocalDateTime.class) {
                        Method method = value.getClass().getMethod("timestampValue");
                        Timestamp ts = (Timestamp) method.invoke(value);
                        return Optional.of(type.cast(ts.toLocalDateTime()));
                    }

                    // 타입 변환 : NUMBER -> BigDecimal -> Integer
                    if (type == Integer.class) {
                        return Optional.of(type.cast(((Number) value).intValue()));
                    }

                    return Optional.ofNullable(type.cast(value));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
