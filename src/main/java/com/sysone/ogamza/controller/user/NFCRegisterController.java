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

/**
 * NFC 카드를 이용한 사원 등록 기능을 제공하는 컨트롤러입니다.
 * <p>
 * 사원의 정보 입력과 카드 UID 인식, 카드 쓰기, 이미지 등록 기능을 포함하며
 * 사원 정보를 DB에 저장하고 카드를 통해 인증할 수 있도록 합니다.
 *
 * @author 김민호
 */
public class NFCRegisterController {

    @FXML private TextField nameField, emailField, telField, picField, vacNumField;
    @FXML private ComboBox<DepartmentDTO> departmentComboBox;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private ImageView profileImageView;
    private final NFCService nfcService = NFCService.getInstance();
    private static final String DEFAULT_POSITION = "직위를 선택하세요.";
    private String scannedUID = null;

    /**
     * 컨트롤러 초기화 메서드
     * 부서 목록 및 직위 콤보박스를 초기화합니다.
     */
    @FXML
    public void initialize() {
        departmentComboBox.getItems().addAll(nfcService.getDepartments());
        departmentComboBox.setPromptText("부서를 선택하세요.");

        positionComboBox.getItems().addAll("인턴","사원","주임","대리","과장","차장","부장","임원");
        positionComboBox.setValue(DEFAULT_POSITION);
    }

    /**
     * NFC 카드의 UID를 읽어옵니다.
     * UID가 감지되지 않으면 오류 메시지를 출력합니다.
     */
    private void loadCardUID() {
        String uid = NFCReader.readUID();
        if (uid == null) {
            System.out.println("❌ NFC 카드를 인식하지 못했습니다.");
        } else {
            scannedUID = uid;
            System.out.println("✅ 카드 UID 인식 성공");
        }
    }

    /**
     * 사원 정보를 카드에 저장하고 DB에 등록합니다.
     * UID가 이미 등록되어 있는 경우 기존 정보를 삭제하고 재등록합니다.
     */
    @FXML
    private void registerEmployee() {
        loadCardUID();

        if (!isInputValid()) {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "사원 등록", "모든 항목을 입력해주세요.");
            return;
        }

        EmployeeCreateDTO dto = buildEmployeeCreateDTO();

        if (nfcService.getEmployeeNameByCardId (dto.getCardId()) != null) {
            nfcService.deleteEmployee(dto);
        }

        String empId = nfcService.registerEmployee(dto);
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
     * 입력값이 모두 유효한지 확인합니다.
     *
     * @return 모든 필드가 입력되어 있고 UID가 감지되었으면 true, 아니면 false
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
     * 입력된 정보를 기반으로 EmployeeCreateDTO 객체를 생성합니다.
     *
     * @return DTO 객체
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
     * 부서 DTO에서 부서명을 추출합니다.
     *
     * @return 부서명 문자열
     */
    private String getSelectedDepartmentName() {
        DepartmentDTO selected = departmentComboBox.getValue();
        return selected != null ? selected.getName() : "";
    }

    /**
     * 이미지 파일을 선택하고 리소스 경로로 복사한 후 이미지 뷰에 표시합니다.
     *
     * @param event 이미지 선택 버튼 클릭 이벤트
     */
    @FXML
    private void handleImageBrowseClick(ActionEvent event) {
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
