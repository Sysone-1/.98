package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeCreateDTO;
import com.sysone.ogamza.nfc.NFCReader;
import com.sysone.ogamza.service.user.NFCService;
import com.sysone.ogamza.utils.api.alert.AlertCreate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class NFCRegisterController {

    @FXML private TextField nameField, emailField, telField, picField, vacNumField;
    @FXML private ComboBox<DepartmentDTO> departmentComboBox;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private ImageView profileImageView;
    private final NFCService nfcService = NFCService.getInstance();
    private static final String DEFAULT_POSITION = "직위를 선택하세요.";


    private String scannedUID = null;

    @FXML
    public void initialize() {
        departmentComboBox.getItems().addAll(nfcService.getDepartment());
        departmentComboBox.setPromptText("부서를 선택하세요.");

        positionComboBox.getItems().addAll("인턴","사원","주임","대리","과장","차장","부장","임원");
        positionComboBox.setValue(DEFAULT_POSITION);
    }

    /**
        카드 UID 읽기
     */
    private void readCardUID() {
        String uid = NFCReader.readUID();
        if (uid == null) {
            System.out.println("❌ NFC 카드를 인식하지 못했습니다.");
        } else {
            scannedUID = uid;
            System.out.println("✅ 카드 UID 인식 성공");
        }
    }

    /**
        카드에 사번, 이름, 부서, 직급 저장하기
     */
    @FXML
    private void registerEmployee() {
        readCardUID();

        if (!isInputValid()) {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "사원 등록", "모든 항목을 입력해주세요.");
            return;
        }

        EmployeeCreateDTO dto = buildEmployeeCreateDTO();

        if (nfcService.getEmployeeInfo(dto.getCardId()) != null) {
            nfcService.deleteEmployee(dto);
        }

        String empId = nfcService.createEmployee(dto);
        if (empId == null) {
            System.out.println("❌ DB 저장 실패");
            return;
        }

        String data = empId + "," + dto.getName() + "," + getSelectedDepartmentName() + "," + dto.getPosition();

        if (nfcService.writeDataToCard(data.getBytes(StandardCharsets.UTF_8))) {
            System.out.println("✅ 카드에 정보 저장 완료");

            AlertCreate.showAlert(Alert.AlertType.INFORMATION, "등록 완료", "사원 등록이 완료되었습니다.");

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("❌ 카드 쓰기에 실패했습니다.");

            AlertCreate.showAlert(Alert.AlertType.ERROR, "등록 실패", "사원 등록에 실패했습니다..");
        }
        scannedUID = null;
    }

    /**
        입력값 누락 검사
     */
    private boolean isInputValid() {
        return !nameField.getText().trim().isEmpty()
                && departmentComboBox.getValue() != null
                && !positionComboBox.getValue().equals(DEFAULT_POSITION)
                && !emailField.getText().trim().isEmpty()
                && !telField.getText().trim().isEmpty()
                && !picField.getText().trim().isEmpty()
                && !vacNumField.getText().trim().isEmpty()
                && scannedUID != null;
    }

    /**
        employee dto 생성
     */
    private EmployeeCreateDTO buildEmployeeCreateDTO() {
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setName(nameField.getText().trim());
        dto.setDepartmentId(departmentComboBox.getValue().getId());
        dto.setPosition(positionComboBox.getValue());
        dto.setEmail(emailField.getText().trim());
        dto.setTelNum(telField.getText().trim());
        dto.setPassword("1234");
        dto.setPicDir(picField.getText().trim());
        dto.setVacNum(Integer.parseInt(vacNumField.getText().trim()));
        dto.setCardId(scannedUID.getBytes(StandardCharsets.UTF_8));
        return dto;
    }

    /**
        부서 번호로 부서 이름 조회
     */
    private String getSelectedDepartmentName() {
        DepartmentDTO selected = departmentComboBox.getValue();
        return selected != null ? selected.getName() : "";
    }

    /**
        프로필 사진 넣기
     */
    @FXML
    private void handleImageBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("이미지 선택");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();

                Image image = new Image(selectedFile.toURI().toString());

                if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
                    throw new IllegalArgumentException("유효하지 않은 이미지입니다.");
                }

                Path targetPath = Paths.get("src/main/resources/images", fileName);

                Files.createDirectories(targetPath.getParent());

                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                picField.setText("/images/" + fileName);

                profileImageView.setImage(image);
                profileImageView.setFitWidth(110);
                profileImageView.setFitHeight(160);
                profileImageView.setPreserveRatio(false);
                profileImageView.setSmooth(true);
                profileImageView.setCache(true);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setHeaderText(null);
                alert.setContentText("선택한 파일은 유효한 이미지가 아닙니다. \npng, jpg, jpeg, gif 파일을 업로드해주세요.");
                alert.showAndWait();
            }
            catch (IOException e) {
                e.printStackTrace();
                picField.setText("❌ 이미지 복사 실패");
            }
        }
    }
}
