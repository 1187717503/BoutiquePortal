package com.intramirror.order.api.vo;

/**
 * Created by caowei on 2019/3/4.
 */
public class ConfirmOrderVO {
    private Long logisticsProductId;
    private String orderLineNum;
    private String stockLocation;
    private Long stockLocationId;
    private Boolean confirmFlag;

    public Boolean getConfirmFlag() {
        return confirmFlag;
    }

    public void setConfirmFlag(Boolean confirmFlag) {
        this.confirmFlag = confirmFlag;
    }

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

    public String getStockLocation() {
        return stockLocation;
    }

    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation;
    }

    public Long getStockLocationId() {
        return stockLocationId;
    }

    public void setStockLocationId(Long stockLocationId) {
        this.stockLocationId = stockLocationId;
    }
}
