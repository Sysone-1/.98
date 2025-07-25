package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.dto.admin.AttendanceStatusDTO;
import com.sysone.ogamza.dto.admin.OvertimeData;
import com.sysone.ogamza.service.admin.AdminDashboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
 * 관리자 대시보드 화면의 컨트롤러 클래스입니다.
 * 출결 상태 테이블, 부서별 주간 초과근무 그래프, 출입 거부 차트 등을 초기화하고 데이터를 로드하여 표시합니다.
 *
 * @author 조윤상
 * @since 2025-07-25
 */
public class AdminDashboardController implements Initializable {

    /** 차트 그룹을 포함하는 HBox */
    @FXML private HBox chartGroup;

    /** 차트 그룹의 컨트롤러 */
    @FXML private ChartGroupController chartGroupController;

    /** 근무 현황 테이블 */
    @FXML private TableView<Map<String, Object>> statusTable;

    /** 근무 현황 테이블의 출근율 컬럼 */
    @FXML private TableColumn<Map<String, Object>, String> colAttendanceRate;

    /** 근무 현황 테이블의 각 인원 관련 컬럼 */
    @FXML private TableColumn<Map<String, Object>, Integer> colTotal, colPresent, colLate, colAbsent, colVacation, colOut, colEtc;

    /** 주간 초과근무 그래프를 표시하는 VBox */
    @FXML private VBox overtimeGraphBox;

    /** 부서 선택 콤보박스 */
    @FXML private ComboBox<String> departmentComboBox;

    /** 초과근무 범례를 표시하는 HBox */
    @FXML private HBox overtimeLegendBox;

    /** 출입 거부 횟수 차트 */
    @FXML private LineChart<String, Number> deniedAccessChart;

    /** 출입 거부 차트의 X축 */
    @FXML private CategoryAxis deniedAccessXAxis;

    /** 출입 거부 차트의 Y축 */
    @FXML private NumberAxis deniedAccessYAxis;

    /** 대시보드 관련 데이터를 처리하는 서비스 */
    private final AdminDashboardService adminDashboardService = new AdminDashboardService();

    /** 부서별 주간 초과근무 데이터 맵 */
    private Map<String, List<OvertimeData>> departmentOvertimeMap = new HashMap<>();

    /**
     * FXML 초기화 메서드입니다. 각 UI 요소에 데이터 바인딩 및 리스너를 등록하고 데이터를 로드합니다.
     *
     * @param location  FXML 파일의 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (chartGroupController != null) {
            chartGroupController.loadCharts();
        }

        setupTableColumnFactories();
        statusTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadAndDisplayAllDashboardData();
        setupOvertimeLegend();

        departmentComboBox.setOnAction(e -> {
            String selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null) {
                updateOvertimeGraph(selectedDepartment);
            }
        });
    }

    /**
     * 초과근무 범례 색상과 설명을 overtimeLegendBox에 표시합니다.
     */
    private void setupOvertimeLegend() {
        overtimeLegendBox.getChildren().clear();
        Map<Color, String> legendItems = new LinkedHashMap<>();
        legendItems.put(Color.LIMEGREEN, "0-4시간");
        legendItems.put(Color.GOLD, "4-8시간");
        legendItems.put(Color.ORANGE, "8-12시간");
        legendItems.put(Color.RED, "12시간 이상");

        for (Map.Entry<Color, String> entry : legendItems.entrySet()) {
            HBox legendItem = new HBox(5);
            legendItem.setAlignment(Pos.CENTER_LEFT);
            Rectangle colorRect = new Rectangle(15, 15, entry.getKey());
            colorRect.setArcWidth(3);
            colorRect.setArcHeight(3);
            Label label = new Label(entry.getValue());
            legendItem.getChildren().addAll(colorRect, label);
            overtimeLegendBox.getChildren().add(legendItem);
        }
    }

    /**
     * 전체 대시보드 데이터를 서비스로부터 받아와 UI에 반영합니다.
     */
    private void loadAndDisplayAllDashboardData() {
        AttendanceStatusDTO statusData = adminDashboardService.getAttendanceStatusData();
        updateStatusTable(statusData);

        this.departmentOvertimeMap = adminDashboardService.getDepartmentOvertimeData();
        updateDepartmentComboBox();

        Map<String, Integer> deniedAccessData = adminDashboardService.getDeniedAccessLogWeekly();
        updateDeniedAccessChart(deniedAccessData);
    }

