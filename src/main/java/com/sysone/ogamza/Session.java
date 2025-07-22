package com.sysone.ogamza;

public class Session {
    public static Session instance;
    private LoginUserDTO loginUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    public void setLoginUser(LoginUserDTO loginUser) {
        this.loginUser = loginUser;
    }
    public LoginUserDTO getLoginUser() {
        return loginUser;
    }
    public void clear() {
        loginUser = null;
    }
}
