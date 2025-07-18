package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.model.user.UserInfo;
import com.sysone.ogamza.service.user.FortuneService;
import com.sysone.ogamza.service.user.UserHomeService;
import com.sysone.ogamza.view.user.CalendarView;
import com.sysone.ogamza.view.user.UserShape;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class UserHomeController implements Initializable {

    @FXML
    private AnchorPane calendarContainer;
    @FXML
    private Group shapeGroup;
    @FXML
    private ImageView employeeProfile;
    @FXML
    private Text employeeName;
    @FXML
    private Text departmentName;
    @FXML
    private Group luckyShape;
    @FXML
    private Text luckyNumber;
    @FXML
    private Text randomMsg;
    @FXML
    private Text emoji;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarView calendarView = new CalendarView();
        calendarContainer.getChildren().add(calendarView);

        // AnchorPane에 꽉 차게
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);

        getHomeInfo(1009);
    }

    public void getHomeInfo(int userId){
        // 유저 정보 불러오기
        UserInfo user = UserHomeService.getInstance().getUserHomeInfo(userId);
        //이미지 셋팅
        Image userProfile = new Image(getClass().getResource(user.getProfile()).toExternalForm());
        employeeProfile.setImage(userProfile);

        // 이름 / 부서 설정
        employeeName.setText(user.getName());
        departmentName.setText(user.getDepartmentName());

        // today lucky setting
        luckyNumber.setText(String.valueOf(user.getLuckyNumber()));
        Shape todayShape = UserShape.getShape(user.getLuckyShape());
        Color todayColor = Color.web(user.getLuckyColor());
            // painting
        todayShape.setFill(todayColor);
            // clear old one and create new one
        luckyShape.getChildren().clear();
        luckyShape.getChildren().add(todayShape);

        // random message
        randomMsg.setText(user.getRandomMessage());

        // emoji
        emoji.setText(user.getEmoji());
    }


}
