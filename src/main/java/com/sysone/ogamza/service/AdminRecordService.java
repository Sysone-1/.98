package com.sysone.ogamza.service;

import com.sysone.ogamza.dao.RecordDao;
import com.sysone.ogamza.entity.Record;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

/**
 * 출입로그 관리 서비스
 *
 * <p>관리자용 출입로그 관리 서비스 클래스입니다.
 * 데이터베이스에서 출입 기록을 조회하고, 관련 비즈니스 로직을 처리합니다.</p>
 *
 *  @author 조윤상
 */

public class AdminRecordService {
    private final RecordDao recordDao;

    /**
     * AdminRecordService의 새 인스턴스를 생성합니다.
     * 내부적으로 RecordDao를 초기화합니다.
     */
    public AdminRecordService() {
        this.recordDao = new RecordDao();
    }

    /**
     * 데이터베이스에서 모든 출입 기록을 로드하여 제공된 TableView에 표시합니다.
     *
     * @param masterData  출입 기록 데이터를 저장할 ObservableList
     * @param recordTable 출입 기록을 표시할 TableView 컨트롤
     */
    public void loadAllRecordsFromDB(ObservableList<Record> masterData, TableView<Record> recordTable) {
        masterData.clear();
        try {
            List<Record> records = recordDao.findAllRecords();
            masterData.addAll(records);
            recordTable.setItems(masterData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("DB 연결 오류", "데이터를 불러오는 중 문제가 발생했습니다.");
        }
    }

    /**
     * 사용자에게 오류 메시지를 표시하는 경고창을 띄웁니다.
     *
     * @param title 경고창의 제목
     * @param msg   경고창에 표시될 메시지 내용
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}