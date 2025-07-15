package com.sysone.ogamza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: 로그인 페이지 완성되면 Login.fxml로 변경
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("ontime.89");
        primaryStage.setScene(new Scene(root, 1284, 832));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
