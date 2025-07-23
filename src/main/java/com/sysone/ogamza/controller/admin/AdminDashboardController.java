package com.sysone.ogamza.controller.admin;


import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;
import com.sysone.ogamza.service.admin.AdminDashboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

/**
 * 관리자 대시보드 화면의 UI 로직을 처리하는 컨트롤러 클래스입니다.
 * Service 계층과 연동하여 데이터베이스의 실시간 데이터를 UI에 표시합니다.
 */
public class AdminDashboardController implements Initializable {

    @FXML
    private TableView<Map<String, Object>> statusTable;
    @FXML
    private TableColumn<Map<String, Object>, String> colAttendanceRate;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colTotal, colPresent, colLate, colAbsent, colVacation, colOut, colEtc;
    @FXML
    private VBox overtimeGraphBox;
    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private HBox overtimeLegendBox;

    // Service 계층 의존성
    private final AdminDashboardService adminDashboardService = new AdminDashboardService();

    // 부서별 초과 근무 데이터 저장 맵
    private Map<String, List<OvertimeData>> departmentOvertimeMap = new HashMap<>();

    /**
     * 컨트롤러 초기화 메소드. FXML 로딩 후 JavaFX 애플리케이션 스레드에 의해 자동으로 호출됩니다.
     * UI 컴포넌트의 초기 설정 및 데이터 로딩을 수행합니다.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. TableView의 각 컬럼과 데이터(Map의 Key)를 매핑합니다.
        setupTableColumnFactories();
        statusTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 2. Service를 통해 DB에서 모든 데이터를 로드하고 UI에 표시합니다.
        loadAndDisplayAllDashboardData();

        setupOvertimeLegend();

        // 3. 부서 선택 콤보박스에 대한 이벤트 리스너를 설정합니다.
        departmentComboBox.setOnAction(e -> {
            String selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null) {
                updateOvertimeGraph(selectedDepartment);
            }
        });
    }


    /**
     * 초과근무 그래프의 범례를 설정합니다.
     */
    private void setupOvertimeLegend() {
        overtimeLegendBox.getChildren().clear();

        // 각 초과근무 범위와 색상, 레이블 정의
        Map<Color, String> legendItems = new LinkedHashMap<>(); // 순서 유지를 위해 LinkedHashMap 사용
        legendItems.put(Color.LIMEGREEN, "0-4시간");
        legendItems.put(Color.GOLD, "4-8시간");
        legendItems.put(Color.ORANGE, "8-12시간");
        legendItems.put(Color.RED, "12시간 이상");

        for (Map.Entry<Color, String> entry : legendItems.entrySet()) {
            Color color = entry.getKey();
            String labelText = entry.getValue();

            HBox legendItem = new HBox(5); // 색상 사각형과 레이블 사이 간격
            legendItem.setAlignment(Pos.CENTER_LEFT); // 정렬

            Rectangle colorRect = new Rectangle(15, 15, color); // 색상 사각형
            colorRect.setArcWidth(3);
            colorRect.setArcHeight(3);

            Label label = new Label(labelText); // 레이블 텍스트

            legendItem.getChildren().addAll(colorRect, label);
            overtimeLegendBox.getChildren().add(legendItem);
        }
    }


    /**
     * Service를 통해 모든 대시보드 데이터를 가져와 UI 컴포넌트를 업데이트합니다.
     */
    private void loadAndDisplayAllDashboardData() {
        // 출근 현황 데이터 로드 및 표시
        AttendanceStatusDTO statusData = adminDashboardService.getAttendanceStatusData();
        updateStatusTable(statusData);

        // 부서별 초과근무 데이터 로드 및 표시
        this.departmentOvertimeMap = adminDashboardService.getDepartmentOvertimeData();
        updateDepartmentComboBox();
    }

