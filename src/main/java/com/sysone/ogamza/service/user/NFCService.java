package com.sysone.ogamza.service.user;

import com.sysone.ogamza.enums.NFCErrCode;
import com.sysone.ogamza.nfc.NFCReader;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

public class NFCService {
    private static final Logger logger = Logger.getLogger(NFCService.class.getName());
    private Card card;
    private CardChannel channel;

    /*
        카드 리더기 가져와서 공유 채널 세팅
    */
    private void connectCardReader() {
        try {
            CardTerminal terminal = NFCReader.getCardTerminal();
            if (terminal == null || !terminal.isCardPresent()) {
                throw new Exception();
            }
            card = terminal.connect("*");
            channel = card.getBasicChannel();
            NFCReader.setChannel(channel);
        } catch (Exception e) {
            logger.warning(NFCErrCode.NO_CARD.getMessage() + " connectCardReader");
        }
    }

    /*
        카드 연결
        16바이트 단위로 블록을 나누어 저장 (MIFARE 한 블록 = 16 bytes)
        블록 단위 인증 후 write
        4번째 블록마다 인증 블록(Sector Trailer)이므로 건너뜀
    */
    public boolean writeDataToCard(byte[] data) {
        try {
            connectCardReader();

            int block = 4;

            for (int i = 0; i < data.length; i += 16) {
                if ((block + 1) % 4 == 0) block++;
                byte[] blockData = Arrays.copyOfRange(data, i, Math.min(i + 16, data.length));
                if (blockData.length < 16) blockData = Arrays.copyOf(blockData, 16);

                if (!NFCReader.authenticateBlock(block)) return false;
                if (!NFCReader.writeBlock(block, blockData)) return false;

                block++;
            }
            return true;
        } catch (Exception e) {
            logger.warning(NFCErrCode.BLOCK_WRITE_FAILED.getMessage() + " writeDataToCard");
            return false;
        }
    }

   /*
        카드 연결
        지정된 시작 블록부터 count개의 블록 읽기
        인증 후 블록 단위로 읽음
        Sector Trailer은 건너뜀
    */
    public String readDataFromCard(int startBlock, int count) {
        try {
            connectCardReader();
            StringBuilder sb = new StringBuilder();
            int block = startBlock;

            for (int i = 0; i < count; i++) {
                if ((block + 1) % 4 == 0) block++;
                if (!NFCReader.authenticateBlock(block)) return null;

                byte[] data = NFCReader.readBlock(block);
                if (data == null) return null;

                sb.append(new String(data, StandardCharsets.UTF_8));
                block++;
            }
            return sb.toString().trim();
        } catch (Exception e) {
            logger.warning(NFCErrCode.BLOCK_READ_FAILED.getMessage() + " readDataFromCard");
            return null;
        }
    }
}
