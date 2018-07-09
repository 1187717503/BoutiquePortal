package com.intramirror.user.api.session;


import com.intramirror.user.api.session.dto.UserAuthInfo;

/**
 *
 * 存放用户登录信息
 *
 * @auth:mingfly
 */
public class AuthUserLocal {

    private static ThreadLocal<UserAuthInfo> authUserLocal=new ThreadLocal<UserAuthInfo>();

    public static UserAuthInfo getAuthUserId(){
        return authUserLocal.get();
    }

    public static void saveAuthUserId(UserAuthInfo userAuthInfo){
        authUserLocal.set(userAuthInfo);
    }

    public static void delAuthUser(){
        authUserLocal.remove();
    }
}
