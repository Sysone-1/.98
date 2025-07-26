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
 * 사용자 근태 기록 화면의 컨트롤러 클래스입니다.
 * 로그인된 사용자의 출퇴근 기록을 조회하여 테이블에 표시하고,
 * 출근/지각/결근 통계 데이터를 상단 요약 카드에 표시합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */

public class RecordController {

    /** 사용자 근태 기록을 표시할 테이블 */
    @FXML private TableView<UserRecordDTO> table;

    /** 사용자 ID, 이름, 근무일자, 출근/퇴근 시간, 상태를 보여주는 컬럼 */
    @FXML private TableColumn<UserRecordDTO, Integer> colId;
    @FXML private TableColumn<UserRecordDTO, String> colName;
    @FXML private TableColumn<UserRecordDTO, Date> colDate;
    @FXML private TableColumn<UserRecordDTO, String> colIn;
    @FXML private TableColumn<UserRecordDTO, String> colOut;
    @FXML private TableColumn<UserRecordDTO, String> colStatus;

    /** 출근/지각/결근/전체 건수를 표시하는 요약 레이블 */
    @FXML private Label cntPresent;
    @FXML private Label cntLate;
    @FXML private Label cntAbsent;
    @FXML private Label cntAll;

    /** 테이블에 표시될 근태 기록 리스트 */
    private final ObservableList<UserRecordDTO> records = FXCollections.observableArrayList();

    /** 초기화 메서드: 로그인 확인 → 컬럼 구성 → 데이터 로딩 → 테이블 높이 조정 */
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

    /** 테이블 컬럼 설정 및 셀 정렬/스타일 지정 */
    private void configureColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("workStatus"));

        colId.setCellFactory(col -> createCenterAlignedCellInt());
        colName.setCellFactory(col -> createCenterAlignedCell());
        colDate.setCellFactory(col -> createCenterAlignedDateCell());
        colIn.setCellFactory(col -> createBlankTimeCell());
        colOut.setCellFactory(col -> createBlankTimeCell());

        // 상태 컬럼: 상태별 색상과 스타일 적용
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

    /** 날짜 셀을 가운데 정렬하여 표시하는 TableCell 생성 */
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

    /** 문자열 셀을 가운데 정렬하여 표시하는 TableCell 생성 */
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

    /** 정수형 셀을 가운데 정렬하여 표시하는 TableCell 생성 */
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

    /** 출퇴근 시간이 비어 있을 경우 공백 처리하는 TableCell 생성 */
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

    /** 사용자 ID를 기반으로 출퇴근 기록 로딩 후 테이블에 적용 */
    private void loadData(int userId) {
        try {
            records.setAll(UserRecordDAO.getInstance().getWorkingRecord(userId));
            table.setItems(records);
            table.getSortOrder().setAll(colDate);  // 날짜 기준 정렬 적용
            updateSummaryCounts();
        } catch (Exception e) {
            System.err.println("[RecordController] 데이터 로드 실패: " + e.getMessage());
        }
    }

    /** 출근/지각/결근 건수 계산 및 상단 카드에 표시 */
    private void updateSummaryCounts() {
        long presentCount = records.stream().filter(r -> "출근".equals(r.getWorkStatus())).count();
        long lateCount = records.stream().filter(r -> "지각".equals(r.getWorkStatus())).count();
        long absentCount = records.stream().filter(r -> "결근".equals(r.getWorkStatus())).count();

        cntAll.setText(records.size() + "건");
        cntPresent.setText(presentCount + "일");
        cntLate.setText(lateCount + "일");
        cntAbsent.setText(absentCount + "일");
    }

    /** 테이블의 행 수에 맞춰 높이 자동 조정 */
    private void adjustTableHeightToRowCount() {
        int rowCount = table.getItems().size();
        table.setFixedCellSize(30);
        table.setPrefHeight(table.getFixedCellSize() * rowCount + 28);
    }
}
