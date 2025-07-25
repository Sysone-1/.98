package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.LogoutUtil;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.service.admin.NotificationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 관리자 메인 화면을 제어하는 컨트롤러 클래스입니다.
 * 사용자 프로필, 알림, 페이지 이동 등의 기능을 담당합니다.
 *
 * @author 조윤상
 * @since 2025-07-24
 */
public class AdminMainController {

    /** 콘텐츠 영역 StackPane - 동적으로 화면 전환되는 뷰가 이 영역에 로드됩니다. */
    @FXML
    private StackPane contentArea;

    /** 루트 AnchorPane - 클리핑 처리 등 레이아웃 설정에 사용됩니다. */
    @FXML
    private AnchorPane anchorPane;

    /** 관리자 이름 표시 라벨 */
    @FXML
    private Label adminNameLabel;

    /** 관리자 부서/직책 표시 라벨 */
    @FXML
    private Label adminDepartmentLabel;

    /** 관리자 프로필 이미지 뷰 */
    @FXML
    private ImageView adminProfileImageView;

    /** 알림 아이콘 이미지 뷰 */
    @FXML
    private ImageView notificationIcon;

    /** 읽지 않은 알림 수 표시 라벨 */
    @FXML
    private Label notificationCountLabel;

    /** 알림 서비스 */
    private NotificationService notificationService;

    /** 알림 주기적 확인용 스케줄러 */
    private ScheduledExecutorService scheduler;

    /**
     * 컨트롤러 초기화 메서드.
     * 사용자 정보 표시, 알림 폴링 설정, 기본 홈 화면 로딩 등을 수행합니다.
     */
    @FXML
    public void initialize() {
        notificationService = new NotificationService();
        setupNotificationPolling();

        if (notificationIcon != null) {
            notificationIcon.setOnMouseClicked(this::handleNotificationClick);
        }

        LoginUserDTO currentUser = Session.getInstance().getLoginUser();
        if (currentUser != null) {
            adminNameLabel.setText(currentUser.getName());
            adminDepartmentLabel.setText(currentUser.getPosition());

            String profile = currentUser.getProfile();
            if (profile != null && !profile.isEmpty()) {
                try {
                    Image profileImage = new Image(profile);
                    adminProfileImageView.setImage(profileImage);
                } catch (Exception e) {
                    System.err.println("프로필 이미지 로드 실패: " + profile + ", " + e.getMessage());
                }
            }
        }

        loadPage("/fxml/admin/AdminHome.fxml");

//        Rectangle clip = new Rectangle(anchorPane.getPrefWidth(), anchorPane.getPrefHeight());
//        clip.setArcWidth(50);
//        clip.setArcHeight(50);
//        anchorPane.setClip(clip);
    }

    /**
     * 알림 아이콘 클릭 시 미승인 출입 기록을 다이얼로그로 표시하고 알림을 읽음 처리합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    @FXML
    private void handleNotificationClick(MouseEvent event) {
        try {
            List<LocalDateTime> unauthorizedAccessTimes = notificationService.getUnauthorizedAccessTimes();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/UnauthorizedAccessLogDialog.fxml"));
            Parent root = loader.load();

            UnauthorizedAccessLogController controller = loader.getController();
            controller.setLogData(unauthorizedAccessTimes);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("미승인 출입 기록");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(notificationIcon.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            notificationService.markAllUnauthorizedAccessLogAsRead();

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

    /**
     * 5초마다 알림 개수를 확인하여 UI에 반영하는 폴링 스케줄을 설정합니다.
     */
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
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * 컨트롤러 종료 시 알림 스케줄러를 안전하게 종료합니다.
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

    /**
     * 홈 화면으로 이동합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    public void goHome(MouseEvent event) {
        System.out.println(" 메인 홈 이동 ");
        loadPage("/fxml/admin/AdminHome.fxml");
    }

    /**
     * 설정 화면으로 이동합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    public void goSettings(MouseEvent event) {
        System.out.println(" 사원 조회 이동 ");
        loadPage("/fxml/admin/EmployeeManagement.fxml");
    }

    /**
     * 대시보드 화면으로 이동합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    public void goDashboard(MouseEvent event) {
        loadPage("/fxml/admin/AdminDashboard.fxml");
        System.out.println(" 대시보드 이동 ");
    }

    /**
     * 레코드 화면으로 이동합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    public void goRecord(MouseEvent event) {
        loadPage("/fxml/admin/AdminRecord.fxml");
        System.out.println(" 레코드 이동 ");
    }

    /**
     * 로그아웃을 수행하고 로그인 화면으로 이동합니다.
     *
     * @param event 마우스 클릭 이벤트
     */
    @FXML
    public void logout(MouseEvent event) {
        System.out.println("로그아웃");
        LogoutUtil.logout(event);
    }

    /**
     * 주어진 FXML 경로에 해당하는 화면을 contentArea에 로드합니다.
     *
     * @param fxmlPath FXML 파일 경로
     */
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
