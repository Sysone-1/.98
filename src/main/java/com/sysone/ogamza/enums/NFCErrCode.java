package com.sysone.ogamza.enums;

public enum NFCErrCode {
    NO_CARD("❌ NFC 카드를 인식하지 못했습니다. 다시 시도하세요."),
    NO_TERMINAL("❌ 카드 터미널을 찾지 못했습니다."),
    BLOCK_WRITE_FAILED("❌ 블록 쓰기에 실패했습니다."),
    BLOCK_READ_FAILED("❌ 블록 읽기에 실패했습니다."),
    NO_UID("❌ UID 읽기 실패"),
    AUTH_FAILED("❌ 카드 인증 중 오류 발생"),
    NO_READER("❌ 연결된 리더기가 없습니다.");

    private final String message;

    NFCErrCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
