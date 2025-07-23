package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class MessageSentBoxDTO implements MessageBoxViewDTO {

    private int messageId;
    private String receiverName;
    private String receiverDept;
    private String content;
    private LocalDate sentAt;
    private int isRead;


    @Override
    public int getMessageId(){
        return messageId;
    }

    @Override
    public String getProfileImagePath() {
        return null;
    }


    @Override
    public String getName() {
        return receiverName;
    }

    @Override
    public String getDeptName(){
        return receiverDept;
    }

    @Override
    public String getContent(){
        return content;
    }

    @Override
    public LocalDate getSendDate(){
        return sentAt;
    }

    @Override
    public int getIsRead(){
        return isRead;
    }

    @Override
    public void setIsRead(int num){
        this.isRead = num;
    }

}
