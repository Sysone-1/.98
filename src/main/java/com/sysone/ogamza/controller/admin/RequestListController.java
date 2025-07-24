package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.enums.RequestType;
import com.sysone.ogamza.dto.admin.BaseRequestDTO;
import com.sysone.ogamza.service.admin.RequestService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class RequestListController implements Initializable {

    @FXML private TableView<BaseRequestDTO> requestTable;
    @FXML private TableColumn<BaseRequestDTO, Integer> employeeIdColumn;
    @FXML private TableColumn<BaseRequestDTO, String> employeeNameColumn;
    @FXML private TableColumn<BaseRequestDTO, String> departmentColumn;
    @FXML private TableColumn<BaseRequestDTO, String> positionColumn;
    @FXML private TableColumn<BaseRequestDTO, String> scheduleType;
    @FXML private TableColumn<BaseRequestDTO, String> startDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> endDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> colReason;
    @FXML private TableColumn<BaseRequestDTO, String> statusColumn;

    // 🔥 핵심 추가: DB 실제값 표시용 컬럼
    @FXML private TableColumn<BaseRequestDTO, String> scheduleTypeColumn;

    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private Button closeButton;
    @FXML private Button refreshButton;
    @FXML private Label titleLabel;

    private RequestService requestService;
    private RequestType currentRequestType;
    private ObservableList<BaseRequestDTO> requestData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestService = new RequestService();
        setupTableColumns();

        // 테이블 선택 이벤트
        requestTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonState());

        // 초기에는 버튼 비활성화
        approveButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    /**
     * 요청 타입 설정 (외부에서 호출)
     */
    public void setRequestType(RequestType requestType) {
        this.currentRequestType = requestType;
        if (titleLabel != null) {
            titleLabel.setText(requestType.getDisplayName());
        }
        loadRequestData();
    }

    /**
     * 테이블 컬럼 설정 (DB 실제값 표시 개선)
     */
    private void setupTableColumns() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        // 🔥 수정: sType → scheduleType으로 변경
        scheduleType.setCellValueFactory(new PropertyValueFactory<>("scheduleType"));

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("content"));

        // 기존 scheduleTypeColumn 설정은 그대로 유지
        if (scheduleTypeColumn != null) {
            scheduleTypeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getScheduleType())
            );
            scheduleTypeColumn.setText("상세 종류");
        }

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatusString())
        );
    }

    /**
     * 요청 데이터 로드 (DB 실제값 포함)
     */
    private void loadRequestData() {
        try {
            List<BaseRequestDTO> requests = requestService.getPendingList(currentRequestType);
            requestData = FXCollections.observableArrayList(requests);
            requestTable.setItems(requestData);
            updateButtonState();

            System.out.println(currentRequestType.getDisplayName() + " 목록 로드 완료: " + requests.size() + "건");

            // 🔥 디버그: DB 실제값 출력
            for (BaseRequestDTO req : requests) {
                System.out.println("요청 내역: ID=" + req.getRequestId() +
                        ", 이름=" + req.getEmployeeName() +
                        ", 화면타입=" + req.getRequestType() +
                        ", DB실제값=" + req.getScheduleType());
            }
        } catch (Exception e) {
            System.err.println("요청 목록 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateButtonState() {
        BaseRequestDTO selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        boolean isSelected = selectedRequest != null;
        // isGranted==0 (대기)일 때만 승인/거절 활성화
        boolean isPending = isSelected && selectedRequest.getIsGranted() == 0;

        approveButton.setDisable(!isPending);
        rejectButton.setDisable(!isPending);
    }

    @FXML
    private void handleApprove() {
        processRequest("승인", 1, "승인 완료", "승인되었습니다.");
    }

    @FXML
    private void handleReject() {
        processRequest("거절", 2, "거절 완료", "거절되었습니다.");
    }

    /**
     * 승인/거절 처리 (카운팅 동기화 개선)
     */
    private void processRequest(String action, int newStatus, String successTitle, String successMsg) {
        BaseRequestDTO selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) return;

        // 확인 다이얼로그
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(action + " 확인");
        confirm.setHeaderText(null);
        confirm.setContentText(selectedRequest.getEmployeeName() + "님의 " +
                currentRequestType.getDisplayName() + "(" + selectedRequest.getScheduleType() + ")을(를) " +
                action + "하시겠습니까?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // 백그라운드 스레드에서 DB 처리
            Task<Boolean> updateTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    // DB 상태 업데이트 (트랜잭션 포함)
                    boolean success = requestService.updateRequestStatus(
                            currentRequestType,
                            selectedRequest.getRequestId(),
                            String.valueOf(newStatus)
                    );

                    if (success) {
                        System.out.println("DB 업데이트 성공 - " + action + ": ID=" +
                                selectedRequest.getRequestId() + ", 실제값=" + selectedRequest.getScheduleType());
                    } else {
                        System.err.println("DB 업데이트 실패 - " + action + ": ID=" +
                                selectedRequest.getRequestId());
                    }

                    return success;
                }
            };

            updateTask.setOnSucceeded(e -> {
                Boolean success = updateTask.getValue();
                Platform.runLater(() -> {
                    if (success) {
                        // 🔥 핵심: UI에서 처리된 항목 즉시 제거 (카운팅 동기화)
                        requestData.remove(selectedRequest);
                        updateButtonState();

                        // 성공 메시지 표시
                        Alert success_alert = new Alert(Alert.AlertType.INFORMATION);
                        success_alert.setTitle(successTitle);
                        success_alert.setHeaderText(null);
                        success_alert.setContentText(selectedRequest.getEmployeeName() + "님의 " +
                                currentRequestType.getDisplayName() + "(" + selectedRequest.getScheduleType() + ")이(가) " + successMsg);
                        success_alert.showAndWait();

                        System.out.println("✅ UI 업데이트 완료 - " + action + " 처리됨 (실시간 카운팅 반영)");


                    } else {
                        // 실패 메시지
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("처리 실패");
                        error.setHeaderText(null);
                        error.setContentText(action + " 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
                        error.showAndWait();
                    }
                });
            });

            updateTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("시스템 오류");
                    error.setHeaderText(null);
                    error.setContentText("시스템 오류가 발생했습니다: " + updateTask.getException().getMessage());
                    error.showAndWait();
                });
            });

            // 백그라운드 스레드 실행
            Thread thread = new Thread(updateTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    private void handleRefresh() {
        loadRequestData();
        System.out.println(currentRequestType.getDisplayName() + " 목록 새로고침 완료");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
