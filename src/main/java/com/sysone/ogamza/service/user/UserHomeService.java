package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dao.user.UserInfoDAO;
import com.sysone.ogamza.dao.user.UserHomeDAO;

public class UserHomeService {

    private static final UserHomeService instance = new UserHomeService();
    private UserHomeService(){}

    public static UserHomeService getInstance(){return instance;}


    public UserInfoDAO getUserHomeInfo(int userId){
        try {
            UserInfoDAO user = UserHomeDAO.getInstance().getUserHome(userId);
            if(user == null){
                throw new RuntimeException("사용자를 찾을 수 없습니다");
            }
            System.out.println(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
