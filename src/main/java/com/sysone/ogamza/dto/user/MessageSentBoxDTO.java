
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
/**
 * 보낸 쪽지함에서 사용할 DTO 클래스입니다.
 * 쪽지 ID, 수신자 이름/부서, 내용, 발송일, 읽음 여부를 포함하며,
 * MessageBoxViewDTO 인터페이스를 구현하여 공통 쪽지 인터페이스를 제공합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class MessageSentBoxDTO implements MessageBoxViewDTO {

    private int messageId;          // 쪽지 ID
    private String receiverName;    // 수신자 이름
    private String receiverDept;    // 수신자 부서명
    private String content;         // 쪽지 내용
    private LocalDate sentAt;       // 발송일
    private int isRead;             // 읽음 여부 (0: 안읽음, 1: 읽음)

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public String getProfileImagePath() {
        return null;  // 보낸 쪽지함에서는 프로필 이미지를 사용하지 않음
    }

    @Override
    public String getName() {
        return receiverName;
    }

    @Override
    public String getDeptName() {
        return receiverDept;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public LocalDate getSendDate() {
        return sentAt;
    }

    @Override
    public int getIsRead() {
        return isRead;
    }

    @Override
    public void setIsRead(int num) {
        this.isRead = num;
    }

}
