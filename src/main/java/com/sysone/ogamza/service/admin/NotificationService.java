package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.NotificationDAO;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {

    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public int getUnauthorizedAccessCount() {
        return notificationDAO.getNullEmployeeIdAccessLogCount();
    }

    public List<LocalDateTime> getUnauthorizedAccessTimes() {
        return notificationDAO.getNullEmployeeIdAccessTimes();
    }

    public void markAllUnauthorizedAccessLogAsRead() {
        notificationDAO.markUnauthorizedAccessLogAsRead();
    }
}