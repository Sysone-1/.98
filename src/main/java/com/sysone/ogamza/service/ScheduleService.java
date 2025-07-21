package com.sysone.ogamza.service;

import com.sysone.ogamza.dto.ScheduleContentDto;
import com.sysone.ogamza.dto.ScheduleListDto;
import com.sysone.ogamza.dao.ScheduleDAO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ScheduleService {
    @Getter
    private static final ScheduleService instance = new ScheduleService();
    private static final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();

    private ScheduleService() {}


    /**
        일정 상세 내용 조회
     */
    public List<String> getScheduleContent (long id, int index) {
        ScheduleContentDto dto = scheduleDAO.findScheduleContentById(id, index);

        List<String> scheduleList = new ArrayList<>();
        int sYear = dto.getStartDate().getYear();
        int sMonth = dto.getStartDate().getMonthValue();
        int sDay = dto.getStartDate().getDayOfMonth();
        int eYear = dto.getEndDate().getYear();
        int eMonth = dto.getEndDate().getMonthValue();
        int eDay = dto.getEndDate().getDayOfMonth();
        String title = dto.getTitle();
        String type = dto.getScheduleType();
        String content = dto.getContent();

        scheduleList.add(title);
        scheduleList.add(type);
        scheduleList.add(String.format("%d-%02d-%02d ~ %d-%02d-%02d", sYear, sMonth, sDay, eYear, eMonth, eDay));
        scheduleList.add(content);

        return scheduleList;
    }

    /**
        일정 조회
     */
    public List<ScheduleListDto> getScheduleList(long id) {
       return scheduleDAO.findScheduleListById(id);
    }

    /**
        일정 결재 상신
     */
    public boolean createSchedule(ScheduleContentDto scheduleListDto) {
        return scheduleDAO.insertSchedule(scheduleListDto);
    }

    /**
       일정 결재 상신 취소
     */
    public boolean removeScheduleById(long id, long scheduleId) {
        return scheduleDAO.deleteScheduleById(id, scheduleId);
    }
}
