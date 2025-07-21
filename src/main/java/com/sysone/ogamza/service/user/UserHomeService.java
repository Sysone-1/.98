package com.sysone.ogamza.service.user;

import com.sysone.ogamza.model.user.UserInfo;
import com.sysone.ogamza.repository.user.UserHomeDAO;

public class UserHomeService {

    private static final UserHomeService instance = new UserHomeService();
    private UserHomeService(){}

    public static UserHomeService getInstance(){return instance;}


    public UserInfo getUserHomeInfo(int userId){
        try {
            UserInfo user = UserHomeDAO.getInstance().getUserHome(userId);
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
