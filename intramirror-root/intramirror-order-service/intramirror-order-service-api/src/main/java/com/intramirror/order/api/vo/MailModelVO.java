package com.intramirror.order.api.vo;

import java.util.List;

/**
 * Created by 123 on 2018/5/21.
 */
public class MailModelVO {

    private String host;
    private Integer port;
    private String userName;
    private String password;
    private String emailFrom;
    private String timeout;
    /**
     * 收件人邮箱，多个邮箱以“;”分隔
     */
    private String toEmails;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 邮件中的附件，为空时无附件。
     */
    private List<MailAttachmentVO> attachments;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getToEmails() {
        return toEmails;
    }

    public void setToEmails(String toEmails) {
        this.toEmails = toEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MailAttachmentVO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MailAttachmentVO> attachments) {
        this.attachments = attachments;
    }
}
