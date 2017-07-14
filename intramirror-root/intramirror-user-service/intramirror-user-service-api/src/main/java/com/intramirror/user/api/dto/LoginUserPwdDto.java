package com.intramirror.user.api.dto;

/**
 * 通过邮箱和密码登录
 *
 * @auth:mingfly
 */
public class LoginUserPwdDto {

    private String email;//用户手机号码

    private String password;//用户密码

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
