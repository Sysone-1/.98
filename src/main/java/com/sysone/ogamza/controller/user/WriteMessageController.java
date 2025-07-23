package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeDTO;
import com.sysone.ogamza.service.user.SenderMessageService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.management.Notification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteMessageController {
    @FXML
    private ComboBox<String> deptComboBox;

    @FXML
    private ComboBox<String> receiverComboBox;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button sendButton;


    @FXML
    private Button cancelButton;

    private final SenderMessageService messageService = new SenderMessageService();

    //부서명 -> 부서 ID
    private final Map<String, Integer> deptMap = new HashMap<>();
    //사원 이름 -> 사원 ID
    private final Map<String, Integer> employeeMap = new HashMap<>();
    
    @FXML
    private void initialize() {
        loadDepartments();
        setupDepartmentSelection();
        setupSendButton();
    }

    private void setupSendButton() {
        sendButton.setOnAction(event -> {
            String receiverName = receiverComboBox.getValue();
            String content = contentArea.getText();

            if (receiverName == null || content == null || content.isBlank()) {
                ShowWarning("수신자와 내용을 모두 입력해주세요.");
                return;
            }
            int receiverId = employeeMap.get(receiverName);
            messageService.sendMessage(receiverId, content);
            
            showNotification("쪽지가 성공적으로 전송되었습니다.");
            clearForm();
        });
    }

    private void clearForm() {
        receiverComboBox.getSelectionModel().clearSelection();
        contentArea.clear();
    }

    private void showNotification(String message) {
        Notifications.create()
                .title("알림")
                .text(message)
                .darkStyle()
                .showInformation();
    }

    private void ShowWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void setupDepartmentSelection() {
        deptComboBox.setOnAction(event -> {
            String selectedDept = deptComboBox.getValue();
            if (selectedDept != null) {
                int deptId = deptMap.get(selectedDept);
                loadEmployeesByDepartment(deptId);
            }
        });
    }

    private void loadEmployeesByDepartment(int deptId) {
        List<EmployeeDTO> employees = messageService.getAllEmployeeByDeptId(deptId);
        ObservableList<String> employeeNames = FXCollections.observableArrayList();
        employeeMap.clear();

        for (EmployeeDTO emp : employees) {
            employeeNames.add(emp.getName());
            employeeMap.put(emp.getName(), emp.getId());
        }
        receiverComboBox.setItems(employeeNames);
    }

    private void loadDepartments() {
        List<DepartmentDTO> departments = messageService.getAllDepartment();
        ObservableList<String> deptNames = FXCollections.observableArrayList();

        for (DepartmentDTO dept : departments) {
            deptNames.add(dept.getName());
            deptMap.put(dept.getName(), dept.getId());
        }
        deptComboBox.setItems(deptNames);
    }


    @FXML
    private void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
