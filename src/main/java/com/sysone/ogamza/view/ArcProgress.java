package com.sysone.ogamza.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 반원 형태의 진행률(게이지)을 JavaFX {@link Canvas}를 사용하여 시각화하는 클래스입니다.
 *
 * <p>진행률은 0~100% 범위로 나타나며, 하단을 기준으로 반원 형태의 Arc로 표시됩니다.
 * 회색 배경 반원 위에 파란색으로 실제 진행률을 표시합니다.</p>
 *
 * <p>{@code percent} 변수는 외부에서 값을 설정할 수 있으며,
 * {@link #initialize()} 시점에 {@link #drawGauge(double)} 메서드를 호출하여 즉시 렌더링됩니다.</p>
 *
 * @author 김민호
 */
public class ArcProgress {

    @FXML private Canvas gaugeCanvas;

    public static double percent = 0;

    /**
     * JavaFX 초기화 메서드로, FXML 로딩 완료 후 {@link Platform#runLater(Runnable)}를 통해
     * {@link #drawGauge(double)}를 호출하여 진행률 게이지를 그립니다.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> drawGauge(percent));
    }

    /**
     * 주어진 퍼센트 값을 기준으로 반원 형태의 게이지를 캔버스에 그림.
     *
     * @param percent 진행률 (0~100 범위)
     */
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
        gc.setStroke(Color.web("#5A82A9"));
        gc.setLineWidth(strokeWidth);
        double progressAngle = -(angleRange * percent / 100); // 시계 반대 방향
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, progressAngle, javafx.scene.shape.ArcType.OPEN);
    }
}
