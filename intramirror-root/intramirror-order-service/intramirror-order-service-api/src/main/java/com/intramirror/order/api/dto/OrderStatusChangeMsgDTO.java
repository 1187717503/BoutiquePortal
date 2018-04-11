package com.intramirror.order.api.dto;

import java.util.Date;

/**
 * Created by zhongyu on 2018/4/10.
 */
public class OrderStatusChangeMsgDTO {
    private Long logisticsProductId;
    private String orderLineNum;
    private Long parentOrderId;
    private String parentOrderNum;
    private Integer status;
    private Long userId;
    private Date triggerTime;

    public Long getLogisticsProductId() {
        return logisticsProductId;
    }

    public void setLogisticsProductId(Long logisticsProductId) {
        this.logisticsProductId = logisticsProductId;
    }

    public String getOrderLineNum() {
        return orderLineNum;
    }

    public void setOrderLineNum(String orderLineNum) {
        this.orderLineNum = orderLineNum;
    }

    public Long getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(Long parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getParentOrderNum() {
        return parentOrderNum;
    }

    public void setParentOrderNum(String parentOrderNum) {
        this.parentOrderNum = parentOrderNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    @Override
    public String toString() {
        return "OrderStatusChangeMsgDTO{" +
                "logisticsProductId=" + logisticsProductId +
                ", orderLineNum='" + orderLineNum + '\'' +
                ", parentOrderId=" + parentOrderId +
                ", parentOrderNum='" + parentOrderNum + '\'' +
                ", status=" + status +
                ", userId=" + userId +
                ", triggerTime=" + triggerTime +
                '}';
    }
}
