package com.github.lany192.arouter.sample;

public class UserHelper {
    private volatile static UserHelper instance = null;

    private boolean login;

    private UserHelper() {
    }

    public static UserHelper get() {
        if (instance == null) {
            synchronized (UserHelper.class) {
                if (instance == null) {
                    instance = new UserHelper();
                }
            }
        }
        return instance;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
