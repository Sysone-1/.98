
package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LogoutUtil;
import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.service.user.MessageService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;

/**
 * 메인 컨트롤러 클래스입니다.
 * 사용자 로그인 정보 로딩, 화면 전환, 쪽지 모달 호출, 쪽지 수 폴링 등 전반적인 사용자 메인 뷰 관리를 담당합니다.
 * 세션에 저장된 로그인 정보를 바탕으로 유저 정보와 뷰를 초기 세팅합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */

public class MainController {

    /** 메인 콘텐츠 영역 (동적으로 페이지 로드됨) */
    @FXML private StackPane contentArea;
    /** 프로필 이미지 영역 */
    @FXML private StackPane profileContainer;
    /** 사원 이름 텍스트 */
    @FXML private Text name;
    /** 부서명 텍스트 */
    @FXML private Text dept;
    /** 안 읽은 쪽지 수 라벨 */
    @FXML private Label unreadCountLabel;
    /** 5초 간격 쪽지 폴링 타이머 */
    private Timeline pollingTimeline;

    /**
     * 초기화 메서드: 로그인 정보 기반으로 홈 로딩 및 유저 정보 세팅
     */
    @FXML
    public void initialize() {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        System.out.println(" 메인 페이지 로딩 ");
        goHome(null);

        // 프로필 이미지 불러오기
        String path = user.getProfile();
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            imageUrl = getClass().getResource("/images/eunwoo.png");
        }

        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            Circle profileCircle = new Circle(25);
            profileCircle.setFill(new ImagePattern(image));
            profileContainer.getChildren().clear();
            profileContainer.getChildren().add(profileCircle);
        }

        name.setText(user.getName());
        dept.setText(user.getDeptName());

        startUnreadPolling();
    }

    /** 홈으로 이동 */
    public void goHome(MouseEvent event) {
        System.out.println(" 메인 홈 이동 ");
        loadPage("/fxml/user/UserHome.fxml");
    }

    /** 환경설정으로 이동 */
    public void goSettings(MouseEvent event) {
        System.out.println(" 셋팅 이동 ");
        loadPage("/fxml/user/Settings.fxml");
    }

    /** 대시보드로 이동 */
    public void goDashboard(MouseEvent event){
        loadPage("/fxml/user/Dashboard.fxml");
        System.out.println(" 대시보드 이동 ");
    }

    /** 근태기록 화면 이동 */
    public void goRecord(MouseEvent event){
        loadPage("/fxml/user/Record.fxml");
        System.out.println(" 레코드 이동 ");
    }

    /** 로그아웃 */
    @FXML
    public void logout(MouseEvent event) {
        System.out.println(" 로그아웃 ");
        LogoutUtil.logout(event);
    }

    /**
     * 지정된 FXML 파일 경로로 화면을 동적으로 교체합니다.
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

    /**
     * 쪽지함 모달 오픈
     */
    public void handleMessageInBox(MouseEvent event){
        System.out.println("메세지함 오픈");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/MessageInbox.fxml"));
            Parent view = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("쪽지함");
            modalStage.setScene(new Scene(view));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 읽지 않은 쪽지 개수 주기적으로 확인하는 폴링 시작 (5초 간격)
     */
    private void startUnreadPolling() {
        pollingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> updateUnreadCount())
        );
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    /**
     * 읽지 않은 쪽지 수 갱신
     */
    private void updateUnreadCount() {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) return;

        int count = MessageService.getInstance().getUnreadMessageCount(user.getId());
        unreadCountLabel.setVisible(count > 0);
        unreadCountLabel.setText(String.valueOf(count));
    }
}
