package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.enums.RequestType;
import com.sysone.ogamza.service.admin.AttendanceStatsService;
import com.sysone.ogamza.service.admin.RequestService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ChartGroupController.java
 *
 * 관리자 화면에서 승인 요청, 근태 통계 및 부서별 차트 등 전반적인 데이터를
 * 처리하고 시각화하는 JavaFX 컨트롤러입니다.
 * 자동 새로고침 및 이벤트 처리도 포함됩니다.
 *
 * @author 조윤상
 * @since 2025-07-23
 */
public class ChartGroupController implements Initializable {

    // =============== 승인 관리 ===============
    @FXML private Text vacationCountText;
    @FXML private Text clockChangeCountText;
    @FXML private Text outworkCountText;
    @FXML private Text completedCountText;

    // =============== 근태 현황 ===============
    @FXML private Text txtTotal;
    @FXML private Text txtPresent;
    @FXML private Text txtLate;
    @FXML private Text txtBusiness;
    @FXML private Text txtVacation;

    // =============== 차트/부서별 영역 ===============
    @FXML private PieChart overallChart;
    @FXML private PieChart departmentChart;
    @FXML private ComboBox<String> departmentComboBox;

    private RequestService requestService;
    private AttendanceStatsService statsService;

    private ScheduledExecutorService scheduler;

