package com.sysone.ogamza.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UnauthorizedAccessLogController {

    @FXML
    private ListView<String> logListView;

    public void setLogData(List<LocalDateTime> logTimes) {
        ObservableList<String> items = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (LocalDateTime time : logTimes) {
            items.add(time.format(formatter));
        }
        logListView.setItems(items);
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) logListView.getScene().getWindow();
        stage.close();
    }
}
