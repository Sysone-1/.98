/**
 * ============================================
 * 쪽지 작성 컨트롤러 (WriteMessageController)
 * ============================================
 * - 부서 선택 → 사원 목록 로드 → 수신자 선택 → 쪽지 작성 후 전송
 * - 부서 및 사원 데이터는 DB에서 동적으로 로드
 * - 메시지 전송 완료 후 알림 표시 및 모달 종료
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ============================================
 */

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteMessageController {

    @FXML private ComboBox<String> deptComboBox;        // 부서 선택 콤보박스
    @FXML private ComboBox<String> receiverComboBox;    // 수신자 선택 콤보박스
    @FXML private TextArea contentArea;                 // 쪽지 입력 영역
    @FXML private Button sendButton;                    // 보내기 버튼
    @FXML private Button cancelButton;                  // 취소 버튼

    private final SenderMessageService messageService = new SenderMessageService(); // 서비스 호출용 객체

    // 부서명 → 부서 ID 맵핑
    private final Map<String, Integer> deptMap = new HashMap<>();
    // 사원명 → 사원 ID 맵핑
    private final Map<String, Integer> employeeMap = new HashMap<>();

    /**
     * ▶ 컨트롤러 초기화 시 실행됨
     * - 부서 목록 로드
     * - 부서 선택 시 동작 정의
     * - 전송 버튼 이벤트 등록
     */
    @FXML
    private void initialize() {
        loadDepartments();
        setupDepartmentSelection();
        setupSendButton();
    }

    /**
     * ▶ 전송 버튼 동작 정의
     * - 수신자와 내용이 모두 입력되어야 전송 가능
     * - DB에 메시지 저장 후 알림 표시 및 폼 초기화
     */
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

    /**
     * ▶ 쪽지 입력 폼 초기화
     */
    private void clearForm() {
        receiverComboBox.getSelectionModel().clearSelection();
        contentArea.clear();
    }

    /**
     * ▶ 전송 완료 후 알림창 표시 및 모달 창 종료
     */
    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // 창 닫기
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }

    /**
     * ▶ 유효성 검사 실패 시 경고창 표시
     */
    private void ShowWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * ▶ 부서 콤보박스 선택 이벤트 설정
     * - 부서 선택 시 해당 부서의 사원 목록을 로드
     */
    private void setupDepartmentSelection() {
        deptComboBox.setOnAction(event -> {
            String selectedDept = deptComboBox.getValue();
            if (selectedDept != null) {
                int deptId = deptMap.get(selectedDept);
                loadEmployeesByDepartment(deptId);
            }
        });
    }

    /**
     * ▶ 부서 ID에 따라 해당 부서의 사원 목록 조회 및 설정
     *
     * @param deptId 선택된 부서 ID
     */
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

    /**
     * ▶ DB에서 부서 목록을 조회하고 콤보박스에 바인딩
     */
    private void loadDepartments() {
        List<DepartmentDTO> departments = messageService.getAllDepartment();
        ObservableList<String> deptNames = FXCollections.observableArrayList();

        for (DepartmentDTO dept : departments) {
            deptNames.add(dept.getName());
            deptMap.put(dept.getName(), dept.getId());
        }

        deptComboBox.setItems(deptNames);
    }

    /**
     * ▶ 취소 버튼 클릭 시 현재 모달 창 종료
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}