package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.controller.user.NFCCardTagController;
import com.sysone.ogamza.dao.admin.EmployeeDAO;
import com.sysone.ogamza.dto.admin.EmployeeDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 사원 관리 컨트롤러
 * - 사원 조회, 검색, 부서별 필터링
 * - NFC 카드 등록/수정
 * - 새 사원 등록
 *
 *  @author 허겸
 *  @since 2025-07-23
 */
public class EmployeeManagementController implements Initializable {

    @FXML private ComboBox<String> departmentComboBox; // 부서 필터 콤보박스
    @FXML private TextField searchField; // 사원 검색 입력창

    @FXML private TableView<EmployeeDTO> employeeTable;
    @FXML private TableColumn<EmployeeDTO, Integer> employeeIdColumn;
    @FXML private TableColumn<EmployeeDTO, String> employeeNameColumn;
    @FXML private TableColumn<EmployeeDTO, String> departmentColumn;
    @FXML private TableColumn<EmployeeDTO, String> positionColumn;
    @FXML private TableColumn<EmployeeDTO, String> emailColumn;
    @FXML private TableColumn<EmployeeDTO, String> telColumn;

    @FXML private Label selectedEmployeeLabel;        // 선택된 사원 표시 라벨
    @FXML private TextField nfcUidField;              // NFC UID 입력/표시창
    @FXML private Button registerNfcButton;           // NFC 등록 버튼

    private EmployeeDAO employeeDAO;                       // DB 접근 DAO
    private ObservableList<EmployeeDTO> employeeData;      // 테이블 데이터 모델
    private EmployeeDTO selectedEmployee;                  // 선택된 사원 객체

    /**
     * 테이블 컬럼 설정
     * 행 선택 이벤트 바인딩
     * 부서 콤보박스 채우기
     * 전체 사원 목록 로드
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDAO = new EmployeeDAO();

        setupTableColumns();
        setupTableSelection();
        setupDepartmentComboBox();

        // 초기 데이터 로드
        loadAllEmployees();
    }

    /**
     * [NFC 카드 태깅 창] 버튼 클릭시 태그 대기 창 보여주기
     */
    @FXML
    private void handleTagCardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/EmployeeTag.fxml"));
            Parent formRoot = loader.load();

            NFCCardTagController tagController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("카드 태그");

            Window parentWindow = ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(parentWindow);

