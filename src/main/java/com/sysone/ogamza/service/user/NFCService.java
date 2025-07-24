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

/**
 * NFC 관련 기능을 담당하는 서비스 클래스입니다.
 *
 * <p>카드 리더기 연결, 카드 데이터 읽기/쓰기, 사원 등록 및 삭제, 출입 기록 등록,
 * 부서 및 사원 정보 조회 등의 기능을 제공합니다.</p>
 *
 * @author 김민호
 */
public class NFCService {

    @Getter
    private static final NFCService instance = new NFCService();
    private static final NFCDAO nfcDao = NFCDAO.getInstance();

    public NFCService() {}
    private static final Logger logger = Logger.getLogger(NFCService.class.getName());
    private Card card;
    private CardChannel channel;

    /**
     * 카드 리더기와 연결하고 기본 채널을 설정합니다.
     * 연결 실패 시 로그에 경고 메시지를 출력합니다.
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
     * NFC 카드에 데이터를 블록 단위로 작성합니다.
     * MIFARE 카드 기준 16바이트 단위로 작성하며,
     * 4번째 블록마다 존재하는 Sector Trailer는 건너뜁니다.
     *
     * @param data NFC 카드에 저장할 바이트 배열 데이터
     * @return 작성 성공 여부
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
     * 신규 사원을 등록합니다.
     *
     * @param dto 사원 정보 DTO
     * @return 등록 결과 메시지
     */
    public String registerEmployee(EmployeeCreateDTO dto) {
        return nfcDao.insertEmployee(dto);
    }

    /**
     * 사원을 삭제(탈퇴)합니다.
     *
     * @param dto 사원 정보 DTO
     * @return 삭제 결과 메시지
     */
    public String deleteEmployee(EmployeeCreateDTO dto) {
        return nfcDao.deleteEmployeeByCardId(dto);
    }

    /**
     * 모든 부서 목록을 조회합니다.
     *
     * @return 부서 DTO 리스트
     */
    public List<DepartmentDTO> getDepartments() {
        return nfcDao.findAllDepartments();
    }

    /**
     * NFC 카드에서 데이터를 블록 단위로 읽어 문자열로 반환합니다.
     * 4번째 블록마다 존재하는 Sector Trailer는 건너뜁니다.
     *
     * @param startBlock 시작 블록 번호
     * @param count 읽을 블록 수
     * @return 읽어온 문자열 데이터 (UTF-8), 실패 시 null
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
     * 카드 UID를 기반으로 사원 ID를 조회합니다.
     *
     * @param cardId 카드 UID 바이트 배열
     * @return 사원 ID (문자열)
     */
    public String getEmployeeNameByCardId(byte[] cardId ) {
        return nfcDao.findEmployeeNameByCardId(cardId);
    }

    /**
     * 출입 기록을 등록합니다.
     *
     * @param empId 사원 ID
     * @return 등록 성공 여부
     */
    public boolean insertAccessTime(int empId) {
        return nfcDao.insertAccessTime(empId);
    }

    /**
     * 미인가 출입 시각을 등록합니다.
     *
     * @return 등록 성공 여부
     */
    public boolean insertUnauthorizedAccessTime() {
        return nfcDao.insertUnauthorizedAccessTime();
    }

    /**
     * 사원 ID를 통해 프로필 사진 경로를 조회합니다.
     *
     * @param empId 사원 ID
     * @return 프로필 사진 경로 (문자열)
     */
    public String getProfileImagePath(int empId) {
        return nfcDao.findProfileImagePath(empId);
    }
}
