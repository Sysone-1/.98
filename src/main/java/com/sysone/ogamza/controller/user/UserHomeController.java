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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 사용자 홈 화면을 초기화하고 각종 위젯을 유저 데이터로 설정하는 컨트롤러입니다.
 * 이모지 선택 기능, 오늘의 행운 정보 표시, 부서별 출근 랭킹 등을 담당합니다.
 *
 * @author 서샘이
 * @since 2025-07-25
 */
public class UserHomeController implements Initializable {

    @FXML private AnchorPane calendarContainer;
    @FXML private ImageView employeeProfile;
    @FXML private Text employeeName;
    @FXML private Text departmentName;
    @FXML private Group luckyShape;
    @FXML private Text luckyNumber;
    @FXML private Text randomMsg;
    @FXML private ImageView emojiView;
    @FXML private VBox todayMood;
    @FXML private Text rankingDept1;
    @FXML private Text rankingDept2;
    @FXML private Text rankingDept3;
    @FXML private Text rankingNum1;
    @FXML private Text rankingNum2;
    @FXML private Text rankingNum3;

    /**
     * FXML 초기화 메서드. 사용자 정보를 기반으로 화면을 구성하고 이벤트 리스너를 등록합니다.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoginUserDTO user = Session.getInstance().getLoginUser();
        if (user == null) {
            System.err.println("⚠️ 로그인 유저 정보 없음! 세션이 비어 있음");
            return;
        }

        // 캘린더 뷰 추가 및 AnchorPane에 꽉 차도록 설정
        CalendarView calendarView = new CalendarView();
        calendarContainer.getChildren().add(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);

        // 유저 정보 기반 화면 세팅
        getHomeInfo(user.getId());

        // 이모지 뷰 클릭 처리 설정
        todayMood.setMouseTransparent(false); // 마우스 이벤트 받도록 설정
        todayMood.setPickOnBounds(true); // 텍스트 영역 바깥도 클릭 가능하게 설정

        // 이모지 선택 뷰 초기화 및 콜백 처리
        new EmojiView(emojiView, todayMood, selected -> {
            try {
                String path = "/images/emoji/" + selected;
                Image img   = new Image(getClass().getResourceAsStream(path));
                emojiView.setImage(img);
                int updated = EmojiDAO.getInstance().updateEmoji(user.getId(), selected);
                if (updated == 0) throw new RuntimeException("0행 업데이트되었습니다.");
            } catch (Exception ex) {
                System.out.println("이모지 업데이트 실패 :: " + ex.getMessage());
            }
        });

        // 부서별 랭킹 세팅
        try {
            List<RankingDTO> rankingList = RankingDAO.getInstance().getRanking();
            rankingDept1.setText(rankingList.get(0).getDeptName());
            rankingNum1.setText(String.valueOf(rankingList.get(0).getRanking()));
            rankingDept2.setText(rankingList.get(1).getDeptName());
            rankingNum2.setText(String.valueOf(rankingList.get(1).getRanking()));
            rankingDept3.setText(rankingList.get(2).getDeptName());
            rankingNum3.setText(String.valueOf(rankingList.get(2).getRanking()));
        } catch (Exception e) {
            System.out.println("랭킹 불러오기 실패 :: "+ e.getMessage());
        }
    }

    /**
     * 로그인한 유저 ID를 기반으로 홈 화면 정보를 조회하고 UI에 설정합니다.
     *
     * @param userId 로그인한 유저의 ID
     */
    public void getHomeInfo(int userId) {
        UserInfoDTO user = UserHomeService.getInstance().getUserHomeInfo(userId);

        // 프로필 이미지 설정
        String defaultPath = "/images/eunwoo.png";
        String userPath = user.getProfile();
        URL imageUrl = getClass().getResource(userPath);
        if (imageUrl == null) {
            System.err.println("⚠프로필 이미지 없음: " + userPath + " → 기본 이미지로 대체");
            imageUrl = getClass().getResource(defaultPath);
        }
        Image userProfile = new Image(imageUrl.toExternalForm());
        employeeProfile.setImage(userProfile);

        // 텍스트 정보 설정
        employeeName.setText(user.getName());
        departmentName.setText(user.getDepartmentName());

        // 오늘의 행운 설정
        luckyNumber.setText(String.valueOf(user.getLuckyNumber()));
        Shape todayShape = UserShape.getShape(user.getLuckyShape());
        Color todayColor;
        try {
            todayColor = Color.web(user.getLuckyColor());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("⚠️ 잘못된 컬러 값입니다: " + user.getLuckyColor());
            todayColor = Color.RED;
        }
        if (todayShape == null) {
            System.err.println("⚠️ 알 수 없는 도형입니다: " + user.getLuckyShape());
            todayShape = new Circle(35);
        }
        todayShape.setFill(todayColor);
        luckyShape.getChildren().clear();
        luckyShape.getChildren().add(todayShape);

        // 랜덤 메시지 설정
        randomMsg.setText("\"" + user.getRandomMessage() + "\"");

        // 이모지 이미지 설정
        String path = "/images/emoji/" + user.getEmoji();
        Image img = new Image(getClass().getResourceAsStream(path));
        emojiView.setImage(img);
    }
}
