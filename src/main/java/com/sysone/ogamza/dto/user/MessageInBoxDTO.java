
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
/**
 * 받은 쪽지함 목록에 표시할 쪽지 정보를 담는 DTO 클래스입니다.
 *
 * MessageBoxViewDTO 인터페이스를 구현하여 쪽지함 UI에서 공통 구조로 활용됩니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */

@Builder
@Data
public class MessageInBoxDTO implements MessageBoxViewDTO {

    /** 쪽지 ID */
    private int messageId;

    /** 보낸 사람의 프로필 이미지 경로 */
    private String senderProfile;

    /** 보낸 사람 이름 */
    private String senderName;

    /** 보낸 사람 부서 */
    private String senderDept;

    /** 쪽지 내용 */
    private String content;

    /** 발송일 */
    private LocalDate sendDate;

    /** 읽음 여부 (0: 안읽음, 1: 읽음) */
    private int isRead;

    @Override
    public int getMessageId() {
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
    public String getDeptName() {
        return senderDept;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public LocalDate getSendDate() {
        return sendDate;
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
