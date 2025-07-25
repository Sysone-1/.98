package com.sysone.ogamza.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // 시작 시 로그인 페이지로 로딩
        System.out.println(" 로그인 페이지 로딩 ");
        loadPage("/fxml/Home.fxml");
    }

    public void goHome(MouseEvent event) {
        System.out.println(" 메인 홈 이동 ");
        loadPage("/fxml/Home.fxml");
    }

    public void goSettings(MouseEvent event) {
        System.out.println(" 셋팅 이동 ");

        loadPage("/fxml/Settings.fxml");
    }

    public void goDashboard(MouseEvent event){
        loadPage("/fxml/Dashboard.fxml");
        System.out.println(" 대시보드 이동 ");
    }

    public void goRecord(MouseEvent event){
        System.out.println(" 레코드 이동 ");
    }

    public void logout(MouseEvent mouseEvent) {
        System.out.println(" 로그아웃 ");
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
