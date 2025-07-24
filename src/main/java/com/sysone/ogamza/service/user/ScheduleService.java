package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.DashboardDAO;
import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.dao.user.ScheduleDAO;
import com.sysone.ogamza.utils.dashboard.UsedVacationCalculator;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleService {
    @Getter
    private static final ScheduleService instance = new ScheduleService();
    private static final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private static final DashboardDAO dashboardDAO = DashboardDAO.getInstance();

    private ScheduleService() {}

    /**
        일정 상세 내용 조회
     */
    public List<String> getScheduleContent (long id, int index) {
        ScheduleContentDTO dto = scheduleDAO.findScheduleContentById(id, index);

        List<String> scheduleList = new ArrayList<>();
        int sYear = dto.getStartDate().getYear() % 100;
        int sMonth = dto.getStartDate().getMonthValue();
        int sDay = dto.getStartDate().getDayOfMonth();
        int eYear = dto.getEndDate().getYear() % 100;
        int eMonth = dto.getEndDate().getMonthValue();
        int eDay = dto.getEndDate().getDayOfMonth();
        String title = dto.getTitle();
        String type = dto.getScheduleType();
        String content = dto.getContent();

        scheduleList.add(title);
        scheduleList.add(type);
        scheduleList.add(String.format("%d.%02d.%02d ~ %d.%02d.%02d", sYear, sMonth, sDay, eYear, eMonth, eDay));
        scheduleList.add(content);

        return scheduleList;
    }

    /**
        승인된 일정 리스트 조회
     */
    public List<ScheduleListDTO> getScheduleList(long id) {
       return scheduleDAO.findScheduleListById(id);
    }

    /**
        전체 일정 리스트 조회
     */
    public List<ScheduleContentDTO> getScheduleAllList(long id) {return scheduleDAO.findSchedulesByEmpId(id); }

    /**
        일정 결재 상신
     */
    public String createSchedule(ScheduleContentDTO scheduleListDto) {
        int vacation = dashboardDAO.findVacationDaysByEmpId(scheduleListDto.getEmpId());
        double used = UsedVacationCalculator.compute(dashboardDAO.findUsedVacationDaysByEmpId(scheduleListDto.getEmpId()));

        LocalDate start = scheduleListDto.getStartDate().toLocalDate();
        LocalDate end = scheduleListDto.getEndDate().toLocalDate();

        long workingDays = start.datesUntil(end.plusDays(1))
                .filter(d -> !(d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY))
                .count();

        double totalUsed = used + workingDays;

        if (totalUsed > vacation) {
            return "잔여 연차 부족 으로 상신 불가";
        }

        return scheduleDAO.insertSchedule(scheduleListDto) ? "상신 완료" : "다시 시도해 주세요.";
    }

    /**
       일정 결재 상신 취소
     */
    public boolean removeScheduleById(long id, long scheduleId) {
        return scheduleDAO.deleteScheduleById(id, scheduleId);
    }

    /**
        매일 11시 50분에 자동 출퇴근 기록 실행
        실행은 Main 에서 프로그램 작동하면 바로 스케줄 등록
     */
    public void scheduleDailyWorkingTime() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                scheduleDAO.insertWorkingTime();
            }
        };

        long initialDelay = computeInitialDelay(23, 50);
        long period = Duration.ofDays(1).toMillis();

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
        현재 시점부터 지정된 시간까지 남은 시간 계산
     */
    private long computeInitialDelay(int targetHour, int targetMinute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0);

        if (now.isAfter(target)) {
            target = target.plusDays(1);
        }

        return ChronoUnit.MILLIS.between(now, target);
    }
}
