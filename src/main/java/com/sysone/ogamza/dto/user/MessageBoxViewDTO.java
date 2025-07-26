package com.sysone.ogamza.dto.user;

import java.time.LocalDate;

/**
 * 쪽지함에서 사용되는 공통 메시지 뷰 인터페이스입니다.
 * - 메시지 목록 화면에서 수신/발신 쪽지를 구분 없이 다루기 위한 통일된 접근 방식 제공
 * - 프로필 이미지, 이름, 부서, 메시지 내용, 읽음 여부 등을 공통으로 추출할 수 있도록 설계
 * @author 서샘이
 * @since 2025-07-27
 */
public interface MessageBoxViewDTO {

    /** 메시지 고유 ID 반환 */
    int getMessageId();

    /** 송신자 or 수신자 이름 */
    String getName();

    /** 소속 부서 이름 */
    String getDeptName();

    /** 메시지 본문 */
    String getContent();

    /** 발송 날짜 */
    LocalDate getSendDate();

    /** 읽음 여부 (0: 안읽음, 1: 읽음) */
    int getIsRead();

    /** 프로필 이미지 경로 */
    String getProfileImagePath();

    /** 읽음 상태 수정용 세터 */
    void setIsRead(int num);
}
