package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;


import java.time.LocalTime;
import java.util.Date;

@Builder
@Data
public class UserRecordDTO {

    private int employeeId;
    private String name;
    private Date workDate;
    private String checkInTime;
    private String checkOutTime;
    private String workStatus;
}
