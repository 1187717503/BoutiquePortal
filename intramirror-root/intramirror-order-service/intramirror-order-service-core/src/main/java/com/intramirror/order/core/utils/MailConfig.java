package com.intramirror.order.core.utils;

/**
 * Created by 123 on 2018/5/22.
 */
public class MailConfig {
    public static String host;
    public static Integer port;
    public static String userName;
    public static String password;
    public static String emailFrom;
    public static String timeout;
    public static String emailTo;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        MailConfig.host = host;
    }

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        MailConfig.port = port;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        MailConfig.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MailConfig.password = password;
    }

    public static String getEmailFrom() {
        return emailFrom;
    }

    public static void setEmailFrom(String emailFrom) {
        MailConfig.emailFrom = emailFrom;
    }

    public static String getTimeout() {
        return timeout;
    }

    public static void setTimeout(String timeout) {
        MailConfig.timeout = timeout;
    }

    public static String getEmailTo() {
        return emailTo;
    }

    public static void setEmailTo(String emailTo) {
        MailConfig.emailTo = emailTo;
    }
}
