package com.sysone.ogamza.controller.admin;

import com.sysone.ogamza.LogoutUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class AdminMainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize() {
        // 시작 시 로그인 페이지로 로딩
        System.out.println(" 로그인 페이지 로딩 ");
        loadPage("/fxml/admin/AdminHome.fxml");

        Rectangle clip = new Rectangle(anchorPane.getPrefWidth(), anchorPane.getPrefHeight());
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        anchorPane.setClip(clip);

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

            contentArea.getChildren().setAll(view);  // 페이지 교체
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}