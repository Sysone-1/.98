package com.sysone.ogamza.service;

import com.sysone.ogamza.repository.LoginDAO;

import java.sql.SQLException;

public class LoginService {
    private final LoginDAO loginDAO = new LoginDAO();

    public boolean login(String email, String password) throws SQLException {
        return loginDAO.isValidUser(email, password);
    }
}
