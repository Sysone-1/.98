package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.service.admin.AttendanceStatsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * 근무 현황 통계를 시각화하는 PieChart를 관리하는 컨트롤러 클래스입니다.<br>
 * 전체 및 부서별 근무 상태 데이터를 조회하여 파이 차트로 시각화합니다.
 * </p>
 *
 * @author 조윤상
 * @since 2025-07-24
 */
public class ChartGroupController implements Initializable {

    /** 전체 근무 현황을 보여주는 파이 차트 */
    @FXML private PieChart overallChart;

    /** 부서별 근무 현황을 보여주는 파이 차트 */
    @FXML private PieChart departmentChart;

    /** 부서 선택을 위한 콤보박스 */
    @FXML private ComboBox<String> departmentComboBox;

    /** 근무 통계 데이터를 제공하는 서비스 */
    private AttendanceStatsService statsService;

    /**
     * FXML 초기화 메서드. 서비스 객체를 생성하고 부서 콤보박스를 설정합니다.
     *
     * @param location  FXML 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statsService = new AttendanceStatsService();
        setupDepartmentFilter();
    }

    /**
     * 전체 및 부서별 차트를 초기 로드하는 메서드입니다.
     */
    public void loadCharts() {
        loadOverallChart();
        handleDepartmentChange(); // 콤보박스의 초기값 기준으로 부서별 차트도 로드
    }

    /**
     * 부서 선택 콤보박스를 초기화하고 "전체" 옵션을 포함합니다.
     */
    private void setupDepartmentFilter() {
        try {
            List<String> departments = statsService.getAllDepartments();
            departmentComboBox.setItems(FXCollections.observableArrayList(departments));
            departmentComboBox.getItems().add(0, "전체");
            departmentComboBox.setValue("전체");
        } catch (Exception e) {
            System.err.println("부서 필터 설정 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 부서 선택 콤보박스 값이 변경되었을 때 호출됩니다.
     * 해당 부서의 근무 현황 데이터를 로드합니다.
     */
    @FXML
    private void handleDepartmentChange() {
        String dept = departmentComboBox.getValue();
        if (dept != null) {
            loadDepartmentChart(dept);
        }
    }

    /**
     * 전체 근무 현황 데이터를 조회하여 전체 파이 차트를 갱신합니다.
     */
    private void loadOverallChart() {
        try {
            int[] stats = statsService.getTodayStats();
            updateChart(overallChart, stats, "전체 근무 현황");
        } catch (Exception e) {
            System.err.println("전체 근무 현황 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 특정 부서의 근무 현황 데이터를 조회하여 파이 차트를 갱신합니다.
     *
     * @param departmentName 부서명 ("전체" 포함 가능)
     */
    private void loadDepartmentChart(String departmentName) {
        try {
            int[] stats = "전체".equals(departmentName)
                    ? statsService.getTodayStats()
                    : statsService.getTodayStatsByDept(departmentName);
            updateChart(departmentChart, stats, departmentName + " 근무 현황");
        } catch (Exception e) {
            System.err.println("부서별 통계 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 주어진 통계 데이터를 기반으로 파이 차트를 업데이트합니다.
     * 출근, 지각, 결근, 휴가 등의 상태에 따라 섹터를 구성합니다.
     *
     * @param chart   갱신할 파이 차트
     * @param stats   통계 데이터 배열 [총원, 출근, 지각, 결근, 휴가]
     * @param title   차트 제목
     */
    private void updateChart(PieChart chart, int[] stats, String title) {
        Platform.runLater(() -> {
            chart.getData().clear();
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1]));
            if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2]));
            if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3]));
            if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4]));

            if (!chartData.isEmpty()) {
                chart.setData(chartData);
                chart.setTitle(title + " (" + stats[0] + "명)");
                chart.setLegendVisible(false);
                applyFixedStatusColors(chart);
            } else {
                chart.setTitle(title + " (기록 없음)");
                chartData.add(new PieChart.Data("기록 없음", 1));
                chart.setData(chartData);
                Platform.runLater(() -> {
                    if (!chart.getData().isEmpty() && chart.getData().get(0).getNode() != null) {
                        chart.getData().get(0).getNode().setStyle("-fx-pie-color: #CCCCCC;");
                    }
                });
            }
        });
    }

    /**
     * 차트 데이터의 항목 이름에 따라 고정 색상을 적용합니다.
     * 출근: 초록, 지각: 주황, 결근: 빨강, 휴가: 파랑
     *
     * @param chart 색상을 적용할 파이 차트
     */
    private void applyFixedStatusColors(PieChart chart) {
        Platform.runLater(() -> {
            for (PieChart.Data data : chart.getData()) {
                String statusColor;
                switch (data.getName()) {
                    case "출근": statusColor = "#4CAF50"; break;
                    case "지각": statusColor = "#FF9800"; break;
                    case "결근": statusColor = "#F44336"; break;
                    case "휴가": statusColor = "#2196f3"; break;
                    default: statusColor = "#CCCCCC"; break;
                }
                Node pieSlice = data.getNode();
                if (pieSlice != null) {
                    pieSlice.setStyle("-fx-pie-color: " + statusColor + ";");
                }
            }
        });
    }
}
