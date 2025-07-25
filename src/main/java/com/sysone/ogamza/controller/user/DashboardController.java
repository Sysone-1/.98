package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.service.user.DashboardService;
import com.sysone.ogamza.view.ArcProgress;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Locale;

/**
 * 사용자 대시보드에 대한 기능을 담당하는 컨트롤러입니다.
 * <p>
 * 사용자의 출퇴근 시간, 근로 시간, 연차, 일정 등의 정보를 조회 및 표시하며,
 * 일정 등록 및 결재 내역 확인, 사원 등록, 카드 태깅, 달력 상세 보기 등의 기능을 제공합니다.
 *
 * @author 김민호
 */
public class DashboardController {
    @FXML private Text accessTime, leaveTime, workingHours, remainingWorkingHours, totalVacation, usedVacation, remainingVacation;
    @FXML private Text totalWorkingHours, totalRemainingWorkingHours, extendWorkingTime, weekendWorkingTime;
    @FXML private ProgressBar workingProgressBar, vacationProgressBar;
    @FXML private Text todayMonth, todayWeek;
    @FXML private VBox scheduleListBox;
    @FXML private Text noScheduleText;
    @FXML private ScrollPane scheduleScrollPane;
    @FXML private ImageView tooltipImage;
    private static final DashboardService dashboardService = DashboardService.getInstance();
    private static final Session employeeSession = Session.getInstance();
    public static long empId = 0L;

    /**
     * 컨트롤러 초기화 메서드.
     * 사용자 출퇴근 시간, 근로 시간, 연차 정보, 일정 등을 로드합니다.
     */
    @FXML
    public void initialize() {
        empId = Session.getInstance().getLoginUser().getId();

        loadAccessTime();
        loadLeaveTime();
        loadWorkingHours();
        loadVacationDays();
        loadTotalWorkingHours();
        loadTodayScheduleList();

        Platform.runLater(() -> {
            Image img = new Image(getClass().getResource("/images/tooltip.png").toExternalForm());
            tooltipImage.setImage(img);
            Tooltip tooltip = new Tooltip("잔여 근로 : 휴게 시간  1시간 제외");
            tooltip.setStyle(
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10px;"
            );
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(tooltipImage, tooltip);
        });
    }

    /**
     * 출근 시간 조회 및 화면에 설정합니다.
     */
    private void loadAccessTime() {
        accessTime.setText(dashboardService.getTodayAccessTime(empId));
    }

    /**
     * 퇴근 시간 조회 및 화면에 설정합니다.
     */
    private void loadLeaveTime() {
        String time = dashboardService.getTodayLeaveTime(empId);
        leaveTime.setText(time);
    }

    /**
     * 금일 근로 시간 및 잔여 근로 시간을 조회하고 화면에 반영합니다.
     */
    private void loadWorkingHours() {
        String[] timeArray = dashboardService.getWorkingTime(empId);
        int totalMinutes = Integer.parseInt(timeArray[2]);

        ArcProgress.percent =((double) totalMinutes / 540) * 100;
        workingHours.setText(timeArray[0]);
        remainingWorkingHours.setText(timeArray[1]);
    }

    /**
     * 총 연차, 사용 연차, 남은 연차를 조회하여 화면에 표시하고, 연차 프로그레스바를 설정합니다.
     */
    private void loadVacationDays() {
        int total = dashboardService.getVacationDays(empId);
        double used = dashboardService.getUsedVacationDays(empId);
        double remaining = total - used;

        String usedSting = String.valueOf(used);

        if (usedSting.contains(".0")) {
            usedVacation.setText((int)used + "일");
            remainingVacation.setText((int)remaining + "일");
        } else {
            usedVacation.setText(used + "일");
            remainingVacation.setText(remaining + "일");
        }

        totalVacation.setText(total + "일");
        vacationProgressBar.setProgress((double) used / total);
    }

