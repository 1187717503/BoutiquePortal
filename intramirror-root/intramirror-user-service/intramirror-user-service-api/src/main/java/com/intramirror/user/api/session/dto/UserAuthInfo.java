package com.intramirror.user.api.session.dto;

import java.io.Serializable;

/**
 * 用户认证
 *
 * @auth:mingfly
 */
public class UserAuthInfo implements Serializable {

    private static final long serialVersionUID = -6580382143045305121L;
    private Long userId;
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
