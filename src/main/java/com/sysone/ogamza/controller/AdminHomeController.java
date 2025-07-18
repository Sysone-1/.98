package com.sysone.ogamza.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminHomeController implements Initializable {

    @FXML private Pane gaugeContainergreen;
    @FXML private Pane gaugeContainerpink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초록색 게이지 (예: 78%)
        createGauge(gaugeContainergreen, 78, "#4CAF50");
        // 분홍색 게이지 (예: 43%)
        createGauge(gaugeContainerpink, 43, "#E91E63");
    }

    private void createGauge(Pane container, double value, String colorHex) {
        double w = container.getPrefWidth();
        double h = container.getPrefHeight();
        double radius = Math.min(w, h) / 2 - 20;
        double strokeWidth = 20;

        // 트랙 (회색 원호)
        Arc track = new Arc(radius + strokeWidth/2, radius + strokeWidth/2,
                radius, radius,
                135, 270);
        track.setType(ArcType.OPEN);
        track.setStroke(Color.LIGHTGRAY);
        track.setStrokeWidth(strokeWidth);
        track.setFill(Color.TRANSPARENT);

        // 진행(컬러 원호)
        Arc progress = new Arc(radius + strokeWidth/2, radius + strokeWidth/2,
                radius, radius,
                135, -270 * (value / 100));
        progress.setType(ArcType.OPEN);
        progress.setStroke(Color.web(colorHex));
        progress.setStrokeWidth(strokeWidth);
        progress.setFill(Color.TRANSPARENT);

        // 중앙 텍스트
        Text text = new Text((int) value + "%");
        text.setFont(Font.font(36));
        text.setFill(Color.web("#333333"));
        // 가운데 정렬
        text.setLayoutX(radius + strokeWidth/2 - text.getBoundsInLocal().getWidth() / 2);
        text.setLayoutY(radius + strokeWidth/2 + text.getBoundsInLocal().getHeight() / 4);

        // Pane에 추가
        container.getChildren().setAll(track, progress, text);
    }
}