    /**
     * 주간 총 근로 시간 및 남은 근무 시간을 계산하여 화면에 설정합니다.
     * 연장 및 휴일 근무 시간도 포함됩니다.
     */
    private void loadTotalWorkingHours() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int week = now.get(WeekFields.of(Locale.KOREA).weekOfMonth());

        todayMonth.setText(String.format("%02d월", month));
        todayWeek.setText(week + "주차");

        // 해당 주의 평일 근로 시간
        int base = dashboardService.getTotalWorkingHours(empId);

        // 해당 주의 연장 근무 횟수
        int extend = dashboardService.getTotalExtendWorkingHours(empId);

        // 해당 주의 휴일 근무 횟수
        int weekend = dashboardService.getTotalWeekendWorkingHours(empId);

        int totalMinutes = base + (extend + weekend) * 60;

        int totalHours = totalMinutes / 60;
        int remainHours = 68 - totalHours;
        int remainMinutes = totalMinutes % 60;

        if (remainMinutes > 0) {
            totalWorkingHours.setText(totalHours + "시간 " + remainMinutes + "분");
            totalRemainingWorkingHours.setText((remainHours - 1) + "시간 " + (60 - remainMinutes) + "분");
        } else {
            totalWorkingHours.setText(totalHours + "시간");
            totalRemainingWorkingHours.setText(remainHours + "시간");
        }

        workingProgressBar.setProgress((double) totalMinutes / (68 * 60));
        extendWorkingTime.setText((12 - dashboardService.getTotalExtendWorkingHours(empId)) + "시간 ");
        weekendWorkingTime.setText((16 - dashboardService.getTotalWeekendWorkingHours(empId)) + "시간 ");
    }

    /**
     * 금주 등록된 일정 리스트를 조회하여 화면에 표시합니다.
     * 일정이 없을 경우 등록된 일정이 없다는 표시를 합니다.
     */
    private void loadTodayScheduleList() {
        scheduleListBox.getChildren().clear();

        List<String> scheduleList = dashboardService.getWeekSchedules(empId);
        scheduleListBox.setAlignment(scheduleList.isEmpty() ? Pos.CENTER : Pos.TOP_CENTER);
        scheduleListBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));

        if (scheduleList.isEmpty()) {
            scheduleListBox.getChildren().add(noScheduleText);

        } else {
            scheduleScrollPane.setPadding(new Insets(20, 10, 20, 10));
            scheduleListBox.setPadding(new Insets(0, 10, 0, 10));

            for (int i = 0; i < scheduleList.size(); i++) {
                Label item = dashboardService.getLabel(scheduleList.get(i), i);
                scheduleListBox.getChildren().add(item);
            }
        }
    }

    /**
     * '결재 상신' 버튼 클릭 시 실행되는 핸들러입니다.
     * 결재 등록 폼을 모달 형태로 표시합니다.
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void handleAddScheduleClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/ScheduleRegister.fxml"));
            Parent formRoot = loader.load();


            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("결재 상신");

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

    /**
     * '결재 내역' 버튼 클릭 시 실행되는 핸들러입니다.
     * 사용자의 결재 내역 리스트를 모달로 표시합니다.
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void handleFetchScheduleClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/ScheduleList.fxml"));
            Parent formRoot = loader.load();

            ScheduleListController controller = loader.getController();
            controller.loadScheduleList();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("결재 내역");

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

    /**
     * NFC 카드 태그 버튼 클릭 시 실행되는 핸들러입니다.
     * 카드 태깅을 위한 창을 열고 NFC 리스닝을 시작합니다.
     *
     * @param event 클릭 이벤트
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
     * 달력 상세 버튼 클릭 시 실행되는 핸들러입니다.
     * 월별 일정 상세 정보를 보여주는 달력 창을 모달로 표시합니다.
     *
     * @param event 클릭 이벤트
     */
    @FXML
    private void handleCalendarDetailClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/CalendarDetail.fxml"));
            Parent formRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("달력 상세");

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
