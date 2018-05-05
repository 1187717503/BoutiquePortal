package com.intramirror.order.api.vo;


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

    private Integer type;

    private Integer shipmentType;

    private Integer isComplete;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(Integer shipmentType) {
        this.shipmentType = shipmentType;
    }

    public Integer getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Integer isComplete) {
        this.isComplete = isComplete;
    }

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
