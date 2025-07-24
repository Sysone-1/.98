package com.sysone.ogamza.nfc;

import com.sysone.ogamza.enums.NFCErrCode;

import javax.smartcardio.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * NFCReader는 javax.smartcardio API를 활용하여 NFC 카드 리더기와의 통신을 처리합니다.
 *
 * <p>카드 리더기 또는 카드 미탐지 시 {@link NFCErrCode}를 통해 오류 메시지를 기록합니다.</p>
 *
 * <p><strong>주의:</strong> 해당 클래스는 ACR122U와 같은 리더기에 맞춰 설계되었으며,
 * MIFARE Classic 1K 타입 카드 기준입니다.</p>
 *
 * @author 김민호
 */
public class NFCReader {

    private static CardChannel channel; // NFC 카드와 통신할 때 사용되는 채널

    private static final Logger logger = Logger.getLogger(NFCReader.class.getName());

    public static void setChannel(CardChannel ch) {
        channel = ch;
    }

    /**
     * 시스템에서 사용 가능한 NFC 카드 리더기를 탐색하고,
     * 첫 번째 리더기를 반환합니다.
     *
     * @return 사용 가능한 첫 번째 {@link CardTerminal}, 없으면 {@code null}
     */
    public static CardTerminal getCardTerminal() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                logger.warning(NFCErrCode.NO_TERMINAL.getMessage() + " getCardTerminal");
                return null;
            }
            return terminals.get(0);
        } catch (Exception e) {
            logger.warning( NFCErrCode.NO_READER.getMessage() + " getCardTerminal");
            return null;
        }
    }

    /**
     * NFC 카드와 연결 후 UID(고유 식별자)를 읽어 반환합니다.
     *
     * @return UID 문자열 (HEX 형식, 예: "04AABBCCDD"), 실패 시 {@code null}
     */
    public static String readUID() {
        try {
            CardTerminal terminal = getCardTerminal();
            terminal.waitForCardPresent(0);
            Card card = terminal.connect("*");
            channel = card.getBasicChannel();

            CommandAPDU getUid = new CommandAPDU(new byte[]{
                    (byte) 0xFF,
                    (byte) 0xCA,
                    0x00,
                    0x00,
                    0x00
            });

            ResponseAPDU response = channel.transmit(getUid);

            if (response.getSW() != 0x9000) return null;

            byte[] uidBytes = response.getData();
            StringBuilder sb = new StringBuilder();
            for (byte b : uidBytes) sb.append(String.format("%02X", b));

            return sb.toString();
        } catch (Exception e) {
            logger.warning(NFCErrCode.NO_UID.getMessage() + " readUID");
            return null;
        }
    }

    /**
     * 지정된 블록 번호에 대해 기본 키 A를 사용하여 인증을 수행합니다.
     *
     * @param block 인증할 블록 번호
     * @return 인증 성공 여부
     */
    public static boolean authenticateBlock(int block) {
        try {
            byte[] cmd = new byte[]{
                    (byte) 0xFF, (byte) 0x86, 0x00, 0x00, 0x05, // 인증 명령 헤더
                    0x01, // Version
                    0x00, // 시작 블록 위치 (MSB)
                    (byte) block, // 인증할 블록 번호
                    0x60, // 키 타입 A (0x61이면 키 B)
                    0x00  // 키 번호 (기본 키 슬롯)
            };
            ResponseAPDU res = channel.transmit(new CommandAPDU(cmd));
            return res.getSW() == 0x9000;
        } catch (Exception e) {
            logger.warning(NFCErrCode.AUTH_FAILED.getMessage() + " authenticateBlock");
            return false;
        }
    }

    /**
     * 주어진 데이터를 지정된 블록 번호에 씁니다. 데이터는 16바이트로 제한됩니다.
     *
     * @param block 대상 블록 번호
     * @param data 16바이트 데이터 배열
     * @return 쓰기 성공 여부
     */
    public static boolean writeBlock(int block, byte[] data) {
        try {
            byte[] cmd = new byte[21]; // 5바이트 명령헤더, 16바이트 실제 데이터
            cmd[0] = (byte) 0xFF; // CLA
            cmd[1] = (byte) 0xD6; // INS: write binary
            cmd[2] = 0x00;        // P1
            cmd[3] = (byte) block; // P2: 대상 블록
            cmd[4] = 0x10;         // Lc: 16바이트 데이터
            System.arraycopy(data, 0, cmd, 5, 16);  // 사용자 데이터 16바이트 복사

            ResponseAPDU res = channel.transmit(new CommandAPDU(cmd));
            return res.getSW() == 0x9000;
        } catch (Exception e) {
            logger.warning(NFCErrCode.BLOCK_WRITE_FAILED.getMessage() + " writeBlock");
            return false;
        }
    }

    /**
     * 지정된 블록 번호에서 16바이트 데이터를 읽어 반환합니다.
     *
     * @param block 읽을 블록 번호
     * @return 읽은 데이터 (16바이트), 실패 시 {@code null}
     */
    public static byte[] readBlock(int block) {
        try {
            byte[] cmd = new byte[]{
                    (byte) 0xFF,  // CLA: 명령 클래스 (vendor-specific 명령: ACR122U 전용)
                    (byte) 0xB0,  // INS: READ BINARY 명령어
                    0x00,         // P1: 기본값 (블록 단위 읽기라 0으로 고정)
                    (byte) block, // P2: 읽고자 하는 블록 번호 (0~63 등)
                    0x10          // Le: 읽을 바이트 수 (0x10 = 16 bytes, 한 블록 크기)
            };
            ResponseAPDU res = channel.transmit(new CommandAPDU(cmd));
            return res.getSW() == 0x9000 ? res.getData() : null;
        } catch (Exception e) {
            logger.warning(NFCErrCode.BLOCK_READ_FAILED.getMessage() + " readBlock");
            return null;
        }
    }
}
