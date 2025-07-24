package com.sysone.ogamza;

import javafx.application.Platform;
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
            Session.getInstance().clear();
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(LogoutUtil.class.getResource("/fxml/Login.fxml")));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("로그인");
            stage.setWidth(1300);
            stage.setHeight(820);
            stage.show();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
