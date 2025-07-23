package com.sysone.ogamza.dto.user;

import java.time.LocalDate;

public interface MessageBoxViewDTO {
    int getMessageId();
    String getName();
    String getDeptName();
    String getContent();
    LocalDate getSendDate();
    int getIsRead();
    String getProfileImagePath();

    void setIsRead(int num);

}
