package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dao.user.UserRecordDAO;
import com.sysone.ogamza.dto.user.UserRecordDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;

public class RecordController {
    @FXML private TableView<UserRecordDTO> recordTable;
    @FXML private TableColumn<UserRecordDTO, Integer> id;
    @FXML private TableColumn<UserRecordDTO, String> name;
    @FXML private TableColumn<UserRecordDTO, LocalDate> date;
    @FXML private TableColumn<UserRecordDTO, String> inTime;
    @FXML private TableColumn<UserRecordDTO, String> outTime;
    @FXML private TableColumn<UserRecordDTO, String> status;


    @FXML
    public void initialize(){
        id.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        date.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        inTime.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));
        outTime.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));
        status.setCellValueFactory(new PropertyValueFactory<>("workStatus"));

        ObservableList<UserRecordDTO> records = FXCollections.observableArrayList();

        try {
        List<UserRecordDTO> datas = UserRecordDAO.getInstance().getWorkingRecord(1002);
            for(UserRecordDTO data : datas ){
                records.add(data);
            }
            recordTable.setItems(records);
        }catch (Exception e){
            System.out.println("사용자 데이터를 불러오는데 실패하였습니다."+ e.getMessage());
        }
            inTime.setCellFactory(column -> new TableCell<UserRecordDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item == null || item.isBlank()) {
                        setText("결근");
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setText(item);
                        setStyle("");
                    }
                }
            });

        outTime.setCellFactory(column -> new TableCell<UserRecordDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else if (item == null || item.isBlank()) {
                    setText("결근");
                    setStyle("-fx-text-fill: red;");
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });

        }
}