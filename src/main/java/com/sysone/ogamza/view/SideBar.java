package com.sysone.ogamza.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SideBar extends VBox {

    public SideBar() {
        setPadding(new Insets(50));
        setSpacing(30);
        setStyle("-fx-background-color: #0099FF;");
        setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(
                createSidebarItem("mdi-home-outline0.svg", "Home"),
                createSidebarItem("cuida-dashboard-outline0.svg", "Dashboard"),
                createSidebarItem("streamline-flex-text-file-remix0.svg", "Record"),
                createSidebarItem("uil-setting0.svg", "Setting")
        );
    }

    private VBox createSidebarItem(String iconPath, String labelText) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image("file:" + iconPath));
        icon.setFitWidth(40);
        icon.setFitHeight(40);

        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Inter", 16));

        box.getChildren().addAll(icon, label);
        return box;
    }
}
