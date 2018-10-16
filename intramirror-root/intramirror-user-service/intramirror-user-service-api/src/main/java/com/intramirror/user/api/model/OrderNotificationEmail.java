package com.intramirror.user.api.model;

import java.util.Date;

public class OrderNotificationEmail {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.order_notification_email_id
     *
     * @mbggenerated
     */
    private Long orderNotificationEmailId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.vendor_id
     *
     * @mbggenerated
     */
    private Long vendorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.email
     *
     * @mbggenerated
     */
    private String email;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.order_time
     *
     * @mbggenerated
     */
    private String orderTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.time_zone
     *
     * @mbggenerated
     */
    private String timeZone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.email_content
     *
     * @mbggenerated
     */
    private String emailContent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.created_at
     *
     * @mbggenerated
     */
    private Date createdAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.updated_at
     *
     * @mbggenerated
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_notification_email.enabled
     *
     * @mbggenerated
     */
    private Boolean enabled;
    private String emailContentStr;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.order_notification_email_id
     *
     * @return the value of order_notification_email.order_notification_email_id
     *
     * @mbggenerated
     */
    public Long getOrderNotificationEmailId() {
        return orderNotificationEmailId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.order_notification_email_id
     *
     * @param orderNotificationEmailId the value for order_notification_email.order_notification_email_id
     *
     * @mbggenerated
     */
    public void setOrderNotificationEmailId(Long orderNotificationEmailId) {
        this.orderNotificationEmailId = orderNotificationEmailId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.vendor_id
     *
     * @return the value of order_notification_email.vendor_id
     *
     * @mbggenerated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.vendor_id
     *
     * @param vendorId the value for order_notification_email.vendor_id
     *
     * @mbggenerated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.email
     *
     * @return the value of order_notification_email.email
     *
     * @mbggenerated
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.email
     *
     * @param email the value for order_notification_email.email
     *
     * @mbggenerated
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.order_time
     *
     * @return the value of order_notification_email.order_time
     *
     * @mbggenerated
     */
    public String getOrderTime() {
        return orderTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.order_time
     *
     * @param orderTime the value for order_notification_email.order_time
     *
     * @mbggenerated
     */
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime == null ? null : orderTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.time_zone
     *
     * @return the value of order_notification_email.time_zone
     *
     * @mbggenerated
     */
    public String getTimeZone() {
        return timeZone;
    }

    public String getEmailContentStr() {
        return emailContentStr;
    }

    public void setEmailContentStr(String emailContentStr) {
        this.emailContentStr = emailContentStr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.time_zone
     *
     * @param timeZone the value for order_notification_email.time_zone
     *
     * @mbggenerated
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone == null ? null : timeZone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.email_content
     *
     * @return the value of order_notification_email.email_content
     *
     * @mbggenerated
     */
    public String getEmailContent() {
        return emailContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.email_content
     *
     * @param emailContent the value for order_notification_email.email_content
     *
     * @mbggenerated
     */
    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent == null ? null : emailContent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.created_at
     *
     * @return the value of order_notification_email.created_at
     *
     * @mbggenerated
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.created_at
     *
     * @param createdAt the value for order_notification_email.created_at
     *
     * @mbggenerated
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.updated_at
     *
     * @return the value of order_notification_email.updated_at
     *
     * @mbggenerated
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.updated_at
     *
     * @param updatedAt the value for order_notification_email.updated_at
     *
     * @mbggenerated
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_notification_email.enabled
     *
     * @return the value of order_notification_email.enabled
     *
     * @mbggenerated
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_notification_email.enabled
     *
     * @param enabled the value for order_notification_email.enabled
     *
     * @mbggenerated
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}