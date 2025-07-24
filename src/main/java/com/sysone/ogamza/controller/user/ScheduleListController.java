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

/**
 * 사용자 결재 내역 리스트를 표시하고 상신 취소 기능을 제공하는 컨트롤러입니다.
 * <p>
 * TableView를 통해 일정 목록을 조회하고, 선택 항목에 대한 상신 취소 처리도 수행할 수 있습니다.
 *
 * @author 김민호
 */
public class ScheduleListController {

    @FXML private TableView<ScheduleListDTO> scheduleTable;
    @FXML private TableColumn<ScheduleListDTO, String> titleColumn, typeColumn, dateColumn, approvalColumn, grantedStatusColumn;
    private static final ScheduleService scheduleService = ScheduleService.getInstance();

    /**
     * 결재 내역 리스트를 조회하여 TableView에 출력합니다.
     * 각 컬럼에 대한 셀 데이터 팩토리를 설정하고, 데이터를 바인딩합니다.
     */
    public void loadScheduleList() {
        List<ScheduleListDTO> resultList = scheduleService.getApprovedScheduleList(DashboardController.empId);
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
     * 결재 상신 취소 버튼 클릭 시 실행되는 이벤트 핸들러입니다.
     * 선택된 일정이 있고 승인되지 않은 경우에만 상신을 취소합니다.
     */
    @FXML
    private void handleCancleClick() {
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

        boolean success = scheduleService.cancelScheduleRequestById(DashboardController.empId, scheduleId);

        if (success) {
            AlertCreate.showAlert(Alert.AlertType.INFORMATION, "상세 조회", "상신 취소 되었습니다.");
            loadScheduleList();
        } else {
            AlertCreate.showAlert(Alert.AlertType.ERROR, "상세 조회", "다시 시도해주세요.");
        }
    }

    /**
     * 일정 DTO의 시작일과 종료일을 포맷팅하여 문자열로 반환합니다.
     *
     * @param dto 일정 DTO
     * @return yyyy-MM-dd ~ yyyy-MM-dd 형식의 문자열
     */
    private String formatDateRange(ScheduleListDTO dto) {
        return String.format("%d-%02d-%02d ~ %d-%02d-%02d",
                dto.getStartDate().getYear(), dto.getStartDate().getMonthValue(), dto.getStartDate().getDayOfMonth(),
                dto.getEndDate().getYear(), dto.getEndDate().getMonthValue(), dto.getEndDate().getDayOfMonth());
    }

    /**
     * 상태 코드에 따라 상태 메시지를 변환합니다.
     *
     * @param status 상태 코드 (0: 승인대기, 1: 승인완료, 2: 거절, 3: 취소)
     * @return 상태 메시지 문자열
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
