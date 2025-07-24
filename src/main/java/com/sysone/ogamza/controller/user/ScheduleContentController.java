package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 일정 상세 내용을 표시하는 컨트롤러입니다.
 * <p>
 * 사용자가 선택한 일정 항목의 상세 정보(제목, 날짜, 내용)를 표시하며,
 * 아이콘 설정 및 외부에서 전달받은 인덱스 기반 조회 기능을 제공합니다.
 *
 * @author 김민호
 */
public class ScheduleContentController {

    @FXML private Text title, date, content;
    @FXML private ImageView titleIcon, dateIcon;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();
    private int scheduleIndex;

    private Stage stage;

    /**
     * 외부에서 현재 Stage를 설정합니다.
     *
     * @param stage 현재 팝업 창의 Stage 객체
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * 컨트롤러 초기화 메서드입니다.
     * 아이콘 이미지를 설정하고 초기 스타일을 지정합니다.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Image titleImg = new Image(getClass().getResource("/images/titleIcon.png").toExternalForm());
            Image dateImg = new Image(getClass().getResource("/images/dateIcon.png").toExternalForm());
            titleIcon.setImage(titleImg);
            dateIcon.setImage(dateImg);

            titleIcon.setFitWidth(20);
            titleIcon.setFitHeight(20);
            titleIcon.setPreserveRatio(false);

            dateIcon.setFitWidth(25);
            dateIcon.setFitHeight(25);
            dateIcon.setPreserveRatio(false);
        });
    }

    /**
     * 상세 달력 뷰에서 전달받은 DTO 데이터를 화면에 세팅합니다.
     *
     * @param dto 일정 정보 DTO
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
     * 외부에서 선택된 일정 리스트 항목의 인덱스를 전달받아 저장합니다.
     * 이후 내부적으로 일정 정보를 로드합니다.
     *
     * @param index 일정 리스트에서 선택된 항목의 인덱스
     */
    public void setScheduleIndex(int index) {
        this.scheduleIndex = index;
        loadScheduleContent();
    }

    /**
     * 인덱스를 기반으로 일정 상세 정보를 조회하여 화면에 표시합니다.
     * 일정 제목, 날짜, 내용을 순서대로 세팅합니다.
     */
    private void loadScheduleContent() {
        List<String> scheduleList =  scheduleService.getScheduleDetailsByEmpIdAndIndex(DashboardController.empId, scheduleIndex);

        title.setText(scheduleList.get(0));
        date.setText(scheduleList.get(1));
        content.setText(scheduleList.get(2));
    }
}
