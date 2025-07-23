package com.sysone.ogamza;

import com.sysone.ogamza.service.user.FortuneService;
import com.sysone.ogamza.service.user.ScheduleService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* 글꼴 등록 : 첫 실행 때 한 번만 호출해주면 됨.
           이후부터 Font.font("Inter",...)
        */
      Font.loadFont(
                getClass().getResourceAsStream("/fonts/Inter-VariableFont_opsz,wght.ttf"),
                10
        );
      Font.loadFont(getClass().getResourceAsStream("/fonts/SUIT-Regular.tff"),10);
      Font.loadFont(getClass().getResourceAsStream("/fonts/KoPubWorld Batang Light.ttf"),10);
      Font.loadFont(getClass().getResourceAsStream("/fonts/ylee Mortal Heart, Immortal Memory v.1.11 (TTF).ttf"),10);
      Font.loadFont(getClass().getResourceAsStream("/fonts/SUIT-SemiBold.tff"),10);
      // 안전한 폰트 로딩

        /* 2) FXML 파일 로드 */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        /* 3) Scene + CSS */
       // Scene scene = new Scene(root, 1280, 832);
        Scene scene = new Scene(root, 1300, 850);
        scene.getStylesheets().add (
            Objects.requireNonNull(getClass().getResource("/css/main.css")).toExternalForm());

        /* 4) Stage 세팅 */
        primaryStage.setTitle("ontime.89");
        primaryStage.setScene(scene);
        primaryStage.show();

        FortuneService.getInstance().todayLuck();
        ScheduleService.getInstance().scheduleDailyWorkingTime();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
