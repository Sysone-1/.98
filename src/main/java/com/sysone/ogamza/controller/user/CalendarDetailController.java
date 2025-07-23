package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import com.sysone.ogamza.view.user.MonthView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class CalendarDetailController {
    @FXML private AnchorPane calendar;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();
    private static final Session employeeSession = Session.getInstance();
    public static final long empId = employeeSession.getLoginUser().getId();

    @FXML
    public void initialize() {
            List<ScheduleContentDTO> list = scheduleService.getScheduleAllList(empId);

            MonthView monthView = new MonthView(list);
            calendar.getChildren().add(monthView);
    }
}
