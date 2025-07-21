package com.sysone.ogamza.controller;

import com.sysone.ogamza.service.ScheduleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ScheduleContentController {

    @FXML private Text title, type, date, content;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();

    private int scheduleIndex;

    /**
        선택한 일정 리스트 항목에 대한 인덱스 setting
    */
    public void setScheduleIndex(int index) {
        this.scheduleIndex = index;
        loadScheduleContent();
    }

    /**
        일정 내용 조회
    */
    private void loadScheduleContent() {
        List<String> scheduleList =  scheduleService.getScheduleContent(DashboardController.empId, scheduleIndex);

        title.setText(scheduleList.get(0));
        type.setText(scheduleList.get(1));
        date.setText(scheduleList.get(2));
        content.setText(scheduleList.get(3));
    }

    /**
        닫기 버튼 핸들러
    */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