    /**
     * 초기화 메서드. 서비스 초기화 및 UI 이벤트와 자동 새로고침 설정.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestService = new RequestService();
        statsService = new AttendanceStatsService();
        setupClickEvents();
        setupDepartmentFilter();
        refreshAllData();
        setupAutoRefresh();
    }

    /**
     * 자동 새로고침 스케줄러를 설정합니다. (5분 주기)
     */
    private void setupAutoRefresh() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::refreshAllData), 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 전체 데이터(카운트, 차트 등)를 새로고침합니다.
     */
    public void refreshAllData() {
        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    updateAllCounts();
                    loadHomeStats();
                    handleDepartmentChange();
                    System.out.println("관리자 홈 데이터 새로고침 완료");
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
    }

    /**
     * 부서 콤보박스를 초기화하고 선택 이벤트를 바인딩합니다.
     */
    private void setupDepartmentFilter() {
        try {
            List<String> departments = statsService.getAllDepartments();
            departmentComboBox.setItems(FXCollections.observableArrayList(departments));
            departmentComboBox.setValue("전체");
            departmentComboBox.setOnAction(e -> handleDepartmentChange());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 부서 선택 변경 시 통계 데이터를 로드합니다.
     */
    @FXML
    private void handleDepartmentChange() {
        String dept = departmentComboBox.getValue();
        if (dept != null) {
            loadDepartmentStats(dept);
        }
    }

    /**
     * 지정된 부서의 통계를 조회하고 차트를 업데이트합니다.
     *
     * @param departmentName 부서 이름
     */
    public void loadDepartmentStats(String departmentName) {
        try {
            int[] stats = "전체".equals(departmentName)
                    ? statsService.getTodayStats()
                    : statsService.getTodayStatsByDept(departmentName);
            updateDepartmentChart(stats, departmentName);
        } catch (Exception e) {
            updateDepartmentChart(new int[]{0, 0, 0, 0, 0}, departmentName);
        }
    }

    /**
     * 부서별 파이 차트를 업데이트합니다.
     */
    private void updateDepartmentChart(int[] stats, String deptName) {
        Platform.runLater(() -> {
            departmentChart.getData().clear();
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

            if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1]));
            if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2]));
            if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3]));
            if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4]));

            if (!chartData.isEmpty()) {
                departmentChart.setData(chartData);
                departmentChart.setTitle(("전체".equals(deptName) ? "전체 부서" : deptName + " 부서") + " (" + stats[0] + "명)");
                departmentChart.setLegendVisible(false);
                departmentChart.setVisible(true);
                departmentChart.setManaged(true);
                applyFixedStatusColors(departmentChart);
            } else {
                departmentChart.setTitle(deptName + " 부서 (출근 기록 없음)");
                chartData.add(new PieChart.Data(" ", 1));
                departmentChart.setData(chartData);
                for (PieChart.Data data : departmentChart.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-pie-color: #CCCCCC;");
                    }
                }
            }
        });
    }

    /**
     * 전체 근태 통계를 로드하고 UI를 업데이트합니다.
     */
    private void loadHomeStats() {
        try {
            int[] stats = statsService.getTodayStats();
            Platform.runLater(() -> {
                txtTotal.setText("총원: " + stats[0] + "명");
                txtPresent.setText("출근: " + stats[1] + "명");
                txtLate.setText("지각: " + stats[2] + "명");
                txtBusiness.setText("결근: " + stats[3] + "명");
                txtVacation.setText("휴가: " + stats[4] + "명");
                updateOverallChart(stats);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                txtTotal.setText("총원: 0명");
                txtPresent.setText("출근: 0명");
                txtLate.setText("지각: 0명");
                txtBusiness.setText("결근: 0명");
                txtVacation.setText("휴가: 0명");
            });
        }
    }

    /**
     * 전체 파이 차트를 업데이트합니다.
     */
    private void updateOverallChart(int[] stats) {
        Platform.runLater(() -> {
            overallChart.getData().clear();
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

            if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1]));
            if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2]));
            if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3]));
            if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4]));

            if (!chartData.isEmpty()) {
                overallChart.setData(chartData);
                overallChart.setTitle("전체 근무 현황 (" + stats[0] + "명)");
                overallChart.setVisible(true);
                overallChart.setManaged(true);
                applyFixedStatusColors(overallChart);
            }
        });
    }

    /**
     * 상태별로 고정 색상을 적용합니다.
     */
    private void applyFixedStatusColors(PieChart chart) {
        Platform.runLater(() -> {
            for (PieChart.Data data : chart.getData()) {
                String statusColor;
                switch (data.getName()) {
                    case "출근" -> statusColor = "#4CAF50";
                    case "지각" -> statusColor = "#FF9800";
                    case "결근" -> statusColor = "#F44336";
                    case "휴가" -> statusColor = "#9C27B0";
                    default -> statusColor = "#CCCCCC";
                }
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + statusColor + ";");
                }
            }
        });
    }

    /**
     * 승인 요청 건수 및 완료 내역을 UI에 표시합니다.
     */
    private void updateAllCounts() {
        Platform.runLater(() -> {
            vacationCountText.setText(String.valueOf(requestService.getPendingCount(RequestType.VACATION)));
            clockChangeCountText.setText(String.valueOf(requestService.getPendingCount(RequestType.CLOCK_CHANGE)));
            outworkCountText.setText(String.valueOf(requestService.getPendingCount(RequestType.OUTWORK)));
            completedCountText.setText(String.valueOf(requestService.getAllCompletedCount()));
        });
    }

    /**
     * 각 승인 카운트 항목에 클릭 이벤트를 바인딩합니다.
     */
    private void setupClickEvents() {
        bindIfNotNull(vacationCountText, RequestType.VACATION);
        bindIfNotNull(clockChangeCountText, RequestType.CLOCK_CHANGE);
        bindIfNotNull(outworkCountText, RequestType.OUTWORK);

        completedCountText.setOnMouseClicked(e -> openCompletedRequestList());
        completedCountText.setOnMouseEntered(e -> completedCountText.setStyle("-fx-fill: blue; -fx-cursor: hand;"));
        completedCountText.setOnMouseExited(e -> completedCountText.setStyle("-fx-fill: black;"));
    }

    /**
     * 텍스트 요소에 클릭 및 hover 이벤트를 바인딩합니다.
     */
    private void bindIfNotNull(Text txt, RequestType type) {
        if (txt != null) {
            txt.setOnMouseClicked(e -> openRequestList(type));
            txt.setOnMouseEntered(e -> txt.setStyle("-fx-fill:blue; -fx-cursor:hand;"));
            txt.setOnMouseExited(e -> txt.setStyle("-fx-fill:black;"));
        }
    }

    /**
     * 요청 리스트 창을 열고, 닫힐 때 데이터를 새로고침합니다.
     */
    private void openRequestList(RequestType requestType) {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/admin/RequestList.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            RequestListController controller = loader.getController();
            controller.setRequestType(requestType);

            Stage dialog = new Stage();
            dialog.setTitle(requestType.getDisplayName());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(vacationCountText.getScene().getWindow());
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.setOnCloseRequest(e -> refreshAllData());
            dialog.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 결재 완료 창을 열고, 닫힐 때 데이터를 새로고침합니다.
     */
    private void openCompletedRequestList() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/admin/CompleteRequestList.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            CompletedRequestListController ctrl = loader.getController();
            ctrl.setRequestService(requestService);

            Stage dialog = new Stage();
            dialog.setTitle("결재 완료 내역");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(vacationCountText.getScene().getWindow());
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.setOnCloseRequest(e -> refreshAllData());
            dialog.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
