package com.sysone.ogamza.utils.api.alert;

import javafx.scene.control.Alert;

/**
 * JavaFX의 {@link Alert} 다이얼로그를 간편하게 생성하고 표시하는 유틸리티 클래스입니다.
 * <p>
 * 알림 종류(AlertType), 제목, 메시지를 인자로 받아 알림창을 출력합니다.
 * </p>
 *
 * @author 김민호
 */
public class AlertCreate {
    /**
     * 지정된 타입, 제목, 메시지를 가진 알림(Alert)을 생성하여 화면에 표시합니다.
     *
     * @param alertType 알림의 종류 (예: INFORMATION, WARNING, ERROR 등)
     * @param title     알림창의 제목
     * @param message   알림창에 표시할 메시지 본문
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
