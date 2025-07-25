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
import javafx.geometry.Side;
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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * AdminHomeController (DB 실제값 표시 및 카운팅 동기화 개선 버전)
 * - DB 실제 SCHEDULE_TYPE 값 표시 지원
 * - 승인/거절 시 카운팅 실시간 동기화 개선
 * - 근무자별 일일 출근내역 및 파이차트 로직은 기존 유지
 * - 부서별 필터링 시각화 로직 기존 유지
 */
public class AdminHomeController implements Initializable {

    // =============== 승인 관리 ===============
    @FXML private Text vacationCountText;
    @FXML private Text clockChangeCountText;
    @FXML private Text outworkCountText;
    @FXML private Text completedCountText;

    // =============== 근태 현황 (기존 로직 유지) ===============
    @FXML private Text txtTotal;
    @FXML private Text txtPresent; // 출근 표시용 (기존 필드명 유지)
    @FXML private Text txtLate; // 지각
    @FXML private Text txtBusiness; // 결근 표시용 (기존 필드명 유지)
    @FXML private Text txtVacation; // 휴가

    // =============== 차트/부서별 영역 (기존 로직 유지) ===============
    @FXML private PieChart overallChart;
    @FXML private PieChart departmentChart;
    @FXML private ComboBox<String> departmentComboBox;

    private RequestService requestService;
    private AttendanceStatsService statsService;

