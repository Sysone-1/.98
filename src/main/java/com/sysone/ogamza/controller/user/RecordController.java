package com.sysone.ogamza.controller.user;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RecordController {

    @FXML private TableColumn<?, ?> id;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> date;
    @FXML private TableColumn<?, ?> inTime;
    @FXML private TableColumn<?, ?> outTIme;
    @FXML private TableColumn<?, ?> status;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            applyBoldHeader(id);
            applyBoldHeader(name);
            applyBoldHeader(date);
            applyBoldHeader(inTime);
            applyBoldHeader(outTIme);
            applyBoldHeader(status);
        });
    }

    private void applyBoldHeader(TableColumn<?, ?> column) {
        Text headerText = new Text(column.getText());
        headerText.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 20));
        column.setText(null); // 기본 text 제거
        column.setGraphic(headerText); // 새 텍스트로 대체
    }
}