package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.dto.admin.BaseRequestDTO;
import com.sysone.ogamza.service.admin.RequestService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * CompletedRequestListController (결재완료 조회 개선된 버전)
 * - 올바른 승인/거절 내역 조회
 * - UI 업데이트 개선
 * - 예외 처리 강화
 */
public class CompletedRequestListController implements Initializable {

    @FXML private TableView<BaseRequestDTO> completedTable;
    @FXML private TableColumn<BaseRequestDTO, Integer> employeeIdColumn;
    @FXML private TableColumn<BaseRequestDTO, String> employeeNameColumn;
    @FXML private TableColumn<BaseRequestDTO, String> departmentColumn;
    @FXML private TableColumn<BaseRequestDTO, String> positionColumn;
    @FXML private TableColumn<BaseRequestDTO, String> requestTypeColumn;
    @FXML private TableColumn<BaseRequestDTO, String> startDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> endDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> statusColumn;
    @FXML private TableColumn<BaseRequestDTO, String> colReason;
    @FXML private Button closeButton;
    @FXML private Button refreshButton;

    private RequestService requestService;
    private ObservableList<BaseRequestDTO> completedData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        // 데이터 로드는 setRequestService 호출 후에 하도록 제거
    }

    private void setupTableColumns() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        requestTypeColumn.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("content"));

        // 상태 표시 개선 (승인/거절 명확히 표시)
        statusColumn.setCellValueFactory(cellData -> {
            BaseRequestDTO request = cellData.getValue();
            String statusText = switch (request.getIsGranted()) {
                case 1 -> "✅ 승인";
                case 2 -> "❌ 거절";
                default -> "⏳ 대기";
            };
            return new SimpleStringProperty(statusText);
        });
    }

    @FXML
    private void handleRefresh() {
        loadCompletedData();
        System.out.println("결재 완료 내역 새로고침 완료");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * RequestService 설정 및 데이터 로드
     */
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
        loadCompletedData();
    }

    /**
     * 결재완료 데이터 로드 (개선된 로직)
     */
    public void loadCompletedData() {
        try {
            if (requestService == null) {
                System.err.println("RequestService가 설정되지 않았습니다.");
                return;
            }

            // 모든 승인/거절된 요청 조회 (is_granted IN (1, 2) 조건 포함)
            List<BaseRequestDTO> requests = requestService.getAllCompletedRequests();

            if (requests == null || requests.isEmpty()) {
                System.out.println("결재 완료된 내역이 없습니다.");
                completedData = FXCollections.observableArrayList();
            } else {
                // 승인(1) 또는 거절(2) 상태만 필터링 (추가 안전 장치)
                List<BaseRequestDTO> filteredRequests = requests.stream()
                        .filter(req -> req.getIsGranted() == 1 || req.getIsGranted() == 2)
                        .toList();

                completedData = FXCollections.observableArrayList(filteredRequests);
                System.out.println("결재 완료 내역 로드 성공: " + filteredRequests.size() + "건");

                // 디버그 정보 출력
                for (BaseRequestDTO req : filteredRequests) {
                    System.out.println("완료 내역: ID=" + req.getRequestId() +
                            ", 이름=" + req.getEmployeeName() +
                            ", 상태=" + req.getIsGranted() +
                            ", 타입=" + req.getRequestType());
                }
            }

            completedTable.setItems(completedData);

        } catch (Exception e) {
            System.err.println("결재 완료 내역 로드 실패: " + e.getMessage());
            e.printStackTrace();

            // 오류 시 빈 리스트 설정
            completedData = FXCollections.observableArrayList();
            completedTable.setItems(completedData);
        }
    }
}
