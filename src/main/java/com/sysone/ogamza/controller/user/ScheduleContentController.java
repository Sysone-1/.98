package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleContentController {

    @FXML private Text title, date, content;
    @FXML private ImageView titleIcon, dateIcon;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();
    private int scheduleIndex;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Image titleImg = new Image(getClass().getResource("/images/titleIcon.png").toExternalForm());
            Image dateImg = new Image(getClass().getResource("/images/dateIcon.png").toExternalForm());
            titleIcon.setImage(titleImg);
            dateIcon.setImage(dateImg);

            // 이미지 크기 조절
            titleIcon.setFitWidth(20);
            titleIcon.setFitHeight(20);
            titleIcon.setPreserveRatio(false); // 비율 유지

            dateIcon.setFitWidth(25);
            dateIcon.setFitHeight(25);
            dateIcon.setPreserveRatio(false); // 비율 유지
        });
    }

    /**
        일정 상세 달력에서 사용할 data setting
     */
    public void setData(ScheduleContentDTO dto) {
        title.setText(dto.getTitle());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.M.d");
        String formattedStart = dto.getStartDate().format(formatter);
        String formattedEnd = dto.getEndDate().format(formatter);

        date.setText(formattedStart + " ~ " + formattedEnd);
        content.setText(dto.getContent());
    }

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
        date.setText(scheduleList.get(2));
        content.setText(scheduleList.get(3));
    }
}
