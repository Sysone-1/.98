package com.sysone.ogamza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 안전한 폰트 로딩
        loadCustomFont("/fonts/KoPubWorld Batang Light.ttf", 16);
        loadCustomFont("/fonts/ylee Mortal Heart, Immortal Memory v.1.11 (TTF).ttf", 16);

        // TODO: 로그인 페이지 완성되면 Login.fxml로 변경
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("ontime.89");
        primaryStage.setScene(new Scene(root, 1284, 832));
        primaryStage.show();
    }

    private void loadCustomFont(String fontPath, double size) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, size);
                if (customFont != null) {
                    String fontFamily = customFont.getFamily();
                    System.out.println("폰트 로딩 성공: " + fontFamily);
                } else {
                    System.err.println("폰트 로딩 실패: " + fontPath);
                }
                fontStream.close();
            } else {
                System.err.println("폰트 파일을 찾을 수 없습니다: " + fontPath);
            }
        } catch (Exception e) {
            System.err.println("폰트 로딩 중 오류 발생: " + fontPath + " - " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
