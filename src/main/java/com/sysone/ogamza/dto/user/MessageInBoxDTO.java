package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class MessageInBoxDTO {
    private int messageId;
    private String senderProfile;
    private String senderName;
    private String senderDept;
    private String content;
    private LocalDate sendDate;
    private int isRead;
}
