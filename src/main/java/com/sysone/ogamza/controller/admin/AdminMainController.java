package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.LogoutUtil;
import com.sysone.ogamza.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import com.sysone.ogamza.service.admin.NotificationService;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public class AdminMainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label adminNameLabel;

    @FXML
    private Label adminDepartmentLabel;

    @FXML
    private ImageView adminProfileImageView;

    @FXML
    private ImageView notificationIcon;

    @FXML
    private Label notificationCountLabel;


    private NotificationService notificationService;
    private ScheduledExecutorService scheduler;


    @FXML
    public void initialize() {

        notificationService = new NotificationService();
        setupNotificationPolling();

        // 알림 아이콘 클릭 이벤트 연결
        if (notificationIcon != null) {
            notificationIcon.setOnMouseClicked(this::handleNotificationClick);
        }

        // 사용자 정보 로드 및 표시
        LoginUserDTO currentUser = Session.getInstance().getLoginUser();
        if (currentUser != null) {
            adminNameLabel.setText(currentUser.getName());
            adminDepartmentLabel.setText(currentUser.getPosition()); // 직책을 부서명으로 사용

            String profile = currentUser.getProfile();
            if (profile != null && !profile.isEmpty()) {
                try {
                    Image profileImage = new Image(profile);
                    adminProfileImageView.setImage(profileImage);
                } catch (Exception e) {
                    System.err.println("프로필 이미지 로드 실패: " + profile + ", " + e.getMessage());
                    // 기본 이미지 설정 또는 이미지 뷰 숨기기
                }
            }
        }
        loadPage("/fxml/admin/AdminHome.fxml");

        Rectangle clip = new Rectangle(anchorPane.getPrefWidth(), anchorPane.getPrefHeight());
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        anchorPane.setClip(clip);

    }


    @FXML
    private void handleNotificationClick(MouseEvent event) {
        try {
            // 1. 미승인 출입 기록 가져오기
            List<LocalDateTime> unauthorizedAccessTimes = notificationService.getUnauthorizedAccessTimes();

            // 2. FXML 로더 생성 및 다이얼로그 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/UnauthorizedAccessLogDialog.fxml"));
            Parent root = loader.load();

            // 3. 컨트롤러 가져와서 데이터 설정
            UnauthorizedAccessLogController controller = loader.getController();
            controller.setLogData(unauthorizedAccessTimes);

            // 4. 모달 다이얼로그 설정 및 표시
            Stage dialogStage = new Stage();
            dialogStage.setTitle("미승인 출입 기록");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // 모달 창으로 설정
            dialogStage.initOwner(notificationIcon.getScene().getWindow()); // 부모 창 설정
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false); // 크기 조절 비활성화
            dialogStage.showAndWait(); // 창이 닫힐 때까지 대기

            // 5. 데이터베이스에서 알림을 '읽음' 상태로 업데이트
            notificationService.markAllUnauthorizedAccessLogAsRead();

            // 6. UI 알림 카운트 초기화 및 숨김
            Platform.runLater(() -> {
                notificationCountLabel.setText("0");
                notificationCountLabel.setVisible(false);
            });

        } catch (IOException e) {
            System.err.println("미승인 출입 기록 다이얼로그 로드 실패: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("알림 클릭 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupNotificationPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int count = notificationService.getUnauthorizedAccessCount();
            Platform.runLater(() -> {
                if (count > 0) {
                    notificationCountLabel.setText(String.valueOf(count));
                    notificationCountLabel.setVisible(true);
                } else {
                    notificationCountLabel.setVisible(false);
                }
            });
        }, 0, 5, TimeUnit.SECONDS); // 5초마다 폴링
    }

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

    public void goHome(MouseEvent event) {
        System.out.println(" 메인 홈 이동 ");
        loadPage("/fxml/admin/AdminHome.fxml");
    }

    public void goSettings(MouseEvent event) {
        System.out.println(" 셋팅 이동 ");

        loadPage("/fxml/admin/AdminSettings.fxml");
    }

    public void goDashboard(MouseEvent event) {
        loadPage("/fxml/admin/AdminDashboard.fxml");
        System.out.println(" 대시보드 이동 ");
    }

    public void goRecord(MouseEvent event) {
        loadPage("/fxml/admin/AdminRecord.fxml");
        System.out.println(" 레코드 이동 ");
    }

    @FXML
    public void logout(MouseEvent event) {
        System.out.println("로그아웃");
        LogoutUtil.logout(event);
    }


    // 페이지 로드
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}