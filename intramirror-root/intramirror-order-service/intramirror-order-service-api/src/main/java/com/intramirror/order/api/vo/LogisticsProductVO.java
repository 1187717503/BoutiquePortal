package com.intramirror.order.api.vo;

import java.util.Date;

/**
 * Created by caowei on 2018/5/5.
 */
public class LogisticsProductVO {

    private String logisticsProductId;

    private String orderLineNum;

    private String orderNum;

    private Long orderId;

    private Long userId;

    private String awb;

    private Date time;

    private Integer type;

    private Integer shipmentType;

    private Integer isComplete;

    //private

    public String getLogisticsProductId() {
        return logisticsProductId;
    }

    public void setLogisticsProductId(String logisticsProductId) {
        this.logisticsProductId = logisticsProductId;
    }

    public String getOrderLineNum() {
        return orderLineNum;
    }

    public void setOrderLineNum(String orderLineNum) {
        this.orderLineNum = orderLineNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }
}