    /**
     * TableView의 각 컬럼에 CellValueFactory를 설정하여
     * Map 데이터의 어떤 Key 값을 보여줄지 지정합니다. 이 작업은 UI와 데이터를 바인딩하는 데 필수적입니다.
     */
    private void setupTableColumnFactories() {
        // String 타입 컬럼 설정
        colAttendanceRate.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("출근율"))
        );

        // Integer 타입 컬럼 설정
        colTotal.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("총원"))
        );
        colPresent.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("출근"))
        );
        colLate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("지각"))
        );
        colAbsent.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("결근"))
        );
        colVacation.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("휴가"))
        );
        colOut.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("외근"))
        );
        colEtc.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("기타"))
        );
    }

    /**
     * 전달받은 DTO를 기반으로 현황 테이블(TableView)을 업데이트합니다.
     *
     * @param data DB에서 조회된 출근 현황 데이터 DTO
     */
    private void updateStatusTable(AttendanceStatusDTO data) {
        Map<String, Object> row = new HashMap<>();
        row.put("출근율", data.getAttendanceRate());
        row.put("총원", data.getTotalEmployees());
        row.put("출근", data.getPresentCount());
        row.put("지각", data.getLateCount());
        row.put("결근", data.getAbsentCount());
        row.put("휴가", data.getVacationCount());
        row.put("외근", data.getTripCount());
        row.put("기타", data.getEtcCount());

        ObservableList<Map<String, Object>> tableItems = FXCollections.observableArrayList(row);
        statusTable.setItems(tableItems);
    }




    /**
     * 부서 콤보박스를 DB에서 가져온 부서 목록으로 채우고 첫 항목을 선택합니다.
     */
    private void updateDepartmentComboBox() {
        departmentComboBox.getItems().clear();
        if (departmentOvertimeMap != null && !departmentOvertimeMap.isEmpty()) {
            departmentComboBox.getItems().addAll(departmentOvertimeMap.keySet());
            departmentComboBox.getSelectionModel().selectFirst();
            // 첫 항목 그래프를 자동으로 그림
            updateOvertimeGraph(departmentComboBox.getValue());
        }
    }

    /**
     * 선택된 부서의 주간 초과 근무 현황 그래프를 다시 그립니다.
     *
     * @param department 선택된 부서명
     */
    private void updateOvertimeGraph(String department) {
        overtimeGraphBox.getChildren().clear();
        if (department == null) return;

        List<OvertimeData> dataList = departmentOvertimeMap.getOrDefault(department,
                Collections.emptyList());

        for (OvertimeData data : dataList) {
            HBox row = new HBox();
            row.setSpacing(0);
            row.setPrefHeight(30);
            row.setMaxWidth(600);
            row.setStyle("-fx-border-color: #ccc; -fx-border-width: 1 0 1 0;");

            Label weekLabel = new Label(data.getWeekLabel()); // DTO의 getter 사용
            weekLabel.setPrefWidth(200);
            weekLabel.setStyle("-fx-padding: 5; -fx-font-weight: bold;");

            HBox bar = createStackedBar(data);
            bar.setPrefWidth(400);

            row.getChildren().addAll(weekLabel, bar);
            overtimeGraphBox.getChildren().add(row);
        }
    }

    /**
     * OvertimeData를 기반으로 색상별 누적 막대그래프 HBox를 생성합니다.
     */
    private HBox createStackedBar(OvertimeData data) {
        HBox bar = new HBox();
        bar.setSpacing(0); // 막대 사이에 간격 없음
        bar.getChildren().addAll(
                createSegment(Color.LIMEGREEN, data.getRange0to4()),
                createSegment(Color.GOLD, data.getRange4to8()),
                createSegment(Color.ORANGE, data.getRange8to12()),
                createSegment(Color.RED, data.getRange12plus())
        );
        return bar;
    }

    /**
     * 그래프의 각 색상 세그먼트(Rectangle)를 생성하고 그 위에 퍼센트 텍스트를 표시합니다.
     * @param color       막대 색상
     * @param percentage 백분율 값 (0~100)
     * @return 스타일이 적용된 StackPane 객체 (Rectangle과 Label 포함)
     */
    private StackPane createSegment(Color color, int percentage) {
        // 전체 막대 너비(400px)에 대한 비율로 너비 계산
        double width = 400.0 * (percentage / 100.0);

        // 1. 색상 막대 (Rectangle) 생성
        Rectangle rect = new Rectangle(width, 28); // 높이를 조금 줄여 테두리 안에 들어오게 함
        rect.setFill(color);

        // 2. 퍼센트 텍스트 (Label) 생성
        Label percentageLabel = new Label(percentage + "%");
        percentageLabel.setTextFill(Color.BLACK); // 텍스트 색상 (막대 색상에 따라 흰색/검은색 조절 가능)
        percentageLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;"); // 폰트 스타일

        // 3. Rectangle과 Label을 겹치기 위해 StackPane 사용
        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, percentageLabel);
        StackPane.setAlignment(percentageLabel, Pos.CENTER); // 텍스트를 막대 중앙에 정렬

        // 퍼센트가 0인 경우 텍스트를 숨김 (선택 사항)
        if (percentage == 0) {
            percentageLabel.setVisible(false);
        }

        return stack;
    }
}
