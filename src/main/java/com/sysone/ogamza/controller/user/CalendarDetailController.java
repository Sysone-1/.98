package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import com.sysone.ogamza.view.user.MonthView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.util.List;

/**
 * 사용자 달력 상세 보기를 담당하는 컨트롤러입니다.
 * <p>
 * 로그인한 사용자의 전체 일정을 조회하고,
 * 이를 기반으로 MonthView를 생성하여 달력 형태로 시각화합니다.
 *
 * @author 김민호
 */
public class CalendarDetailController {
    @FXML private AnchorPane calendar;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();
    private static final Session employeeSession = Session.getInstance();
    public static final long empId = employeeSession.getLoginUser().getId();

    /**
     * 초기화 메서드로, 전체 일정을 조회하여 MonthView를 생성하고 달력에 표시합니다.
     */
    @FXML
    public void initialize() {
            List<ScheduleContentDTO> list = scheduleService.getThisWeekSchedulesByEmpId(empId);

            MonthView monthView = new MonthView(list);
            calendar.getChildren().add(monthView);
    }
}
