package org.example.layout;


import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Header extends HBox {

    public Header() {
        setStyle("-fx-background-color: #FFFFFF;");
        setPadding(new Insets(10, 0, 10, 20));
        setPrefHeight(80);

        ImageView logo = new ImageView(new Image("file:startup-1-10.png"));
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);

        getChildren().add(logo);
    }
}