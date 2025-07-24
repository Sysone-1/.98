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
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

/**
 * 관리자 대시보드 컨트롤러 클래스.
 * 출근 현황, 초과근무 통계, 출입 거부 로그 등의 정보를 UI에 표시한다.
 *
 * @author 조윤상
 */
public class AdminDashboardController implements Initializable {

    @FXML private TableView<Map<String, Object>> statusTable;
    @FXML private TableColumn<Map<String, Object>, String> colAttendanceRate;
    @FXML private TableColumn<Map<String, Object>, Integer> colTotal, colPresent, colLate, colAbsent, colVacation, colOut, colEtc;
    @FXML private VBox overtimeGraphBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private HBox overtimeLegendBox;
    @FXML private LineChart<String, Number> deniedAccessChart;
    @FXML private CategoryAxis deniedAccessXAxis;
    @FXML private NumberAxis deniedAccessYAxis;

    private final AdminDashboardService adminDashboardService = new AdminDashboardService();
    private Map<String, List<OvertimeData>> departmentOvertimeMap = new HashMap<>();

    /**
     * FXML 초기화 메서드.
     *
     * @param location  FXML 파일의 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
     * 초과근무 범례를 설정한다.
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
     * 모든 대시보드 데이터를 로드하고 화면에 반영한다.
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
     * 출입 거부 로그 차트를 갱신한다.
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
     * 출근 현황 테이블의 컬럼 팩토리를 설정한다.
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
     * 출근 현황 테이블에 데이터를 반영한다.
     *
     * @param data 출근 현황 DTO
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
     * 부서 선택 콤보박스를 초기화하고 기본 선택 부서에 대해 그래프를 갱신한다.
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
     * 특정 부서의 주차별 초과근무 그래프를 갱신한다.
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
     * 초과근무 데이터를 바탕으로 누적 막대를 생성한다.
     *
     * @param data OvertimeData 객체
     * @return HBox 형태의 막대 그래프
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
     * 단일 구간에 해당하는 색상과 퍼센트에 맞는 세그먼트를 생성한다.
     *
     * @param color      색상
     * @param percentage 퍼센트 (0~100)
     * @return StackPane 형태의 세그먼트
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
