package com.sysone.ogamza.utils.dashboard;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * 사용자의 연차 사용 일수를 계산하는 유틸리티 클래스입니다.
 * <p>
 * 연차는 주말(토, 일)을 제외한 평일만 계산되며,
 * 반차는 0.5일로 계산됩니다.
 * </p>
 *
 * @author 김민호
 */
public class UsedVacationCalculator {
    /**
     * 연차 사용 내역 리스트를 기반으로 총 사용 연차 일수를 계산합니다.
     *
     * @param result 연차 정보 리스트. 각 항목은 다음과 같은 key를 가집니다:
     *               <ul>
     *                   <li><code>type</code>: "연차" 또는 "반차"</li>
     *                   <li><code>duration</code>: 시작일과 종료일을 쉼표로 구분한 문자열 (예: "2024-07-01T00:00,2024-07-03T00:00")</li>
     *               </ul>
     * @return 총 사용 연차 일수 (double). 반차는 0.5일로 계산됩니다.
     */
    public static double compute(List<HashMap<String, String>> result) {
        double vacationDays = 0;

        for (HashMap<String, String> hm : result) {
            if ("연차".equals(hm.get("type"))) {
                String[] dates = hm.get("duration").split(",");

                LocalDateTime start = LocalDateTime.parse(dates[0]);
                LocalDateTime end = LocalDateTime.parse(dates[1]);

                while (!start.isAfter(end)) {
                    DayOfWeek dayOfWeek = start.getDayOfWeek();
                    if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                        vacationDays += 1;
                    }
                    start = start.plusDays(1);
                }

            } else if ("반차".equals(hm.get("type"))) {
                vacationDays += 0.5;
            }
        }
        return vacationDays;
    }
}