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
 *
 * @author 허겸
 * @since 2025-07-22
 */
public class CompletedRequestListController implements Initializable {

    // ===== FXML 테이블/버튼 컴포넌트 =====
    @FXML private TableView<BaseRequestDTO> completedTable; // 결재완료 요청 목록 테이블
    @FXML private TableColumn<BaseRequestDTO, Integer> employeeIdColumn;    // 사번
    @FXML private TableColumn<BaseRequestDTO, String> employeeNameColumn;   // 이름
    @FXML private TableColumn<BaseRequestDTO, String> departmentColumn;     // 부서명
    @FXML private TableColumn<BaseRequestDTO, String> positionColumn;       // 직급
    @FXML private TableColumn<BaseRequestDTO, String> startDateColumn;      // 시작일
    @FXML private TableColumn<BaseRequestDTO, String> endDateColumn;        // 종료일
    @FXML private TableColumn<BaseRequestDTO, String> statusColumn;         // 승인/거절 상태
    @FXML private TableColumn<BaseRequestDTO, String> colReason;            // 사유(내용)

    // 실제 DB SCHEDULE_TYPE 표시용 컬럼 추가(상세 종류)
    @FXML private TableColumn<BaseRequestDTO, String> scheduleTypeColumn;

    @FXML private Button closeButton; // 닫기 버튼

    private RequestService requestService;
    private ObservableList<BaseRequestDTO> completedData;


    /**
     * 컨트롤러 초기화
     * - 테이블 컬럼 셋업
     * - 실제 데이터 로딩(init 아님. Service setter에서)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        // 데이터 로드는 setRequestService 호출 후에 하도록 제거
    }

    /**
     * 테이블 컬럼별 데이터 바인딩 및 이름 설정
     * - DB 실제값(상세 종류) 컬럼 표시
     * - 상태(승인/거절/대기) 가독성 좋게 변환
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

        // DB 실제값(SCHEDULE_TYPE) 표시 컬럼 추가
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

    /**
     * 닫기 버튼 클릭시 창 닫기
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * RequestService 주입 및 데이터 로드
     * - 부모 컨트롤러에서 세팅
     * - 실제 데이터 로딩 시점 entry point
     */
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
        loadCompletedData();
    }

    /**
     * 결재 완료/거절된 요청 목록 DB에서 조회하여 TableView에 세팅
     * - 실제 SCHEDULE_TYPE 컬럼까지 반영
     * - 승인(1), 거절(2) 상태 요청만 필터
     */
    public void loadCompletedData() {
        try {
            if (requestService == null) {
                System.err.println("RequestService가 설정되지 않았습니다.");
                return;
            }

            // 승인/거절건만 가져오기 (is_granted IN (1,2))
            List<BaseRequestDTO> requests = requestService.getAllCompletedRequests();

            if (requests == null || requests.isEmpty()) {
                System.out.println("결재 완료된 내역이 없습니다.");
                completedData = FXCollections.observableArrayList();
            } else {
                List<BaseRequestDTO> filteredRequests = requests.stream()
                        .filter(req -> req.getIsGranted() == 1 || req.getIsGranted() == 2)
                        .toList();

                completedData = FXCollections.observableArrayList(filteredRequests);
                System.out.println("결재 완료 내역 로드 성공: " + filteredRequests.size() + "건");
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
