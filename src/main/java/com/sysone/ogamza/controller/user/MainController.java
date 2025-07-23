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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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


public class MainController {

    @FXML private StackPane contentArea;
    @FXML private StackPane profileContainer;
    @FXML private Text name;
    @FXML private Text dept;
    @FXML private Label unreadCountLabel;
    private Timeline pollingTimeline;


    @FXML
    public void initialize() {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        System.out.println(" 메인 페이지 로딩 ");
        goHome(null);

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

    public void goHome(MouseEvent event) {
        System.out.println(" 메인 홈 이동 ");
        loadPage("/fxml/user/UserHome.fxml");
    }

    public void goSettings(MouseEvent event) {
        System.out.println(" 셋팅 이동 ");

        loadPage("/fxml/user/Settings.fxml");
    }

    public void goDashboard(MouseEvent event){
        loadPage("/fxml/user/Dashboard.fxml");
        System.out.println(" 대시보드 이동 ");
    }

    public void goRecord(MouseEvent event){
        loadPage("/fxml/user/Record.fxml");
        System.out.println(" 레코드 이동 ");
    }

    @FXML
    public void logout(MouseEvent event) {
        System.out.println(" 로그아웃 ");
        LogoutUtil.logout(event);
    }

    // 페이지 로드
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.getChildren().setAll(view);  // 페이지 교체
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleMessageInBox(MouseEvent event){
        System.out.println("메세지함 오픈");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/MessageInbox.fxml"));
            Parent view = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("쪽지함");
            modalStage.setScene(new Scene(view));

            // 📌 모달 설정
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            modalStage.setResizable(false);
            modalStage.initStyle(StageStyle.UTILITY);

            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startUnreadPolling() {
        pollingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> updateUnreadCount())
        );
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private void updateUnreadCount() {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) return;

        int count = MessageService.getInstance().getUnreadMessageCount(user.getId());
        unreadCountLabel.setVisible(count > 0);
        unreadCountLabel.setText(String.valueOf(count));
    }


}