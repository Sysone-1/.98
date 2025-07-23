package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.NFCDAO;
import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeCreateDTO;
import com.sysone.ogamza.enums.NFCErrCode;
import com.sysone.ogamza.nfc.NFCReader;
import lombok.Getter;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class NFCService {

    @Getter
    private static final NFCService instance = new NFCService();
    private static final NFCDAO nfcDao = NFCDAO.getInstance();

    public NFCService() {}
    private static final Logger logger = Logger.getLogger(NFCService.class.getName());
    private Card card;
    private CardChannel channel;

    /**
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

    /**
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

    /**
        사원 등록
     */
    public String createEmployee(EmployeeCreateDTO dto) {
        return nfcDao.insertEmployee(dto);
    }

    /**
        사원 탈퇴
     */
    public String deleteEmployee(EmployeeCreateDTO dto) {
        return nfcDao.deleteEmployee(dto);
    }

    /**
        부서 ID로 부서명 조회
    */
    public List<DepartmentDTO> getDepartment() {
        return nfcDao.findDepartment();
    }

   /**
        카드 연결
        지정된 시작 블록부터 count개의 블록 읽기
        인증 후 블록 단위로 읽음
        Sector Trailer은 건너뜀
    */
    public String readDataFromCard(int startBlock, int count) {
        try {
            connectCardReader();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int block = startBlock;

            for (int i = 0; i < count; i++) {
                if ((block + 1) % 4 == 0) block++;
                if (!NFCReader.authenticateBlock(block)) return null;

                byte[] data = NFCReader.readBlock(block);
                if (data == null) return null;

                baos.write(data);
                block++;
            }
            byte[] allData = baos.toByteArray();
            String result = new String(allData, StandardCharsets.UTF_8).trim();

            System.out.println(result);
            return result;
        } catch (Exception e) {
            logger.warning(NFCErrCode.BLOCK_READ_FAILED.getMessage() + " readDataFromCard");
            return null;
        }
    }

    /**
        카드 ID에 따른 사원 ID 조회
     */
    public String getEmployeeInfo(byte[] cardId ) {
        return nfcDao.findEmployeeById(cardId);
    }

    /**
        출입 시간 등록
     */
    public boolean insertAccessTime(int empId) {
        return nfcDao.insertAccessTime(empId);
    }

    /**
        미인가 출입 시간 등록
    */
    public boolean insertUnauthorizedAccessTime() {
        return nfcDao.insertUnauthorizedAccessTime();
    }

    /**
        프로필 사진 경로 조회
     */
    public String getProfileDir(int empId) {
        return nfcDao.findPicdir(empId);
    }
}
