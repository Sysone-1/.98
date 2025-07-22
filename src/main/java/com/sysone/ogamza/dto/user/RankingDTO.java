package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RankingDTO {

    private int deptId;
    private String deptName;
    private int score;
    private int ranking;
}
