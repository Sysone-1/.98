package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.nfc.NFCReader;
import com.sysone.ogamza.service.user.NFCService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;

public class NFCCardTagController {

    @FXML private Text empName;
    @FXML private Text department;
    @FXML private ImageView profileImageView;
    private volatile boolean listening = false;
    private Thread cardReaderThread;

    private final NFCService nfcService = NFCService.getInstance();

    private String scannedUID = null;

    /**
        카드 감지 루프 시작
     */
    public void startListeningLoop() {
        listening = true;

        cardReaderThread = new Thread(() -> {
            while (listening) {
                String uid = NFCReader.readUID();

                if (uid != null && !uid.equals(scannedUID)) {
                    scannedUID = uid;
                    Platform.runLater(() -> {
                        if(nfcService.getEmployeeInfo(uid.getBytes()) == null) {
                            nfcService.insertUnauthorizedAccessTime();

                            Image image = new Image(getClass().getResource("/images/fail.jpg").toExternalForm());

                            profileImageView.setImage(image);
                            profileImageView.setVisible(true);

                            String text = "미인가 사원증입니다.";
                            empName.setText(text);

                            System.out.println("❌ 미인가 카드입니다.");
                            return;
                        }
                        onCardDetected();
                    });

                    try {
                        Thread.sleep(2000);
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
        카드 데이터 읽기
     */
    @FXML
    private void onCardDetected() {
        String[] data = nfcService.readDataFromCard(4, 3).split(","); // 블록 4~6

        if (data != null) {
            String dir = nfcService.getProfileDir(Integer.parseInt(data[0]));
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
        감지 루프 종료
     */
    public void stopListeningLoop() {
        listening = false;
        if (cardReaderThread != null && cardReaderThread.isAlive()) {
            cardReaderThread.interrupt();
        }
    }
}
