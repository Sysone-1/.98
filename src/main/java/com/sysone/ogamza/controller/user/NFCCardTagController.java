package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.nfc.NFCReader;
import com.sysone.ogamza.service.user.NFCService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;

/**
 * NFC 카드 태그 기능을 담당하는 컨트롤러입니다.
 * <p>
 * 사원증 카드의 UID를 실시간으로 감지하여, 등록된 사원의 정보 및 프로필 이미지를 조회하고 표시합니다.
 * 미인가 카드의 경우 알림과 함께 출입 기록을 저장합니다.
 *
 * @author 김민호
 */
public class NFCCardTagController {

    @FXML private Text empName;
    @FXML private Text department;
    @FXML private ImageView profileImageView;
    private volatile boolean listening = false;
    private Thread cardReaderThread;

    private final NFCService nfcService = NFCService.getInstance();

    private String scannedUID = null;

    /**
     * 카드 감지 루프를 시작합니다.
     * 별도 스레드에서 주기적으로 UID를 읽고, 새로운 UID가 감지되면 정보를 화면에 출력합니다.
     */
    public void startListeningLoop() {
        listening = true;

        cardReaderThread = new Thread(() -> {
            while (listening) {
                String uid = NFCReader.readUID();

                if (uid != null && !uid.equals(scannedUID)) {
                    scannedUID = uid;
                    Platform.runLater(() -> {
                        if(nfcService.getEmployeeNameByCardId(uid.getBytes()) == null) {
                            nfcService.insertUnauthorizedAccessTime();

                            Image image = new Image(getClass().getResource("/images/fail.jpg").toExternalForm());

                            profileImageView.setImage(image);
                            profileImageView.setVisible(true);

                            String text = "미인가 사원증입니다.";
                            empName.setText(text);

                            System.out.println("❌ 미인가 카드입니다.");
                            return;
                        }
                        detecteCard();
                    });

                    try {
                        Thread.sleep(2000);
                        empName.setText(" ");
                        department.setText(" ");
                        profileImageView.setVisible(false);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        cardReaderThread.setDaemon(true);
        cardReaderThread.start();
    }

    /**
     * UID를 통해 카드에 저장된 사원 데이터를 읽고 화면에 표시합니다.
     * - 블록 4~6에서 데이터 읽기
     * - 프로필 이미지 및 사원 이름/부서 표시
     * - 출입 시간 저장
     */
    @FXML
    private void detecteCard() {
        String[] data = nfcService.readDataFromCard(4, 3).split(","); // 블록 4~6

        if (data != null) {
            String dir = nfcService.getProfileImagePath(Integer.parseInt(data[0]));
            File file = new File("src/main/resources" + dir);
            Image image = new Image(file.toURI().toString());

            profileImageView.setImage(image);
            profileImageView.setVisible(true);

            profileImageView.setFitWidth(400);
            profileImageView.setFitHeight(450);
            profileImageView.setPreserveRatio(false);

            String eName = data[1] + "님";
            String eDepartment = data[2];
            empName.setText(eName);
            department.setText(eDepartment);

            nfcService.insertAccessTime(Integer.parseInt(data[0]));
            System.out.println("✅ 카드 데이터 읽기 완료");
        } else {
            System.out.println("❌ 카드 데이터 읽기 실패");
        }
        scannedUID = null;
    }

    /**
     * 카드 감지 루프를 종료합니다.
     * 백그라운드 스레드를 안전하게 종료합니다.
     */
    public void stopListeningLoop() {
        listening = false;
        if (cardReaderThread != null && cardReaderThread.isAlive()) {
            cardReaderThread.interrupt();
        }
    }
}
