package com.intramirror.user.api.model;

import java.util.Date;

public class PasswordMail {

    private Long password_mail_id;

    private String email;

    private String url;

    private Date create_date;

    private Integer is_change;

    public Long getPassword_mail_id() {
        return password_mail_id;
    }

    public void setPassword_mail_id(Long password_mail_id) {
        this.password_mail_id = password_mail_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Integer getIs_change() {
        return is_change;
    }

    public void setIs_change(Integer is_change) {
        this.is_change = is_change;
    }
}
