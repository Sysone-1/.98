package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dao.user.UserRecordDAO;
import com.sysone.ogamza.dto.user.UserRecordDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;


/**
 * 근태 기록 TableView 컨트롤러 (리팩터링 버전)
 */
public class RecordController {

    @FXML private TableView<UserRecordDTO> table;
    @FXML private TableColumn<UserRecordDTO, Integer> colId;
    @FXML private TableColumn<UserRecordDTO, String> colName;
    @FXML private TableColumn<UserRecordDTO, Date> colDate;
    @FXML private TableColumn<UserRecordDTO, String> colIn;
    @FXML private TableColumn<UserRecordDTO, String> colOut;
    @FXML private TableColumn<UserRecordDTO, String> colStatus;

    @FXML private Label cntPresent;
    @FXML private Label cntLate;
    @FXML private Label cntAbsent;
    @FXML private Label cntAll;

    private final ObservableList<UserRecordDTO> records = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("[RecordController] 로그인 정보 없음");
            return;
        }

        configureColumns();
        loadData(user.getId());
    }

    private void configureColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("workStatus"));

        colIn.setCellFactory(col -> createBlankTimeCell());
        colOut.setCellFactory(col -> createBlankTimeCell());

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText("");
                    setStyle("");
                    return;
                }
                setText(status);
                setStyle(switch (status) {
                    case "결근" -> "-fx-text-fill:#E03131; -fx-font-weight:bold;";
                    case "지각" -> "-fx-text-fill:#F08C00; -fx-font-weight:bold;";
                    default -> "-fx-text-fill:#2B8A3E; -fx-font-weight:bold;";
                });
            }
        });
    }

    private TableCell<UserRecordDTO, String> createBlankTimeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String time, boolean empty) {
                super.updateItem(time, empty);
                setText((empty || time == null || time.isBlank()) ? "" : time);
            }
        };
    }

    private void loadData(int userId) {
        try {
            records.setAll(UserRecordDAO.getInstance().getWorkingRecord(userId));
            table.setItems(records);
            table.getSortOrder().setAll(colDate);

            updateSummaryCounts();
        } catch (Exception e) {
            System.err.println("[RecordController] 데이터 로드 실패: " + e.getMessage());
        }
    }

    private void updateSummaryCounts() {
        long presentCount = records.stream().filter(r -> "출근".equals(r.getWorkStatus())).count();
        long lateCount = records.stream().filter(r -> "지각".equals(r.getWorkStatus())).count();
        long absentCount = records.stream().filter(r -> "결근".equals(r.getWorkStatus())).count();

        cntAll.setText(records.size() + "건");
        cntPresent.setText(presentCount + "일");
        cntLate.setText(lateCount + "일");
        cntAbsent.setText(absentCount + "일");
    }
}