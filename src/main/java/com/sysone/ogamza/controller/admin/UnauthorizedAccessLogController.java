package com.sysone.ogamza.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * UnauthorizedAccessLogController 클래스는
 * 관리자 대시보드에서 주별 출입 거부 로그를 보여주는 팝업 창의 컨트롤러입니다.
 *
 * 출입 거부 시각 목록을 문자열로 포맷하여 표시하며, 사용자가 닫기 버튼을 누를 경우 해당 창을 종료합니다.
 *
 * @author 조윤상
 * @since 2025-07-23
 */
public class UnauthorizedAccessLogController {

    /**
     * 출입 거부 시각을 표시할 리스트 뷰입니다.
     */
    @FXML
    private ListView<String> logListView;

    /**
     * 출입 거부 시각 데이터를 {@link ListView}에 표시합니다.
     *
     * @param logTimes 출입 거부가 발생한 {@link LocalDateTime} 리스트
     */
    public void setLogData(List<LocalDateTime> logTimes) {
        ObservableList<String> items = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (LocalDateTime time : logTimes) {
            items.add(time.format(formatter));
        }
        logListView.setItems(items);
    }

    /**
     * 팝업 창을 닫습니다.
     */
    @FXML
    private void closeDialog() {
        Stage stage = (Stage) logListView.getScene().getWindow();
        stage.close();
    }
}
