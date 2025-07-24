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
 * CompletedRequestListController (DB 실제값 표시 개선 버전)
 * - DB 실제 SCHEDULE_TYPE 값 표시 (연차, 반차, 연장근무, 휴일, 외근)
 * - 올바른 승인/거절 내역 조회
 * - UI 업데이트 개선 및 예외 처리 강화
 */
public class CompletedRequestListController implements Initializable {

    @FXML private TableView<BaseRequestDTO> completedTable;
    @FXML private TableColumn<BaseRequestDTO, Integer> employeeIdColumn;
    @FXML private TableColumn<BaseRequestDTO, String> employeeNameColumn;
    @FXML private TableColumn<BaseRequestDTO, String> departmentColumn;
    @FXML private TableColumn<BaseRequestDTO, String> positionColumn;
    @FXML private TableColumn<BaseRequestDTO, String> scheduleType;
    @FXML private TableColumn<BaseRequestDTO, String> startDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> endDateColumn;
    @FXML private TableColumn<BaseRequestDTO, String> statusColumn;
    @FXML private TableColumn<BaseRequestDTO, String> colReason;

    // 🔥 핵심 추가: DB 실제값 표시용 컬럼
    @FXML private TableColumn<BaseRequestDTO, String> scheduleTypeColumn;

    @FXML private Button closeButton;
    @FXML private Button refreshButton;

    private RequestService requestService;
    private ObservableList<BaseRequestDTO> completedData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        // 데이터 로드는 setRequestService 호출 후에 하도록 제거
    }

    /**
     * 테이블 컬럼 설정 (DB 실제값 표시 개선)
     */
    private void setupTableColumns() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        scheduleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("content"));

        // 핵심: DB 실제값(SCHEDULE_TYPE) 표시 컬럼 추가
        if (scheduleTypeColumn != null) {
            scheduleTypeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getScheduleType())
            );
            scheduleTypeColumn.setText("상세 종류"); // 컬럼 헤더명
        }

        // 상태 표시 개선 (승인/거절 명확히 표시)
        statusColumn.setCellValueFactory(cellData -> {
            BaseRequestDTO request = cellData.getValue();
            String statusText = switch (request.getIsGranted()) {
                case 1 -> "승인";
                case 2 -> "거절";
                default -> "대기";
            };
            return new SimpleStringProperty(statusText);
        });
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
     * 결재완료 데이터 로드 (DB 실제값 포함 개선)
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

                // 디버그: DB 실제값 출력
                for (BaseRequestDTO req : filteredRequests) {
                    System.out.println("완료 내역: ID=" + req.getRequestId() +
                            ", 이름=" + req.getEmployeeName() +
                            ", 상태=" + req.getIsGranted() +
                            ", 화면타입=" + req.getRequestType() +
                            ", DB실제값=" + req.getScheduleType()); // 핵심
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
