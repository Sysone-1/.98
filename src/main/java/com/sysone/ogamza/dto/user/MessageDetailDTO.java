package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class MessageDetailDTO {
    private String name;
    private String content;
    private int isRead;
    private LocalDate sendDate;
}
