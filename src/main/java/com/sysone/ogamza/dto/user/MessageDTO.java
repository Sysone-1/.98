package com.sysone.ogamza.dto.user;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private int senderId;
    private int receiverId;
    private String content;
}
