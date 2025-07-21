package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.nfc.NFCReader;
import com.sysone.ogamza.service.user.NFCService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.nio.charset.StandardCharsets;

public class NFCController {

    @FXML private TextField empIdField, nameField, departmentField, positionField;
    @FXML private Label uidLabel;
    @FXML private TextArea resultArea, cardDataArea;

    private final NFCService nfcService = new NFCService();

    private String scannedUID = null;

    /*
        카드 UID 읽기
    */
    private void readCardUID() {
        String uid = NFCReader.readUID();
        if (uid == null) {
            resultArea.setText("❌ NFC 카드를 인식하지 못했습니다.");
        } else {
            scannedUID = uid;
            uidLabel.setText("UID: " + uid);
            resultArea.setText("✅ 카드 UID 인식 성공");
        }
    }

    /*
        카드에 사번, 이름, 부서, 직급 저장하기
    */
    @FXML
    private void onRegisterEmployee(ActionEvent event) {
        readCardUID();

        String empId = empIdField.getText().trim();
        String name = nameField.getText().trim();
        String dept = departmentField.getText().trim();
        String position = positionField.getText().trim();

        if (empId.isEmpty() || name.isEmpty() || dept.isEmpty() || position.isEmpty() || scannedUID == null) {
            resultArea.setText("❌ 입력값 또는 카드 UID가 없습니다.");
            return;
        }

        String data = empId + "," + name + "," + dept + "," + position;
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        if (nfcService.writeDataToCard(bytes)) {
            resultArea.setText("✅ 카드에 정보 저장 완료");
        } else {
            resultArea.setText("❌ 카드 쓰기에 실패했습니다.");
        }
        scannedUID = null;
    }

    /*
        카드 데이터 읽기
    */
    @FXML
    private void onReadCardData(ActionEvent event) {
        readCardUID();

        String data = nfcService.readDataFromCard(4, 3); // 블록 4~6
        if (data != null) {
            cardDataArea.setText(data);
            resultArea.setText("✅ 카드 데이터 읽기 완료");
        } else {
            resultArea.setText("❌ 카드 데이터 읽기 실패");
        }
        scannedUID = null;
    }
}

