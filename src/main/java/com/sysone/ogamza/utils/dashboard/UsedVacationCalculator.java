package com.sysone.ogamza.utils.dashboard;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class UsedVacationCalculator {
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