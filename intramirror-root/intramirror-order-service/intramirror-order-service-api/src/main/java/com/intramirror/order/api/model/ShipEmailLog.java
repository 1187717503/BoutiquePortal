package com.intramirror.order.api.model;

import java.util.Date;

/**
 * database table is ship_email_log
 * @mbg.generated do_not_delete_during_merge
 */
public class ShipEmailLog {
    /**
     * database column is ship_email_log.id
     * @mbg.generated
     */
    private Long id;

    /**
     * shipment编号
     * database column is ship_email_log.shipment_no
     * @mbg.generated
     */
    private String shipmentNo;

    /**
     * email推送报文信息
     * database column is ship_email_log.email_body
     * @mbg.generated
     */
    private String emailBody;

    /**
     * 错误信息
     * database column is ship_email_log.error_message
     * @mbg.generated
     */
    private String errorMessage;

    /**
     * database column is ship_email_log.create_time
     * @mbg.generated
     */
    private Date createTime;

    /**
     * database column is ship_email_log.update_time
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * 是否删除  1. 是 0.不是
     * database column is ship_email_log.is_deleted
     * @mbg.generated
     */
    private Integer isDeleted;

    /**
     * @return the value of ship_email_log.id
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the value for ship_email_log.id
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the value of ship_email_log.shipment_no
     * @mbg.generated
     */
    public String getShipmentNo() {
        return shipmentNo;
    }

    /**
     * @param shipmentNo the value for ship_email_log.shipment_no
     * @mbg.generated
     */
    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo == null ? null : shipmentNo.trim();
    }

    /**
     * @return the value of ship_email_log.email_body
     * @mbg.generated
     */
    public String getEmailBody() {
        return emailBody;
    }

    /**
     * @param emailBody the value for ship_email_log.email_body
     * @mbg.generated
     */
    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody == null ? null : emailBody.trim();
    }

    /**
     * @return the value of ship_email_log.error_message
     * @mbg.generated
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the value for ship_email_log.error_message
     * @mbg.generated
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    /**
     * @return the value of ship_email_log.create_time
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the value for ship_email_log.create_time
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the value of ship_email_log.update_time
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the value for ship_email_log.update_time
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the value of ship_email_log.is_deleted
     * @mbg.generated
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the value for ship_email_log.is_deleted
     * @mbg.generated
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}