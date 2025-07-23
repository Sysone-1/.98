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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ChartGroupController implements Initializable {

    // =============== 승인 관리 ===============
    @FXML private Text vacationCountText;
    @FXML private Text clockChangeCountText;
    @FXML private Text outworkCountText;
    @FXML private Text completedCountText;

    // =============== 근태 현황 ===============
    @FXML private Text txtTotal;
    @FXML private Text txtPresent;      // 출근 표시용 (기존 필드명 유지)
    @FXML private Text txtLate;         // 지각
    @FXML private Text txtBusiness;     // 결근 표시용 (기존 필드명 유지)
    @FXML private Text txtVacation;     // 휴가

    // =============== 차트/부서별 영역 ===============
    @FXML private PieChart overallChart;
    @FXML private PieChart departmentChart;
    @FXML private ComboBox<String> departmentComboBox;

    private RequestService requestService;
    private AttendanceStatsService statsService;

    // 자동 새로고침을 위한 스케줄러
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestService = new RequestService();
        statsService = new AttendanceStatsService();

        setupClickEvents();
        setupDepartmentFilter();

        // 초기 데이터 로드
        refreshAllData();

        // 자동 새로고침 설정 (5분마다)
        setupAutoRefresh();
    }

    /**
     * 자동 새로고침 설정 (개선된 기능)
     */
    private void setupAutoRefresh() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::refreshAllData);
        }, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 전체 데이터 새로고침 (개선된 버전)
     */
    public void refreshAllData() {
        // 백그라운드 스레드에서 데이터 조회
        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // UI 스레드에서 업데이트
                    Platform.runLater(() -> {
                        updateAllCounts();
                        loadHomeStats();
                        handleDepartmentChange(); // 현재 선택된 부서 기준으로 차트 업데이트
                        System.out.println("관리자 홈 데이터 새로고침 완료");
                    });
                } catch (Exception e) {
                    System.err.println("데이터 새로고침 오류: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(refreshTask);
        thread.setDaemon(true);
        thread.start();
    }

    // ========================= 부서 필터링 및 통계 (개선됨) =========================
    private void setupDepartmentFilter() {
        try {
            List<String> departments = statsService.getAllDepartments();
            departmentComboBox.setItems(FXCollections.observableArrayList(departments));
            departmentComboBox.setValue("전체");
            departmentComboBox.setOnAction(e -> handleDepartmentChange());
        } catch (Exception e) {
            System.err.println("부서 필터 설정 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDepartmentChange() {
        String dept = departmentComboBox.getValue();
        if (dept != null) {
            loadDepartmentStats(dept);
        }
    }

    public void loadDepartmentStats(String departmentName) {
        try {
            int[] stats = "전체".equals(departmentName)
                    ? statsService.getTodayStats()
                    : statsService.getTodayStatsByDept(departmentName);

            // 차트 업데이트 (개선된 로직)
            updateDepartmentChart(stats, departmentName);
        } catch (Exception e) {
            System.err.println("부서별 통계 로드 실패: " + e.getMessage());
            e.printStackTrace();
            // 오류 시 기본값 설정
            updateDepartmentChart(new int[]{0, 0, 0, 0, 0}, departmentName);
        }
    }

    /**
     * 부서별 차트 업데이트 (색상 고정 로직 통합)
     */
    private void updateDepartmentChart(int[] stats, String deptName) {
        Platform.runLater(() -> {
            try {
                // 기존 차트 데이터 완전히 제거
                departmentChart.getData().clear();

                // 0이 아닌 데이터만 추가하여 차트가 보이도록 함
                ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

                if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1]));       // 출근 (기존 정시출근)
                if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2]));       // 지각
                if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3]));       // 결근 (기존 출장)
                if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4]));       // 휴가 (로직 유지)

                // 데이터가 있을 때만 차트 설정
                if (!chartData.isEmpty()) {
                    departmentChart.setData(chartData);
                    String title = "전체".equals(deptName)
                            ? "전체 부서 (" + stats[0] + "명)"
                            : deptName + " 부서 (" + stats[0] + "명)";
                    departmentChart.setTitle(title);

                    overallChart.setLegendVisible(false);
                    departmentChart.setLegendVisible(false);

                    // 차트 visibility 확실히 설정
                    departmentChart.setVisible(true);
                    departmentChart.setManaged(true);

                    // 고정 색상 적용 (핵심!)
                    applyFixedStatusColors(departmentChart);
                } else {
                    // 데이터가 없을 때는 빈 차트 표시
                    departmentChart.setTitle(deptName + " 부서 (출근 기록 없음)");
                    chartData.add(new PieChart.Data(" ", 1));
                    departmentChart.setData(chartData);
                    // 기록 없음 항목에는 회색 적용
                    Platform.runLater(() -> {
                        for (PieChart.Data data : departmentChart.getData()) {
                            if (data.getNode() != null) {
                                data.getNode().setStyle("-fx-pie-color: #CCCCCC;");
                            }
                        }
                    });
                }

                System.out.println("부서별 차트 업데이트 완료: " + deptName +
                        " [총원:" + stats[0] + ", 출근:" + stats[1] + ", 지각:" + stats[2] +
                        ", 결근:" + stats[3] + ", 휴가:" + stats[4] + "]");

            } catch (Exception e) {
                System.err.println("부서별 차트 업데이트 오류: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ========================= 메인/전체 통계 (개선됨) =========================
    private void loadHomeStats() {
        try {
            int[] stats = statsService.getTodayStats();
            Platform.runLater(() -> {
                // 텍스트 업데이트 (라벨 변경됨)
                if (txtTotal != null) txtTotal.setText("총원: " + stats[0] + "명");
                if (txtPresent != null) txtPresent.setText("출근: " + stats[1] + "명");      // 기존 정시출근 → 출근
                if (txtLate != null) txtLate.setText("지각: " + stats[2] + "명");
                if (txtBusiness != null) txtBusiness.setText("결근: " + stats[3] + "명");    // 기존 출장 → 결근
                if (txtVacation != null) txtVacation.setText("휴가: " + stats[4] + "명");    // 휴가 유지

                // 전체 차트 업데이트
                updateOverallChart(stats);
            });

            System.out.println("근태 현황 로드 완료: " +
                    "[총원:" + stats[0] + ", 출근:" + stats[1] + ", 지각:" + stats[2] +
                    ", 결근:" + stats[3] + ", 휴가:" + stats[4] + "]");
        } catch (Exception e) {
            System.err.println("근태 현황 로드 실패: " + e.getMessage());
            e.printStackTrace();

            // 오류 시 기본값 설정
            Platform.runLater(() -> {
                if (txtTotal != null) txtTotal.setText("총원: 0명");
                if (txtPresent != null) txtPresent.setText("출근: 0명");
                if (txtLate != null) txtLate.setText("지각: 0명");
                if (txtBusiness != null) txtBusiness.setText("결근: 0명");
                if (txtVacation != null) txtVacation.setText("휴가: 0명");
            });
        }
    }

    /**
     * 전체 차트 업데이트 (색상 고정 로직 통합)
     */
    private void updateOverallChart(int[] stats) {
        Platform.runLater(() -> {
            try {
                // 기존 데이터 제거
                overallChart.getData().clear();

                // 0이 아닌 데이터만 추가
                ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

                if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1]));       // 출근
                if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2]));       // 지각
                if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3]));       // 결근
                if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4]));       // 휴가

                if (!chartData.isEmpty()) {
                    overallChart.setData(chartData);
                    overallChart.setTitle("전체 근무 현황 (" + stats[0] + "명)");
                    overallChart.setVisible(true);
                    overallChart.setManaged(true);

                    // 고정 색상 적용 (핵심!)
                    applyFixedStatusColors(overallChart);
                } else {
                    // 데이터가 없을 때 처리
                    overallChart.setTitle("전체 근무 현황 (출근 기록 없음)");
                    chartData.add(new PieChart.Data(" ", 1));
                    overallChart.setData(chartData);
                    // 기록 없음 항목에는 회색 적용
                    Platform.runLater(() -> {
                        for (PieChart.Data data : overallChart.getData()) {
                            if (data.getNode() != null) {
                                data.getNode().setStyle("-fx-pie-color:");
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("전체 차트 업데이트 오류: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 부서별 필터링과 관계없이 상태별로 고정 색상을 적용하는 메서드 (핵심!)
     * @param chart 색상을 적용할 PieChart 객체
     */
    private void applyFixedStatusColors(PieChart chart) {
        Platform.runLater(() -> {
            try {
                for (PieChart.Data data : chart.getData()) {
                    String statusColor;

                    // 상태별 고정 색상 매핑
                    switch (data.getName()) {
                        case "출근":
                            statusColor = "#4CAF50";  // 초록색
                            break;
                        case "지각":
                            statusColor = "#FF9800";  // 주황색
                            break;
                        case "결근":
                            statusColor = "#F44336";  // 빨간색
                            break;
                        case "휴가":
                            statusColor = "#9C27B0";  // 보라색
                            break;
                        default:
                            statusColor = "#CCCCCC";  // 기본 회색
                            break;
                    }

                    // 파이 슬라이스에 색상 직접 적용
                    Node pieSlice = data.getNode();
                    if (pieSlice != null) {
                        pieSlice.setStyle("-fx-pie-color: " + statusColor + ";");
                    }

                    // 범례 아이콘 색상도 동일하게 적용
                    Node legendSymbol = chart.lookup(".chart-legend-item[data=\"" + data.getName() +
                            "\"] .chart-legend-item-symbol");
                    if (legendSymbol != null) {
                        legendSymbol.setStyle("-fx-background-color: " + statusColor + ";");
                    }
                }
            } catch (Exception e) {
                System.err.println("고정 색상 적용 오류: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ========================= 기존 승인관리 기능 (개선됨) =========================
    private void updateAllCounts() {
        try {
            Platform.runLater(() -> {
                try {
                    if (vacationCountText != null) {
                        int vacationCount = requestService.getPendingCount(RequestType.VACATION);
                        vacationCountText.setText(String.valueOf(vacationCount));
                    }
                    if (clockChangeCountText != null) {
                        int clockCount = requestService.getPendingCount(RequestType.CLOCK_CHANGE);
                        clockChangeCountText.setText(String.valueOf(clockCount));
                    }
                    if (outworkCountText != null) {
                        int outworkCount = requestService.getPendingCount(RequestType.OUTWORK);
                        outworkCountText.setText(String.valueOf(outworkCount));
                    }
                    if (completedCountText != null) {
                        int completedCount = requestService.getAllCompletedCount();
                        completedCountText.setText(String.valueOf(completedCount));
                    }
                    System.out.println("승인 관리 카운트 업데이트 완료");
                } catch (Exception e) {
                    System.err.println("UI 업데이트 오류: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("승인 관리 카운트 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupClickEvents() {
        bindIfNotNull(vacationCountText, RequestType.VACATION);
        bindIfNotNull(clockChangeCountText, RequestType.CLOCK_CHANGE);
        bindIfNotNull(outworkCountText, RequestType.OUTWORK);

        if (completedCountText != null) {
            completedCountText.setOnMouseClicked(e -> openCompletedRequestList());
            completedCountText.setOnMouseEntered(
                    e -> completedCountText.setStyle("-fx-fill: blue; -fx-cursor: hand;"));
            completedCountText.setOnMouseExited(
                    e -> completedCountText.setStyle("-fx-fill: black;"));
        }
    }

    private void bindIfNotNull(Text txt, RequestType type) {
        if (txt != null) {
            txt.setOnMouseClicked(e -> openRequestList(type));
            txt.setOnMouseEntered(e -> txt.setStyle("-fx-fill:blue; -fx-cursor:hand;"));
            txt.setOnMouseExited(e -> txt.setStyle("-fx-fill:black;"));
        }
    }

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

            // 창 닫힐 때 데이터 새로고침 (핵심 개선!)
            dialog.setOnCloseRequest((WindowEvent e) -> {
                System.out.println("요청 목록 창 닫힘 - 데이터 새로고침 시작");
                refreshAllData();
            });

            dialog.show();
        } catch (IOException ex) {
            System.err.println("요청 목록 창 열기 오류: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

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

            // 창 닫힐 때 데이터 새로고침 (핵심 개선!)
            dialog.setOnCloseRequest((WindowEvent e) -> {
                System.out.println("결재 완료 창 닫힘 - 데이터 새로고침 시작");
                refreshAllData();
            });

            dialog.show();
            System.out.println("결재 완료 창 열기 성공");
        } catch (IOException ex) {
            System.err.println("결재 완료 창 열기 중 오류: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * 컨트롤러 종료 시 리소스 정리 (추가됨)
     */
    public void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}