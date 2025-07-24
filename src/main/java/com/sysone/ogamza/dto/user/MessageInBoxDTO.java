package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class MessageInBoxDTO implements MessageBoxViewDTO{
    private int messageId;
    private String senderProfile;
    private String senderName;
    private String senderDept;
    private String content;
    private LocalDate sendDate;
    private int isRead;

    @Override
    public int getMessageId(){
        return messageId;
    }

    @Override
    public String getProfileImagePath() {
        return senderProfile;
    }


    @Override
    public String getName() {
        return senderName;
    }

    @Override
    public String getDeptName(){
        return senderDept;
    }

    @Override
    public String getContent(){
        return content;
    }

    @Override
    public LocalDate getSendDate(){
        return sendDate;
    }

    @Override
    public int getIsRead(){
        return isRead;
    }

    @Override
    public void setIsRead(int num ){
         this.isRead = num;
    }
}
