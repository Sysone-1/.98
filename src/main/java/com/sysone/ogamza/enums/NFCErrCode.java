package com.sysone.ogamza.enums;

/**
 * NFC 카드 리더기 동작 중 발생할 수 있는 에러 코드를 정의한 열거형 클래스입니다.
 * <p>
 * 각 에러 코드는 사용자에게 표시될 메시지를 포함하며,
 * 카드 미인식, 리더기 미연결, 인증 실패 등 다양한 예외 상황을 다룹니다.
 * </p>
 *
 * @author 김민호
 */
public enum NFCErrCode {
    NO_CARD("❌ NFC 카드를 인식하지 못했습니다. 다시 시도하세요."),
    NO_TERMINAL("❌ 카드 터미널을 찾지 못했습니다."),
    BLOCK_WRITE_FAILED("❌ 블록 쓰기에 실패했습니다."),
    BLOCK_READ_FAILED("❌ 블록 읽기에 실패했습니다."),
    NO_UID("❌ UID 읽기 실패"),
    AUTH_FAILED("❌ 카드 인증 중 오류 발생"),
    NO_READER("❌ 연결된 리더기가 없습니다.");


    private final String message;

    /**
     * 에러 메시지를 포함하는 생성자입니다.
     *
     * @param message 사용자에게 보여질 에러 메시지
     */
    NFCErrCode(String message) {
        this.message = message;
    }

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 에러 메시지 문자열
     */
    public String getMessage() {
        return message;
    }
}
