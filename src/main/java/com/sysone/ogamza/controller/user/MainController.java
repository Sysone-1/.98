package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LogoutUtil;
import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private ImageView headerProfile;
    @FXML private Circle profileBorder;
    @FXML private Text name;
    @FXML private Text dept;

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
            imageUrl = getClass().getResource("/images/eunwoo.png"); // 기본 이미지 fallback
        }
        if (imageUrl != null) {
            headerProfile.setImage(new Image(imageUrl.toExternalForm()));
        }

        double r = profileBorder.getRadius();
        Circle clip2 = new Circle(r, r, r);
        headerProfile.setClip(clip2);

        name.setText(user.getName());
        dept.setText(user.getDeptName());

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


}