    // 자동 새로고침을 위한 스케줄러
    private ScheduledExecutorService scheduler;
    private volatile String currentDept = "전체";


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
     * 자동 새로고침 설정 (기존 유지)
     */
    private void setupAutoRefresh() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::refreshAllData);
        }, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 전체 데이터 새로고침 (승인 카운팅 동기화 개선)
     */
    public void refreshAllData() {
        // 백그라운드 스레드에서 데이터 조회
        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // UI 스레드에서 업데이트
                    Platform.runLater(() -> {
                        updateAllCounts(); // 승인 관리 카운트 개선
                        loadHomeStats(); // 기존 근태 현황 로직 유지
                        handleDepartmentChange(); // 기존 부서별 차트 로직 유지
                        System.out.println("관리자 홈 데이터 새로고침 완료 (DB 실제값 반영)");
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

    // ========================= 부서 필터링 및 통계 (기존 로직 완전 유지) =========================
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
        currentDept = departmentComboBox.getValue();   // ★ 지금 선택한 부서를 저장

        String dept = departmentComboBox.getValue();
        if (dept != null) {
            loadDepartmentStats(dept);
        }
    }


    private void loadDepartmentStats(String departmentName) {
        try {
            int[] stats = "전체".equals(departmentName)
                    ? statsService.getTodayStats()
                    : statsService.getTodayStatsByDept(departmentName);

            System.out.println("[DEBUG] " + departmentName + " → " + Arrays.toString(stats));
            // 차트 업데이트
            updateDepartmentChart(stats, departmentName);
        } catch (Exception e) {  System.err.println("부서별 통계 로드 실패: " + e.getMessage());
            e.printStackTrace();
            // 오류 시 기본값 설정
            updateDepartmentChart(new int[]{0, 0, 0, 0, 0}, departmentName); }
    }


    /**
     * 부서별 차트 업데이트 (기존 로직 완전 유지)
     */
    private void updateDepartmentChart(int[] stats, String deptName) {
        Platform.runLater(() -> {
            try {
                if (!deptName.equals(departmentComboBox.getValue())) return;

                int sum = stats[1] + stats[2] + stats[3] + stats[4];
                ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
                if (sum > 0) {
                    if (stats[1] > 0) data.add(new PieChart.Data("출근", stats[1]));
                    if (stats[2] > 0) data.add(new PieChart.Data("지각", stats[2]));
                    if (stats[3] > 0) data.add(new PieChart.Data("결근", stats[3]));
                    if (stats[4] > 0) data.add(new PieChart.Data("휴가", stats[4]));
                } else {
                    data.add(new PieChart.Data("출근 기록 없음", 1));
                }

                departmentChart.setData(data);
                departmentChart.setTitle(
                        ("전체".equals(deptName) ? "전체 부서" : deptName + " 부서")
                                + " (" + stats[0] + "명)");

                /* ---------- 범례를 전체 차트랑 똑같이 ---------- */
                departmentChart.setLegendVisible(true);         // 켜기
                departmentChart.setLegendSide(Side.BOTTOM);     // 하단 고정

                // 흰색 말풍선-박스 스타일

                Node legend = departmentChart.lookup(".chart-legend");
                if (legend != null) {                            // 흰색 말풍선-박스
                    legend.setStyle("""
                    -fx-background-color: #ffffffee;
                    -fx-background-radius: 8;
                    -fx-border-radius: 8;
                    -fx-padding: 6 12 6 12;
                """);
                }
                /* ---------------------------------------------- */

                departmentChart.setVisible(true);
                departmentChart.setManaged(true);
                applyFixedStatusColors(departmentChart);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }


    // ========================= 메인/전체 통계 (기존 로직 완전 유지) =========================
    private void loadHomeStats() {
//        System.out.println(">> loadHomeStats 시작");
        try {
            int[] stats = statsService.getTodayStats();
            Platform.runLater(() -> {
                // 텍스트 업데이트 (기존 로직 유지)
                if (txtTotal != null) txtTotal.setText( stats[0] + "명");
                if (txtPresent != null) txtPresent.setText( stats[1] + "명");
                if (txtLate != null) txtLate.setText(  stats[2] + "명");
                if (txtBusiness != null) txtBusiness.setText( stats[3] + "명");
                if (txtVacation != null) txtVacation.setText( stats[4] + "명");

                // 전체 차트 업데이트 (기존 로직 유지)
                updateOverallChart(stats);
            });
            System.out.println("근태 현황 로드 완료: " +
                    "[총원:" + stats[0] + ", 출근:" + stats[1] + ", 지각:" + stats[2] +
                    ", 결근:" + stats[3] + ", 휴가:" + stats[4] + "]");
//            System.out.println(">>loadHomeStats 완료" + Arrays.toString(stats));
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
     * 전체 차트 업데이트 (기존 로직 완전 유지)
     */
    private void updateOverallChart(int[] stats) {
        Platform.runLater(() -> {
            try {
                // 기존 데이터 제거
                overallChart.getData().clear();

                // 0이 아닌 데이터만 추가
                ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
                if (stats[1] > 0) chartData.add(new PieChart.Data("출근", stats[1])); // 출근
                if (stats[2] > 0) chartData.add(new PieChart.Data("지각", stats[2])); // 지각
                if (stats[3] > 0) chartData.add(new PieChart.Data("결근", stats[3])); // 결근
                if (stats[4] > 0) chartData.add(new PieChart.Data("휴가", stats[4])); // 휴가

                if (!chartData.isEmpty()) {
                    overallChart.setData(chartData);
                    overallChart.setTitle("전체 근무 현황 (" + stats[0] + "명)");
                    overallChart.setVisible(true);
                    overallChart.setManaged(true);

                    // 고정 색상 적용 (기존 로직 유지)
                    applyFixedStatusColors(overallChart);
                } else {
                    // 데이터가 없을 때 처리
                    overallChart.setTitle("전체 근무 현황 (출근 기록 없음)");
                    chartData.add(new PieChart.Data("출근 기록 없음", 1));
                    overallChart.setData(chartData);

                    // 기록 없음 항목에는 회색 적용
                    Platform.runLater(() -> {
                        for (PieChart.Data data : overallChart.getData()) {
                            if (data.getNode() != null) {
                                data.getNode().setStyle("-fx-pie-color: #CCCCCC;");
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
     * 부서별 필터링과 관계없이 상태별로 고정 색상을 적용하는 메서드 (기존 로직 완전 유지)
     */
    private void applyFixedStatusColors(PieChart chart) {

        Runnable paint = () -> {
            for (PieChart.Data d : chart.getData()) {
                String c = switch (d.getName()) {
                    case "출근"  -> "#4CAF50";   // mint-green
                    case "지각"  -> "#FF9800";   // apricot-yellow
                    case "결근"  -> "#F44336";   // coral-red
                    case "휴가"  -> "#2196f3";   // sky-blue
                    default      -> "#CCCCCC";   // placeholder

                };
                if (d.getNode() != null)
                    d.getNode().setStyle("-fx-pie-color:" + c + ";");

                Node legend = chart.lookup(
                        ".chart-legend-item[data=\"" + d.getName() + "\"] .chart-legend-item-symbol");
                if (legend != null)
                    legend.setStyle("-fx-background-color:" + c + ";");
            }
        };

        /* slice 가 만들어진 다음 프레임에 색을 입혀야 깜빡임이 없음 */
        Platform.runLater(() -> Platform.runLater(paint));
    }

    // ========================= 승인관리 기능 (DB 실제값 표시 및 카운팅 동기화 개선) =========================

    /**
     * 승인 관리 카운트 업데이트 (실시간 동기화 개선)
     */
    private void updateAllCounts() {
        try {
            Platform.runLater(() -> {
                try {
                    if (vacationCountText != null) {
                        int vacationCount = requestService.getPendingCount(RequestType.ANNUAL);
                        vacationCountText.setText(String.valueOf(vacationCount));
                        System.out.println("휴가 대기 건수: " + vacationCount);
                    }

                    if (vacationCountText != null) {
                        int vacationCount = requestService.getPendingCount(RequestType.HALFDAY);
                        vacationCountText.setText(String.valueOf(vacationCount));
                        System.out.println("휴가 대기 건수: " + vacationCount);
                    }

                    if (clockChangeCountText != null) {
                        int clockCount = requestService.getPendingCount(RequestType.OVERTIME);
                        clockChangeCountText.setText(String.valueOf(clockCount));
                        System.out.println("출퇴근변경 대기 건수: " + clockCount);
                    }

                    if (clockChangeCountText != null) {
                        int clockCount = requestService.getPendingCount(RequestType.HOLIDAY);
                        clockChangeCountText.setText(String.valueOf(clockCount));
                        System.out.println("출퇴근변경 대기 건수: " + clockCount);
                    }


                    if (outworkCountText != null) {
                        int outworkCount = requestService.getPendingCount(RequestType.FIELDWORK);
                        outworkCountText.setText(String.valueOf(outworkCount));
                        System.out.println("출장 대기 건수: " + outworkCount);
                    }

                    if (completedCountText != null) {
                        int completedCount = requestService.getAllCompletedCount();
                        completedCountText.setText(String.valueOf(completedCount));
                        System.out.println("전체 완료 건수: " + completedCount);
                    }

                    System.out.println("✅ 승인 관리 카운트 업데이트 완료 (DB 실제값 반영)");
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
        bindIfNotNull(vacationCountText, RequestType.ANNUAL);
        bindIfNotNull(clockChangeCountText, RequestType.OVERTIME);
        bindIfNotNull(outworkCountText, RequestType.FIELDWORK);

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

            // 창 닫힐 때 데이터 새로고침 (카운팅 동기화 핵심!)
            dialog.setOnCloseRequest((WindowEvent e) -> {
                System.out.println("요청 목록 창 닫힘 - 실시간 데이터 새로고침 시작");
                refreshAllData(); // 실시간 카운트 동기화
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

            // 창 닫힐 때 데이터 새로고침 (카운팅 동기화 핵심!)
            dialog.setOnCloseRequest((WindowEvent e) -> {
                System.out.println("결재 완료 창 닫힘 - 실시간 데이터 새로고침 시작");
                refreshAllData(); // 실시간 카운트 동기화
            });

            dialog.show();
            System.out.println("결재 완료 창 열기 성공");
        } catch (IOException ex) {
            System.err.println("결재 완료 창 열기 중 오류: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * 컨트롤러 종료 시 리소스 정리
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
