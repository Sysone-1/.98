package com.sysone.ogamza;

import java.sql.SQLException;

public class LoginService {
    private final LoginDAO loginDAO = new LoginDAO();

    public LoginUserDTO login(String email, String password) throws SQLException {
        return loginDAO.getUserInfo(email, password);
    }
}
