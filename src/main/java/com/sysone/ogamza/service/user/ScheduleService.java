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

/**
 * 일정 관리 기능을 담당하는 서비스 클래스입니다.
 *
 * <p>사용자의 일정 등록, 조회, 삭제 및 자동 근무 기록 기능을 제공합니다.</p>
 * <ul>
 *     <li>일정 상신 및 연차 검증</li>
 *     <li>일정 상세 및 전체 조회</li>
 *     <li>일정 결재 취소</li>
 *     <li>매일 지정 시간에 자동 근무 시간 기록</li>
 * </ul>
 *
 * @author 김민호
 */
public class ScheduleService {
    @Getter
    private static final ScheduleService instance = new ScheduleService();
    private static final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private static final DashboardDAO dashboardDAO = DashboardDAO.getInstance();

    private ScheduleService() {}

    /**
     * 일정 인덱스를 통해 해당 일정의 상세 내용을 조회합니다.
     *
     * @param id     사원 ID
     * @param index  일정 인덱스
     * @return 일정 제목, 유형, 기간, 내용이 포함된 문자열 리스트
     */
    public List<String> getScheduleDetailsByEmpIdAndIndex (long id, int index) {
        ScheduleContentDTO dto = scheduleDAO.findWeeklyGrantedScheduleByEmpIdAndIndex(id, index);

        List<String> scheduleList = new ArrayList<>();
        int sYear = dto.getStartDate().getYear() % 100;
        int sMonth = dto.getStartDate().getMonthValue();
        int sDay = dto.getStartDate().getDayOfMonth();
        int eYear = dto.getEndDate().getYear() % 100;
        int eMonth = dto.getEndDate().getMonthValue();
        int eDay = dto.getEndDate().getDayOfMonth();
        String title = dto.getTitle();
        String content = dto.getContent();

        scheduleList.add(title);
        scheduleList.add(String.format("%d.%02d.%02d ~ %d.%02d.%02d", sYear, sMonth, sDay, eYear, eMonth, eDay));
        scheduleList.add(content);

        return scheduleList;
    }

    /**
     * 승인된 일정 리스트를 조회합니다.
     *
     * @param id 사원 ID
     * @return 승인된 일정 리스트
     */
    public List<ScheduleListDTO> getApprovedScheduleList(long id) {
       return scheduleDAO.findAllSchedulesByEmpId(id);
    }

    /**
     * 사원의 전체 일정 리스트를 조회합니다.
     *
     * @param id 사원 ID
     * @return 전체 일정 DTO 리스트
     */
    public List<ScheduleContentDTO> getThisWeekSchedulesByEmpId(long id) {return scheduleDAO.findThisWeekGrantedSchedulesByEmpId (id); }

    /**
     * 일정 결재 상신을 처리합니다. 연차 일수가 부족한 경우 상신을 거부합니다.
     *
     * @param scheduleListDto 상신할 일정 정보 DTO
     * @return 상신 결과 메시지 ("상신 완료", "잔여 연차 부족 으로 상신 불가" 등)
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
     * 일정 결재 상신을 취소합니다.
     *
     * @param id         사원 ID
     * @param scheduleId 취소할 일정 ID
     * @return 삭제 성공 여부
     */
    public boolean cancelScheduleRequestById(long id, long scheduleId) {
        return scheduleDAO.cancelScheduleRequestByEmpIdAndScheduleId(id, scheduleId);
    }

    /**
     * 매일 정해진 시간(23:50)에 자동으로 근무 기록을 저장하는 작업을 예약합니다.
     *
     * <p>실행 시 Main 클래스에서 최초 한 번만 호출하면 됩니다.</p>
     */
    public void startDailyWorkingTimeScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                scheduleDAO.batchInsertDailyWorkingTime();
            }
        };

        long initialDelay = computeInitialDelay(23, 50);
        long period = Duration.ofDays(1).toMillis();

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 현재 시간 기준으로 지정된 시(hour, minute)까지의 지연 시간을 밀리초로 계산합니다.
     *
     * @param targetHour   목표 시 (24시간 형식)
     * @param targetMinute 목표 분
     * @return 현재 시점부터 목표 시점까지의 지연 시간 (밀리초)
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
