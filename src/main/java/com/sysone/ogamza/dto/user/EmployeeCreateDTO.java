package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeCreateDTO {
    private int departmentId;
    private String name;
    private String email;
    private String position;
    private String telNum;
    private String password;
    private String picDir;
    private byte[] cardId;
    private int vacNum;

    public EmployeeCreateDTO(int departmentId, String name, String email, String position,
                             String telNum, String password, String picDir, byte[] cardId, int vacNum) {
        this.departmentId = departmentId;
        this.name = name;
        this.email = email;
        this.position = position;
        this.telNum = telNum;
        this.password = password;
        this.picDir = picDir;
        this.cardId = cardId;
        this.vacNum = vacNum;
    }
}
