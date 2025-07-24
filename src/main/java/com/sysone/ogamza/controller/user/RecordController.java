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
        adjustTableHeightToRowCount();
    }

    private void configureColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("workStatus"));

        // 가운데 정렬 적용
        colId.setCellFactory(col -> createCenterAlignedCellInt());
        colName.setCellFactory(col -> createCenterAlignedCell());
        colDate.setCellFactory(col -> createCenterAlignedDateCell());
        colIn.setCellFactory(col -> createBlankTimeCell());
        colOut.setCellFactory(col -> createBlankTimeCell());

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText("");
                    setStyle("-fx-alignment: CENTER;");
                    return;
                }
                setText(status);
                setStyle(switch (status) {
                    case "결근" -> "-fx-text-fill:#E03131; -fx-font-weight:bold; -fx-alignment: CENTER;";
                    case "지각" -> "-fx-text-fill:#F08C00; -fx-font-weight:bold; -fx-alignment: CENTER;";
                    default -> "-fx-text-fill:#2B8A3E; -fx-font-weight:bold; -fx-alignment: CENTER;";
                });
            }
        });
    }

    private TableCell<UserRecordDTO, Date> createCenterAlignedDateCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.toString());
                setStyle("-fx-alignment: CENTER;");
            }
        };
    }

    private TableCell<UserRecordDTO, String> createCenterAlignedCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
                setStyle("-fx-alignment: CENTER;");
            }
        };
    }

    private TableCell<UserRecordDTO, Integer> createCenterAlignedCellInt() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.valueOf(item));
                setStyle("-fx-alignment: CENTER;");
            }
        };
    }

    private TableCell<UserRecordDTO, String> createBlankTimeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String time, boolean empty) {
                super.updateItem(time, empty);
                setText((empty || time == null || time.isBlank()) ? "" : time);
                setStyle("-fx-alignment: CENTER;");
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

    private void adjustTableHeightToRowCount() {
        int rowCount = table.getItems().size();
        table.setFixedCellSize(30);
        table.setPrefHeight(table.getFixedCellSize() * rowCount + 28);
    }
}
