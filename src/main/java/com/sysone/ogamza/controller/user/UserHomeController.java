package com.sysone.ogamza.controller.user;

import com.sysone.ogamza.LoginUserDTO;
import com.sysone.ogamza.Session;
import com.sysone.ogamza.dao.user.RankingDAO;
import com.sysone.ogamza.dto.user.RankingDTO;
import com.sysone.ogamza.dto.user.UserInfoDTO;
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
import java.util.List;
import java.util.ResourceBundle;

public class UserHomeController implements Initializable {

    @FXML private AnchorPane calendarContainer;
    @FXML private Group shapeGroup;
    @FXML private ImageView employeeProfile;
    @FXML private Text employeeName;
    @FXML private Text departmentName;
    @FXML private Group luckyShape;
    @FXML private Text luckyNumber;
    @FXML private Text randomMsg;
    @FXML private Text emoji;
    @FXML private VBox todayMood;
    @FXML private Text rankingDept1;
    @FXML private Text rankingDept2;
    @FXML private Text rankingDept3;
    @FXML private Text rankingNum1;
    @FXML private Text rankingNum2;
    @FXML private Text rankingNum3;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        CalendarView calendarView = new CalendarView();
        calendarContainer.getChildren().add(calendarView);

        // AnchorPane에 꽉 차게
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);

        getHomeInfo(user.getId());
        emoji.setMouseTransparent(false); // 혹시라도 true로 되어 있으면
        emoji.setPickOnBounds(true); // 텍스트 바깥 여백도 클릭 가능하게
        new EmojiView(emoji, todayMood, selected -> {
            emoji.setText(selected);
            try{
                int response = EmojiDAO.getInstance().updateEmoji(user.getId(), selected);
                if(response == 0 ){
                    throw new RuntimeException("0행 업데이트되었습니다.");
                }
            }catch (Exception e){
                System.out.println("이모지 업데이트 실패 :: "+ e.getMessage());
            }
        });

        try{
        List<RankingDTO> rankingList = RankingDAO.getInstance().getRanking();
            rankingDept1.setText(rankingList.get(0).getDeptName());
            rankingNum1.setText(String.valueOf(rankingList.get(0).getRanking()));
            rankingDept2.setText(rankingList.get(1).getDeptName());
            rankingNum2.setText(String.valueOf(rankingList.get(1).getRanking()));
            rankingDept3.setText(rankingList.get(2).getDeptName());
            rankingNum3.setText(String.valueOf(rankingList.get(2).getRanking()));
        }catch (Exception e){
            System.out.println("랭킹 불러오기 실패 :: "+ e.getMessage());
        }
    }

    public void getHomeInfo(int userId){
        // 유저 정보 불러오기
        UserInfoDTO user = UserHomeService.getInstance().getUserHomeInfo(userId);
        //이미지 셋팅
        String defaultPath = "/images/eunwoo.png";
        String userPath = user.getProfile();
        URL imageUrl = getClass().getResource(userPath);
        if (imageUrl == null) {
            System.err.println("⚠프로필 이미지 없음: " + userPath + " → 기본 이미지로 대체");
            imageUrl = getClass().getResource(defaultPath);
        }

        if (imageUrl == null) {
            throw new RuntimeException(" 기본 이미지도 없음! " + defaultPath);
        }

        Image userProfile = new Image(imageUrl.toExternalForm());
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
