package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.utils.db.OracleConnector;
import com.sysone.ogamza.sql.admin.NotificationSql;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationDAO {

    public int getNullEmployeeIdAccessLogCount() {
        return OracleConnector.fetchData(NotificationSql.GET_NULL_EMPLOYEE_ID_ACCESS_LOG_COUNT, Integer.class).orElse(0);
    }

    public List<LocalDateTime> getNullEmployeeIdAccessTimes() {
        return OracleConnector.fetchList(NotificationSql.GET_NULL_EMPLOYEE_ID_ACCESS_TIMES, LocalDateTime.class);
    }

    public void markUnauthorizedAccessLogAsRead() {
        OracleConnector.executeDML(NotificationSql.MARK_UNAUTHORIZED_ACCESS_LOG_AS_READ);
    }
}