package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.service.user.DashboardService;
import com.sysone.ogamza.view.ArcProgress;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
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
import java.util.Locale;

public class DashboardController {
    @FXML private Text accessTime, leaveTime, workingHours, remainingWorkingHours, totalVacation, usedVacation, remainingVacation;
    @FXML private Text totalWorkingHours, totalRemainingWorkingHours, extendWorkingTime, weekendWorkingTime;
    @FXML private ProgressBar workingProgressBar, vacationProgressBar;
    @FXML private Text todayMonth, todayWeek;
    @FXML private VBox scheduleListBox;
    @FXML private Text noScheduleText;
    @FXML private ScrollPane scheduleScrollPane;

    private static final DashboardService dashboardService = DashboardService.getInstance();
    public static final long empId = 1009L;


        @FXML
        public void initialize() {

            loadAccessTime();
            loadLeaveTime();
            loadWorkingHours();
            loadVacationDays();
            loadTotalWorkingHours();
            loadTodayScheduleList();
        }

        /**
            출근 시간 조회 및 setText
        */
        private void loadAccessTime() {
            accessTime.setText(dashboardService.getTodayAccessTime(empId));
        }

        /**
            퇴근 시간 조회 및 setText
        */
        private void loadLeaveTime() {
            String time = dashboardService.getTodayLeaveTime(empId);
            leaveTime.setText(time);
        }

        /**
            근로 시간 및 잔여 근로 시간 조회
        */
        private void loadWorkingHours() {
            String[] timeArray = dashboardService.getWorkingTime(empId);
            int totalMinutes = Integer.parseInt(timeArray[2]);

            ArcProgress.percent =((double) totalMinutes / 540) * 100;
            workingHours.setText(timeArray[0]);
            remainingWorkingHours.setText(timeArray[1]);
        }

        /**
            총연차, 사용 연차, 남은 연차 조회
        */
        private void loadVacationDays() {
            int total = dashboardService.getVacationDays(empId);
            int used = dashboardService.getUsedVacationDays(empId);
            int remaining = total - used;

            totalVacation.setText(total + "일");
            usedVacation.setText(used + "일");
            remainingVacation.setText(remaining + "일");
            vacationProgressBar.setProgress((double) used / total);
        }

        /**
            총 근무 시간, 남은 근무 시간, 남은 연장 근무 시간, 남은 휴일 연장 근무 시간 조회
        */
        private void loadTotalWorkingHours() {
            LocalDateTime now = LocalDateTime.now();
            int month = now.getMonthValue();
            int week = now.get(WeekFields.of(Locale.KOREA).weekOfMonth());

            todayMonth.setText(String.format("%02d월", month));
            todayWeek.setText(week + "주차");

            // 해당 월 평일 근로 시간
            int base = dashboardService.getTotalWorkingHours(empId);

            // 해당 월 연장 근무 횟수
            int extend = dashboardService.getTotalExtendWorkingHours(empId);

            // 해당 월 휴일 근무 횟수
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
            일정 등록 리스트 조회
        */
        private void loadTodayScheduleList() {
            scheduleListBox.getChildren().clear();

            List<String> scheduleList = dashboardService.getWeekSchedules(empId);
            scheduleListBox.setAlignment(scheduleList.isEmpty() ? Pos.CENTER : Pos.TOP_CENTER);
            scheduleListBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(30), Insets.EMPTY)));

            if (scheduleList.isEmpty()) {
                scheduleListBox.getChildren().add(noScheduleText);

            } else {
                scheduleScrollPane.setPadding(new Insets(20, 10, 20, 10));
                scheduleListBox.setPadding(new Insets(10, 10, 0, 10));

                for (int i = 0; i < scheduleList.size(); i++) {
                    Label item = dashboardService.getLabel(scheduleList.get(i), i);
                    scheduleListBox.getChildren().add(item);
                }
            }
        }

        /**
            일정 결재 클릭 핸들러
        */
        @FXML
        private void handleAddScheduleClick(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/ScheduleRegister.fxml"));
                Parent formRoot = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle("일정 결재");

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
           결재 내역 클릭 핸들러
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
     임시 회원 등록 버튼 핸들러
     */
    @FXML
    private void handleAddEmployee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/EmployeeRegister.fxml"));
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

    /**
     임시 카드 태그 버튼 핸들러
     */
    @FXML
    private void handleTagCard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/EmployeeTag.fxml"));
            Parent formRoot = loader.load();

            // 컨트롤러 가져오기
            NFCCardTagController tagController = loader.getController();

            // 다이얼로그 스테이지 설정
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("카드 태그");

            // 부모 창 설정
            Window parentWindow = ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(parentWindow);

            // 장면 설정
            Scene dialogScene = new Scene(formRoot);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);

            // 창 닫힐 때 NFC 루프 종료
            dialogStage.setOnCloseRequest(e -> {
                tagController.stopListeningLoop();
            });

            // NFC 감지 루프 시작
            tagController.startListeningLoop();

            // 창 열기 (모달)
            dialogStage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

