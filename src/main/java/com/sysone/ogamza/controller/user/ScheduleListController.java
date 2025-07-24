package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import com.sysone.ogamza.utils.api.alert.AlertCreate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ScheduleListController {

    @FXML private TableView<ScheduleListDTO> scheduleTable;
    @FXML private TableColumn<ScheduleListDTO, String> titleColumn, typeColumn, dateColumn, approvalColumn, grantedStatusColumn;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();

    /**
        결재 내역 조회
    */
    public void loadScheduleList() {
        List<ScheduleListDTO> resultList = scheduleService.getScheduleList(DashboardController.empId);
        ObservableList<ScheduleListDTO> observableList = FXCollections.observableArrayList();

        observableList.addAll(resultList);

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScheduleType()));
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatDateRange(cell.getValue())));
        approvalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApprovalDate().toLocalDate().toString()));
        grantedStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(mapStatus(cell.getValue().getIsGranted())));

        scheduleTable.setItems(observableList);
    }

    /**
        결재 상신 취소 버튼 핸들러
    */
    @FXML
    private void handleRemove() {
        ScheduleListDTO selectedDto = scheduleTable.getSelectionModel().getSelectedItem();
        if (selectedDto == null) {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "상세 조회", "상신 취소할 대상을 선택해 주세요.");
            return;
        }

        long scheduleId = selectedDto.getScheduleId();

        if (selectedDto.getIsGranted() != 0) {
            AlertCreate.showAlert(Alert.AlertType.INFORMATION, "상세 조회", "상신 취소 대상이 아닙니다.");
            return;
        }

        boolean success = scheduleService.removeScheduleById(DashboardController.empId, scheduleId);

        if (success) {
            AlertCreate.showAlert(Alert.AlertType.INFORMATION, "상세 조회", "상신 취소 되었습니다.");
        } else {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "상세 조회", "다시 시도해주세요.");
        }
    }

    /**
        날짜 포맷팅
     */
    private String formatDateRange(ScheduleListDTO dto) {
        return String.format("%d-%02d-%02d ~ %d-%02d-%02d",
                dto.getStartDate().getYear(), dto.getStartDate().getMonthValue(), dto.getStartDate().getDayOfMonth(),
                dto.getEndDate().getYear(), dto.getEndDate().getMonthValue(), dto.getEndDate().getDayOfMonth());
    }

    /**
        상태코드 → 텍스트 변환
     */
    private String mapStatus(int status) {
        String text = "승인 대기";
        switch (status) {
            case 1: text = "승인 완료"; break;
            case 2: text = "승인 거절"; break;
            case 3: text = "상신 취소"; break;
        };
        return text;
    }
}
