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

/**
 * 승인·반려 등 관리자 요청 상세 처리 컨트롤러
 * - 승인/반려 목록 테이블 표시 (실제 scheduleType값 반영)
 * - 승인/거절 버튼과 결과 실시간 업데이트
 * - 선택된 타입별로 데이터 뷰 동작
 * - 승인/반려시 DB 처리 및 UI 동기화
 *
 *  @author 허겸
 *  @since 2025-07-24
 */
public class RequestListController implements Initializable {

    // ======================= FXML UI 컴포넌트 =======================
    @FXML private TableView<BaseRequestDTO> requestTable;         // 요청 리스트 테이블
    @FXML private TableColumn<BaseRequestDTO, Integer> employeeIdColumn;      // 사번
    @FXML private TableColumn<BaseRequestDTO, String> employeeNameColumn;     // 사원이름
    @FXML private TableColumn<BaseRequestDTO, String> departmentColumn;       // 부서
    @FXML private TableColumn<BaseRequestDTO, String> positionColumn;         // 직급
    @FXML private TableColumn<BaseRequestDTO, String> scheduleType;           // 스케줄 타입(텍스트)
    @FXML private TableColumn<BaseRequestDTO, String> startDateColumn;        // 시작일
    @FXML private TableColumn<BaseRequestDTO, String> endDateColumn;          // 종료일
    @FXML private TableColumn<BaseRequestDTO, String> colReason;              // 사유/비고
    @FXML private TableColumn<BaseRequestDTO, String> statusColumn;           // 요청상태(대기/승인/반려)
    // 🔥 실제 DB SCHEDULE_TYPE 표시 컬럼(상세 종류)
    @FXML private TableColumn<BaseRequestDTO, String> scheduleTypeColumn;

    @FXML private Button approveButton;  // 승인 버튼
    @FXML private Button rejectButton;   // 거절 버튼
    @FXML private Button closeButton;    // 닫기 버튼
    @FXML private Label titleLabel;      // 타이틀 (요청타입별 표시)

    // ======================= 내부 변수 =======================
    private RequestService requestService;                   // 서비스 (비즈니스로직)
    private RequestType currentRequestType;                  // 현재 표시 대상 요청 유형
    private ObservableList<BaseRequestDTO> requestData;      // 테이블 바인딩 데이터

    /**
     * 테이블 컬럼별 데이터 맵핑
     * 테이블 값 선택시 승인/거절 버튼 활성화
     * 기본 상태는 비활성화로 설정
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestService = new RequestService();
        setupTableColumns();

        requestTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldVal, newVal) -> updateButtonState());

        approveButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    /**
     * 외부(부모)에서 리스트 오픈 시 type을 명시적으로 셋팅한다.
     */
    public void setRequestType(RequestType requestType) {
        this.currentRequestType = requestType;
        if (titleLabel != null) {
            titleLabel.setText(requestType.getDisplayName());
        }
        loadRequestData();
    }


    /**
     * 각 테이블 컬럼과 DTO 프로퍼티 연결
     * DB 실제 스케줄타입 값(상세) 컬럼도 반영
     */
    private void setupTableColumns() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        // DB 실제 스케줄타입 명(Map)
        scheduleType.setCellValueFactory(new PropertyValueFactory<>("scheduleType"));

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("content"));

        // DB 실제값(상세 종류) 표시 컬럼 추가
        if (scheduleTypeColumn != null) {
            scheduleTypeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getScheduleType())
            );
            scheduleTypeColumn.setText("상세 종류");
        }

        // 상태 문자열 변환(대기/승인/거절)
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatusString())
        );
    }

    /**
     * 현재 type기반 전체 요청 조회 → 테이블 세팅
     * DB scheduleType 등 실제값 포함
     */
    private void loadRequestData() {
        try {
            List<BaseRequestDTO> requests = requestService.getPendingList(currentRequestType);
            requestData = FXCollections.observableArrayList(requests);
            requestTable.setItems(requestData);
            updateButtonState(); // 초기 선택 상태에 맞춰 버튼 상태 초기화

            System.out.println(currentRequestType.getDisplayName() + " 목록 로드 완료: " + requests.size() + "건");

        } catch (Exception e) {
            System.err.println("요청 목록 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 테이블 선택 행과 그 상태에 따라 승인/거절 버튼 활성화 제어
     * (isGranted==0, 즉 '대기' 상태만 버튼 활성)
     */
    private void updateButtonState() {
        BaseRequestDTO selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        boolean isSelected = selectedRequest != null;
        boolean isPending = isSelected && selectedRequest.getIsGranted() == 0;

        approveButton.setDisable(!isPending);
        rejectButton.setDisable(!isPending);
    }


    @FXML
    private void handleApprove() {
        // 승인 처리
        processRequest("승인", 1, "승인 완료", "승인되었습니다.");
    }

    @FXML
    private void handleReject() {
        // 거절 처리
        processRequest("거절", 2, "거절 완료", "거절되었습니다.");
    }

    /**
     * 승인/거절 공통 처리 로직 (DB 갱신 → 실시간 카운트 동기화)
     * @param action 승인/거절 구분
     * @param newStatus DB 상태값(1=승인, 2=거절)
     * @param successTitle 결과창 타이틀
     * @param successMsg 결과 메시지
     */
    private void processRequest(String action, int newStatus, String successTitle, String successMsg) {
        BaseRequestDTO selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) return;

        // 확인(컨펌) 다이얼로그 팝업
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(action + " 확인");
        confirm.setHeaderText(null);
        confirm.setContentText(selectedRequest.getEmployeeName() + "님의 " +
                currentRequestType.getDisplayName() + "(" + selectedRequest.getScheduleType() + ")을(를) " +
                action + "하시겠습니까?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // 백그라운드 스레드에서 DB 상태 처리
            Task<Boolean> updateTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
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

            // DB 갱신 후 UI에서 항목 제거 및 결과 메시지
            updateTask.setOnSucceeded(e -> {
                Boolean success = updateTask.getValue();
                Platform.runLater(() -> {
                    if (success) {
                        // 실시간 카운트/목록 동기화 - 처리 완료건 즉시 제거
                        requestData.remove(selectedRequest);
                        updateButtonState();

                        // 성공 안내 알림
                        Alert success_alert = new Alert(Alert.AlertType.INFORMATION);
                        success_alert.setTitle(successTitle);
                        success_alert.setHeaderText(null);
                        success_alert.setContentText(selectedRequest.getEmployeeName() + "님의 " +
                                currentRequestType.getDisplayName() + "(" + selectedRequest.getScheduleType() + ")이(가) " + successMsg);
                        success_alert.showAndWait();

                        System.out.println("✅ UI 업데이트 완료 - " + action + " 처리됨 (실시간 카운팅 반영)");

                    } else {
                        // 실패 메시지 표시
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

            // 비동기(DB) 처리 스레드 시작
            Thread thread = new Thread(updateTask);
            thread.setDaemon(true);
            thread.start();
        }
    }


    /**
     * 새로고침
     */
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
