package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.dao.admin.EmployeeDAO;
import com.sysone.ogamza.dto.admin.EmployeeDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 사원 등록 컨트롤러
 */
public class EmployeeRegistController implements Initializable {

    @FXML private TextField employeeIdField;
    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private TextField nfcUidField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private EmployeeDAO employeeDAO;
    @lombok.Setter
    private EmployeeManagementController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDAO = new EmployeeDAO();

        setupPositionComboBox();
        setupDepartmentComboBox();
    }

    /**
     * 직급 콤보박스 설정
     */
    private void setupPositionComboBox() {
        List<String> positions = Arrays.asList("인턴", "사원", "대리", "과장", "차장", "부장", "임원");
        positionComboBox.setItems(FXCollections.observableArrayList(positions));
    }

    /**
     * 부서 콤보박스 설정
     */
    private void setupDepartmentComboBox() {
        Task<List<String>> loadTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return employeeDAO.getAllDepartments();
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<String> departments = loadTask.getValue();
            Platform.runLater(() -> {
                departmentComboBox.setItems(FXCollections.observableArrayList(departments));
            });
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 저장 처리
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        EmployeeDTO employee = createEmployeeFromInput();
        String departmentName = departmentComboBox.getValue();

        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int departmentId = employeeDAO.getDepartmentId(departmentName);
                return employeeDAO.insertEmployee(employee, departmentId);
            }
        };

        saveTask.setOnSucceeded(e -> {
            boolean success = saveTask.getValue();
            Platform.runLater(() -> {
                if (success) {
                    showAlert("성공", "사원이 성공적으로 등록되었습니다.", Alert.AlertType.INFORMATION);

                    // 부모 컨트롤러에서 목록 새로고침
                    if (parentController != null) {
                        parentController.refreshEmployeeList();
                    }

                    // 창 닫기
                    handleCancel();
                } else {
                    showAlert("오류", "사원 등록에 실패했습니다.", Alert.AlertType.ERROR);
                }
            });
        });

        saveTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showAlert("오류", "시스템 오류가 발생했습니다.", Alert.AlertType.ERROR);
            });
        });

        Thread thread = new Thread(saveTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 입력값 검증
     */
    private boolean validateInput() {
        if (employeeIdField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "사번을 입력해주세요.", Alert.AlertType.WARNING);
            employeeIdField.requestFocus();
            return false;
        }

        if (employeeNameField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이름을 입력해주세요.", Alert.AlertType.WARNING);
            employeeNameField.requestFocus();
            return false;
        }

        if (departmentComboBox.getValue() == null) {
            showAlert("입력 오류", "부서를 선택해주세요.", Alert.AlertType.WARNING);
            departmentComboBox.requestFocus();
            return false;
        }

        if (positionComboBox.getValue() == null) {
            showAlert("입력 오류", "직급을 선택해주세요.", Alert.AlertType.WARNING);
            positionComboBox.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이메일을 입력해주세요.", Alert.AlertType.WARNING);
            emailField.requestFocus();
            return false;
        }

        if (telField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "전화번호를 입력해주세요.", Alert.AlertType.WARNING);
            telField.requestFocus();
            return false;
        }

        // 사번 숫자 검증
        try {
            Integer.parseInt(employeeIdField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("입력 오류", "사번은 숫자로 입력해주세요.", Alert.AlertType.WARNING);
            employeeIdField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * 입력값으로부터 EmployeeDTO 생성
     */
    private EmployeeDTO createEmployeeFromInput() {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setEmployeeId(Integer.parseInt(employeeIdField.getText().trim()));
        employee.setEmployeeName(employeeNameField.getText().trim());
        employee.setPosition(positionComboBox.getValue());
        employee.setEmail(emailField.getText().trim());
        employee.setTel(telField.getText().trim());
        employee.setCardUid(nfcUidField.getText().trim());
        employee.setIsDeleted(0);

        return employee;
    }

    /**
     * 취소 처리
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * 알림 창 표시
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
