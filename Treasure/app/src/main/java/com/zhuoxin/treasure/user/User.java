package com.zhuoxin.treasure.user;

/**
 * Created by Administrator on 2017/1/9.
 */

public class User {

    /**
     * UserName : qjd
     * Password : 654321
     */

    private String UserName;
    private String Password;

    public User(String userName, String password) {
        UserName = userName;
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
}
