package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dto.user.ScheduleListDTO;
import com.sysone.ogamza.service.user.ScheduleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

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
        취소 버튼 핸들러
    */
    @FXML
    private void handleRemove() {
        ScheduleListDTO selectedDto = scheduleTable.getSelectionModel().getSelectedItem();

        long scheduleId = selectedDto.getScheduleId();

        boolean success = scheduleService.removeScheduleById(DashboardController.empId, scheduleId);
    }

    /**
        닫기 버튼 핸들러
    */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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
