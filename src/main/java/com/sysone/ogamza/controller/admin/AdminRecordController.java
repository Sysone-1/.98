package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.dto.admin.RecordDTO;
import com.sysone.ogamza.service.admin.AdminRecordService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

import java.time.LocalDate;

/**
 * 출입로그 관리 페이지 컨트롤러 클래스
 *
 * <p>
 * 관리자용 출입로그 관리 컨트롤러 클래스입니다.
 * 출입 기록 테이블 초기화, 필터링, 검색 기능을 담당합니다.
 * </p>
 *
 * @author 조윤상
 */
public class AdminRecordController {

    @FXML
    private TableView<RecordDTO> recordTable;

    @FXML
    private TableColumn<RecordDTO, Long> colId;

    @FXML
    private TableColumn<RecordDTO, String> colName;

    @FXML
    private TableColumn<RecordDTO, Long> colDept;

    @FXML
    private TableColumn<RecordDTO, String> colPosition;

    @FXML
    private TableColumn<RecordDTO, String> colTime;

    @FXML
    private TableColumn<RecordDTO, String> colApproval;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private ComboBox<String> positionComboBox;

    @FXML
    private ComboBox<String> approvalComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField searchField;

    private final AdminRecordService adminRecordService;

    private final ObservableList<RecordDTO> masterData;

    /**
     * 기본 생성자
     * AdminRecordService 객체 생성 및 masterData 초기화
     */
    public AdminRecordController() {
        this.adminRecordService = new AdminRecordService();
        masterData = FXCollections.observableArrayList();
    }

    /**
     * FXML 초기화 메서드
     * 테이블 컬럼 초기화, 콤보박스 데이터 로딩, 필터 리스너 설정, DB에서 데이터 로드 수행
     */
    @FXML
    public void initialize() {
        initTableColumns();
        loadComboBoxData();
        adminRecordService.loadAllRecordsFromDB(masterData, recordTable);
        setupFilters();

        // 검색 필드에서 Enter 키 입력 시 필터링 실행
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                filterData();
            }
        });
    }

    /**
     * 검색 버튼 클릭 이벤트 핸들러
     * 필터링 기능 실행
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void onSearchButtonClicked(ActionEvent event) {
        filterData();
    }

    /**
     * 테이블 컬럼의 PropertyValueFactory를 설정합니다.
     */
    private void initTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("taggingTime"));
        colApproval.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
    }

    /**
     * 부서, 직급, 출입여부 콤보박스에 값을 로드하고 기본값을 설정합니다.
     */
    private void loadComboBoxData() {
        departmentComboBox.getItems().addAll("부서 전체", "기획팀", "개발1팀", "개발2팀", "디자인팀", "QA팀");
        positionComboBox.getItems().addAll("직급 전체", "인턴", "사원", "주임", "대리", "과장", "차장", "부장", "임원");
        approvalComboBox.getItems().addAll("출입여부 전체", "출입", "출입 거부");

        departmentComboBox.setValue("부서 전체");
        positionComboBox.setValue("직급 전체");
        approvalComboBox.setValue("출입여부 전체");
    }

    /**
     * 시작일, 종료일, 콤보박스들의 값 변경 이벤트에 필터링 리스너를 등록합니다.
     */
    private void setupFilters() {
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterData());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterData());
        departmentComboBox.setOnAction(e -> filterData());
        positionComboBox.setOnAction(e -> filterData());
        approvalComboBox.setOnAction(e -> filterData());
    }

    /**
     * 현재 필터 및 검색어 조건에 따라 masterData에서 데이터를 필터링하여 테이블에 표시합니다.
     */
    private void filterData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        String selectedDept = departmentComboBox.getValue();
        String selectedPos = positionComboBox.getValue();
        String selectedApproval = approvalComboBox.getValue();
        String keyword = searchField.getText().toLowerCase().trim();

        ObservableList<RecordDTO> filtered = masterData.filtered(recordDTO -> {
            boolean dateMatch = true;
            if (start != null && end != null && recordDTO.getTaggingTime() != null) {
                LocalDate date = recordDTO.getTaggingTime().toLocalDate();
                dateMatch = (date.isEqual(start) || date.isAfter(start)) &&
                        (date.isEqual(end) || date.isBefore(end));
            }

            boolean deptMatch = selectedDept.equals("부서 전체") || recordDTO.getDepartmentName().equals(selectedDept);
            boolean posMatch = selectedPos.equals("직급 전체") || recordDTO.getPosition().equals(selectedPos);
            boolean approvalMatch = selectedApproval.equals("출입여부 전체") || recordDTO.getApprovalStatus().equals(selectedApproval);

            boolean keywordMatch = keyword.isEmpty() || (
                    String.valueOf(recordDTO.getEmployeeId()).contains(keyword) ||
                            recordDTO.getEmployeeName().toLowerCase().contains(keyword) ||
                            recordDTO.getDepartmentName().toLowerCase().contains(keyword) ||
                            recordDTO.getPosition().toLowerCase().contains(keyword) ||
                            recordDTO.getApprovalStatus().toLowerCase().contains(keyword) ||
                            recordDTO.getTaggingTime().toString().toLowerCase().contains(keyword)
            );

            return dateMatch && deptMatch && posMatch && approvalMatch && keywordMatch;
        });

        recordTable.setItems(filtered);
    }

    /**
     * 날짜 초기화 버튼 클릭 시, 시작일과 종료일을 초기화합니다.
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void onResetDateButtonClicked(ActionEvent event) {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        filterData();
    }
}