    /**
     * 출입 거부 차트를 주간 데이터로 업데이트합니다.
     *
     * @param deniedAccessData 주차별 출입 거부 횟수 데이터
     */
    private void updateDeniedAccessChart(Map<String, Integer> deniedAccessData) {
        deniedAccessChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("출입 거부 횟수");

        for (Map.Entry<String, Integer> entry : deniedAccessData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        deniedAccessChart.getData().add(series);
        deniedAccessYAxis.setLabel("거부 횟수");
        deniedAccessXAxis.setLabel("주차");
    }

    /**
     * 근무 현황 테이블의 컬럼에 대한 CellValueFactory를 설정합니다.
     */
    private void setupTableColumnFactories() {
        colAttendanceRate.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("출근율")));
        colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("총원")));
        colPresent.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("출근")));
        colLate.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("지각")));
        colAbsent.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("결근")));
        colVacation.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("휴가")));
        colOut.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("외근")));
        colEtc.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().get("기타")));
    }

    /**
     * 근무 현황 테이블에 데이터를 설정합니다.
     *
     * @param data 출결 상태 DTO
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
     * 부서 콤보박스를 초기화하고 첫 번째 부서의 데이터를 초과근무 그래프에 표시합니다.
     */
    private void updateDepartmentComboBox() {
        departmentComboBox.getItems().clear();
        if (departmentOvertimeMap != null && !departmentOvertimeMap.isEmpty()) {
            departmentComboBox.getItems().addAll(departmentOvertimeMap.keySet());
            departmentComboBox.getSelectionModel().selectFirst();
            updateOvertimeGraph(departmentComboBox.getValue());
        }
    }

    /**
     * 선택된 부서의 초과근무 데이터를 그래프에 표시합니다.
     *
     * @param department 부서명
     */
    private void updateOvertimeGraph(String department) {
        overtimeGraphBox.getChildren().clear();
        if (department == null) return;

        List<OvertimeData> dataList = departmentOvertimeMap.getOrDefault(department, Collections.emptyList());

        for (OvertimeData data : dataList) {
            HBox row = new HBox();
            row.setSpacing(0);
            row.setPrefHeight(30);
            row.setMaxWidth(600);
            row.setStyle("-fx-border-color: #ccc; -fx-border-width: 1 0 1 0;");

            Label weekLabel = new Label(data.getWeekLabel());
            weekLabel.setPrefWidth(200);
            weekLabel.setStyle("-fx-padding: 5; -fx-font-weight: bold;");

            HBox bar = createStackedBar(data);
            bar.setPrefWidth(400);

            row.getChildren().addAll(weekLabel, bar);
            overtimeGraphBox.getChildren().add(row);
        }
    }

    /**
     * OvertimeData 객체로부터 누적형 막대 그래프(HBox)를 생성합니다.
     *
     * @param data 초과근무 데이터
     * @return 누적형 막대 HBox
     */
    private HBox createStackedBar(OvertimeData data) {
        HBox bar = new HBox();
        bar.setSpacing(0);
        bar.getChildren().addAll(
                createSegment(Color.LIMEGREEN, data.getRange0to4()),
                createSegment(Color.GOLD, data.getRange4to8()),
                createSegment(Color.ORANGE, data.getRange8to12()),
                createSegment(Color.RED, data.getRange12plus())
        );
        return bar;
    }

    /**
     * 특정 구간의 색상과 비율로 그래프의 Segment를 생성합니다.
     *
     * @param color 색상
     * @param percentage 해당 구간의 비율 (0~100)
     * @return StackPane 형태의 그래프 세그먼트
     */
    private StackPane createSegment(Color color, int percentage) {
        double width = 400.0 * (percentage / 100.0);
        Rectangle rect = new Rectangle(width, 28);
        rect.setFill(color);

        Label percentageLabel = new Label(percentage + "%");
        percentageLabel.setTextFill(Color.BLACK);
        percentageLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, percentageLabel);
        StackPane.setAlignment(percentageLabel, Pos.CENTER);

        if (percentage == 0) {
            percentageLabel.setVisible(false);
        }

        return stack;
    }
}
