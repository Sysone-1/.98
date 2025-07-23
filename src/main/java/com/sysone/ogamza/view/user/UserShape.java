package com.sysone.ogamza.view.user;

import javafx.scene.shape.*;

public class UserShape {

    // 도형
    public static Shape getShape(String randomShape){


        Shape shape = null;
        SVGPath svg = new SVGPath();

        switch (randomShape){
            case "원": // 원
                shape = new Circle(35);
                break;
            case "사각형": // 사각형
                shape = new Rectangle(70,70);
                break;
            case "별": // 별
                shape = new Polygon(
                        25.0, 0.0,
                        32.0, 18.0,
                        50.0, 18.0,
                        36.0, 30.0,
                        42.0, 50.0,
                        25.0, 38.0,
                        8.0, 50.0,
                        14.0, 30.0,
                        0.0, 18.0,
                        18.0, 18.0
                );
                shape.setScaleX(1.6);
                shape.setScaleY(1.6);
                break;
            case "세모":  // 세모
                shape = new Polygon(
                        25.0, 0.0,
                                50.0, 50.0,
                                0.0, 50.0
                );
                break;
            case "하트": // 하트
                svg.setContent("M10 30 A20 20 0 0 1 50 30 A20 20 0 0 1 90 30 Q90 60 50 90 Q10 60 10 30 Z");
                svg.setScaleX(0.8);
                svg.setScaleY(0.8);
                 shape = svg;
                break;
            case "마름모": // 마름모
                shape = new Polygon(
                        50, 0,
                                100, 50,
                                50, 100,
                                0, 50
                );
                shape.setScaleX(0.8);
                shape.setScaleY(0.8);
                break;
            case "번개": // 번개
                svg.setContent("M10 0 L30 0 L15 25 L35 25 L10 70 L20 40 L0 40 Z");
                svg.setScaleX(1.4);
                svg.setScaleY(1);
                shape = svg;
                break;
            case "퍼즐" : // 퍼즐
                svg.setContent("M39,15c0-2.2-1.8-4-4-4h-6c-0.7,0-1.1-0.8-0.7-1.4c0.6-1,0.9-2.2,0.6-3.5c-0.4-2-1.9-3.6-3.8-4 C21.8,1.4,19,3.9,19,7c0,1,0.3,1.8,0.7,2.6c0.4,0.6,0,1.4-0.8,1.4h-6c-2.2,0-4,1.8-4,4v7c0,0.7,0.8,1.1,1.4,0.7 c1-0.6,2.2-0.9,3.5-0.6c2,0.4,3.6,1.9,4,3.8c0.7,3.2-1.8,6.1-4.9,6.1c-1,0-1.8-0.3-2.6-0.7C9.8,30.9,9,31.3,9,32v6c0,2.2,1.8,4,4,4 h22c2.2,0,4-1.8,4-4V15z");
                svg.setScaleX(1.6);
                svg.setScaleY(1.6);
                shape = svg;
                break;
            default: // 하트
                svg.setContent("M10 30 A20 20 0 0 1 50 30 A20 20 0 0 1 90 30 Q90 60 50 90 Q10 60 10 30 Z");
                svg.setScaleX(0.8);
                svg.setScaleY(0.8);
                shape = svg;
                break;
        }
        return shape;
    }
}
