package com.sysone.ogamza.dto.user;
import lombok.*;

@Getter
@AllArgsConstructor
public class MemberDetailDTO {
    private final String name;
    private final String position;

    @Setter
    private String tel;

    @Setter
    private String password;
}

