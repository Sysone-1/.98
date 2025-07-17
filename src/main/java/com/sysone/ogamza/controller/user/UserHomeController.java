package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.view.user.CalendarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserHomeController implements Initializable {

    @FXML
    private AnchorPane calendarContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarView calendarView = new CalendarView();
        calendarContainer.getChildren().add(calendarView);

        // AnchorPane에 꽉 차게
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }
}
