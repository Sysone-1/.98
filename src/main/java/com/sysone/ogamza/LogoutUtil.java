package com.sysone.ogamza;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LogoutUtil {
    public static void logout(MouseEvent event) {
        try {
            //1. 세션 초기화
            Session.getInstance().clear();

            //2. 로그인 화면 로드
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(LogoutUtil.class.getResource("/fxml/Login.fxml")));
            Scene loginScene = new Scene(loginRoot);

            //3. 현재 스테이지에서 적용
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("로그인");
            stage.show();

        }catch (IOException e) {
            e.printStackTrace();
        }




    }

}
