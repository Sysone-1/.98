package com.sysone.ogamza.dto.user;

import lombok.Getter;

@Getter
public class AlarmSettingDTO {
    private int id;
    private int alarm1;
    private int alarm2;
    private int alarm3;

    public AlarmSettingDTO(int id, int alarm1, int alarm2, int alarm3) {
        this.id = id;
        this.alarm1 = alarm1;
        this.alarm2 = alarm2;
        this.alarm3 = alarm3;
    }
}
