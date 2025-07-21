package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.dao.user.UserInfoDAO;
import com.sysone.ogamza.dao.user.EmojiDAO;
import com.sysone.ogamza.service.user.UserHomeService;
import com.sysone.ogamza.view.user.CalendarView;
import com.sysone.ogamza.view.user.EmojiView;
import com.sysone.ogamza.view.user.UserShape;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox todayMood;


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
        emoji.setMouseTransparent(false); // 혹시라도 true로 되어 있으면
        emoji.setPickOnBounds(true); // 텍스트 바깥 여백도 클릭 가능하게
        new EmojiView(emoji, todayMood, selected -> {
            emoji.setText(selected);
            try{
                int response = EmojiDAO.getInstance().updateEmoji(1009, selected);
                if(response == 0 ){
                    throw new RuntimeException("0행 업데이트되었습니다.");
                }
            }catch (Exception e){
                System.out.println("이모지 업데이트 실패"+ e.getMessage());
            }
        });
    }

    public void getHomeInfo(int userId){
        // 유저 정보 불러오기
        UserInfoDAO user = UserHomeService.getInstance().getUserHomeInfo(userId);
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
