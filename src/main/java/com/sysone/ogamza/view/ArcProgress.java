package com.sysone.ogamza.view;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ArcProgress {

    @FXML private Canvas gaugeCanvas;

    @FXML
    public void initialize() {
        drawGauge(30); // 진행률
    }

    public void drawGauge(double percent) {
        GraphicsContext gc = gaugeCanvas.getGraphicsContext2D();

        double width = gaugeCanvas.getWidth();
        double height = gaugeCanvas.getHeight();

        double centerX = width / 2;
        double centerY = height; // 하단을 중심점으로 설정
        double radius = 100;
        double strokeWidth = 20;

        double startAngle = 180;
        double angleRange = 180;

        // 배경 반원 (회색)
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(strokeWidth);
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, -angleRange, javafx.scene.shape.ArcType.OPEN);

        // 진행도 반원 (파랑)
        gc.setStroke(Color.DODGERBLUE);
        gc.setLineWidth(strokeWidth);
        double progressAngle = -(angleRange * percent / 100); // 시계 반대 방향
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, progressAngle, javafx.scene.shape.ArcType.OPEN);
    }
}