            Scene dialogScene = new Scene(formRoot);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);

            dialogStage.setOnCloseRequest(e -> {
                tagController.stopListeningLoop();
            });

            tagController.startListeningLoop();
            dialogStage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 테이블 컬럼별 DTO 프로퍼티 연결
     */
    private void setupTableColumns() {
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telColumn.setCellValueFactory(new PropertyValueFactory<>("tel"));
    }

    /**
     * 테이블 행(사원) 선택시 선택정보 라벨·필드 업데이트
     */
    private void setupTableSelection() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedEmployee = newValue;
                    updateSelectedEmployeeDisplay();
                }
        );
    }

    /**
     * 부서 콤보박스 비동기 DB조회 후 아이템 셋팅
     */
    private void setupDepartmentComboBox() {
        Task<List<String>> loadDepartmentsTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return employeeDAO.getAllDepartments();
            }
        };

        loadDepartmentsTask.setOnSucceeded(e -> {
            List<String> departments = loadDepartmentsTask.getValue();
            Platform.runLater(() -> {
                departments.add(0, "전체");
                departmentComboBox.setItems(FXCollections.observableArrayList(departments));
                departmentComboBox.setValue("전체");
            });
        });

        Thread thread = new Thread(loadDepartmentsTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 모든 사원 비동기 DB조회 → 테이블에 반영
     */
    private void loadAllEmployees() {
        Task<List<EmployeeDTO>> loadTask = new Task<List<EmployeeDTO>>() {
            @Override
            protected List<EmployeeDTO> call() throws Exception {
                return employeeDAO.getAllEmployees();
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<EmployeeDTO> employees = loadTask.getValue();
            Platform.runLater(() -> {
                employeeData = FXCollections.observableArrayList(employees);
                employeeTable.setItems(employeeData);
                System.out.println("사원 목록 로드 완료: " + employees.size() + "명");
            });
        });

        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setHeaderText("데이터 로드 실패");
                alert.setContentText("사원 목록을 불러오는 중 오류가 발생했습니다.");
                alert.showAndWait();
            });
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 부서별로 사원 목록 필터링
     */
    @FXML
    private void handleDepartmentFilter() {
        String selectedDept = departmentComboBox.getValue();
        if (selectedDept == null) return;

        if ("전체".equals(selectedDept)) {
            loadAllEmployees();
        } else {
            loadEmployeesByDepartment(selectedDept);
        }
    }

    /**
     * 특정 부서 사원만 DB에서 불러오기(비동기)
     */
    private void loadEmployeesByDepartment(String departmentName) {
        Task<List<EmployeeDTO>> loadTask = new Task<List<EmployeeDTO>>() {
            @Override
            protected List<EmployeeDTO> call() throws Exception {
                return employeeDAO.getEmployeesByDepartment(departmentName);
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<EmployeeDTO> employees = loadTask.getValue();
            Platform.runLater(() -> {
                employeeData = FXCollections.observableArrayList(employees);
                employeeTable.setItems(employeeData);
                System.out.println(departmentName + " 부서 사원 로드: " + employees.size() + "명");
            });
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 텍스트 입력 단어로 전체 사원 정보 검색
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            // 검색어가 비어있으면 전체 또는 선택된 부서 기준으로 리로드
            String selectedDept = departmentComboBox.getValue();
            if ("전체".equals(selectedDept)) {
                loadAllEmployees();
            } else {
                loadEmployeesByDepartment(selectedDept);
            }
            return;
        }

        Task<List<EmployeeDTO>> searchTask = new Task<List<EmployeeDTO>>() {
            @Override
            protected List<EmployeeDTO> call() throws Exception {
                return employeeDAO.searchEmployees(keyword);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<EmployeeDTO> employees = searchTask.getValue();
            Platform.runLater(() -> {
                employeeData = FXCollections.observableArrayList(employees);
                employeeTable.setItems(employeeData);
                System.out.println("검색 결과: " + employees.size() + "명 (" + keyword + ")");
            });
        });

        Thread thread = new Thread(searchTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 새로고침 버튼 클릭시 전체목록 초기화 + 선택/검색상태 리셋
     */
    @FXML
    private void handleRefresh() {
        searchField.clear();
        departmentComboBox.setValue("전체");
        selectedEmployee = null;
        updateSelectedEmployeeDisplay();
        loadAllEmployees();
    }

    /**
     * 테이블에서 선택된 사원 표시 라벨/필드 갱신 (NFC UID도 해당)
     */
    private void updateSelectedEmployeeDisplay() {
        if (selectedEmployee != null) {
            selectedEmployeeLabel.setText(
                    selectedEmployee.getEmployeeId() + " - " + selectedEmployee.getEmployeeName()
            );
            selectedEmployeeLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

            // 기존 NFC UID가 있으면 표시
            String currentUid = selectedEmployee.getCardUid();
            if (currentUid != null && !currentUid.trim().isEmpty()) {
                nfcUidField.setText(currentUid);
            } else {
                nfcUidField.clear();
            }
        } else {
            selectedEmployeeLabel.setText("사원을 선택하세요");
            selectedEmployeeLabel.setStyle("-fx-text-fill: #7f8c8d;");
            nfcUidField.clear();
        }
    }

    /**
     * 사원 등록 버튼 클릭 시 실행되는 핸들러입니다.
     * 사원 등록 폼을 모달로 표시합니다.
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void handleAddEmployeeClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/EmployeeRegister.fxml"));
            Parent formRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("사원 등록");

            Window parentWindow = ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(parentWindow);

            Scene dialogScene = new Scene(formRoot);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
