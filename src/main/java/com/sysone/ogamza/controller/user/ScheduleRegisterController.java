package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleContentDTO;
import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import com.sysone.ogamza.utils.api.alert.AlertCreate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 상신 등록 화면의 컨트롤러입니다.
 * <p>
 * 사용자가 일정 제목, 유형, 기간, 내용을 입력하여 상신을 요청할 수 있도록 합니다.
 * 입력 유효성 검사 및 처리 결과에 따라 알림을 제공합니다.
 *
 * @author 김민호
 */
public class ScheduleRegisterController {

    @FXML private TextField titleField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker  endDatePicker;
    @FXML private TextField contentField;
    @FXML private ComboBox<String> typeComboBox;

    private static final ScheduleService scheduleService = ScheduleService.getInstance();

    /**
     * 콤보박스를 초기화합니다.
     * 연장 근무, 휴일, 연차, 반차, 외근 등의 일정 유형을 추가합니다.
     */
    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("연장 근무", "휴일", "연차", "반차", "외근");
        typeComboBox.setValue("종류를 선택하세요.");
    }

    /**
     * 상신 버튼 클릭 시 실행되는 핸들러입니다.
     * 모든 입력 필드를 검사하고 유효할 경우 일정 등록 요청을 처리합니다.
     * 처리 결과에 따라 알림을 표시하고 창을 닫습니다.
     *
     * @param event 상신 버튼 클릭 이벤트
     */
    @FXML
    private void handleSubmitClick(ActionEvent event) {
        if (titleField.getText().isEmpty() ||
                typeComboBox.getValue() == null ||
                startDatePicker.getValue() == null ||
                endDatePicker.getValue() == null ||
                contentField.getText().isEmpty()) {

            AlertCreate.showAlert(Alert.AlertType.ERROR, "결재 상신", "모든 항목을 입력해주세요.");
            return;
        }

        List<ScheduleListDTO> list = scheduleService.getApprovedScheduleList(DashboardController.empId);

        for (ScheduleListDTO dto : list) {
            LocalDateTime startDate = dto.getStartDate();
            LocalDateTime endDate = dto.getEndDate();
            int isGranted = dto.getIsGranted();

            LocalDateTime newStart = startDatePicker.getValue().atStartOfDay();
            LocalDateTime newEnd = endDatePicker.getValue().atStartOfDay();

            boolean isOverlapping = !(newEnd.isBefore(startDate) || newStart.isAfter(endDate));
            if (isOverlapping && (isGranted == 0 || isGranted == 1)) {
                AlertCreate.showAlert(Alert.AlertType.ERROR, "결재 상신", "기존 일정과 겹치는 기간이 있습니다.");
                return;
            }
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "결재 상신", "마침 일정을 다시 선택해주세요.");
            return;
        }

        if ("반차".equals(typeComboBox.getValue()) && !startDatePicker.getValue().equals(endDatePicker.getValue())) {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "결재 상신", "반차는 시작일과 종료일이 같아야 합니다.");
            return;
        }

        ScheduleContentDTO scheduleListDto = new ScheduleContentDTO(
                DashboardController.empId,
                titleField.getText(),
                typeComboBox.getValue(),
                startDatePicker.getValue().atStartOfDay(),
                endDatePicker.getValue().atStartOfDay(),
                contentField.getText(),
                0
        );

        String text = scheduleService.createSchedule(scheduleListDto);

        if ("상신 완료".equals(text)) {
            AlertCreate.showAlert(Alert.AlertType.INFORMATION, "결재 상신", text);
        } else {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "결재 상신", text);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

