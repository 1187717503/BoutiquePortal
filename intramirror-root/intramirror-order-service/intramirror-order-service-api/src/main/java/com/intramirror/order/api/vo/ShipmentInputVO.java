package com.intramirror.order.api.vo;

/**
 * Created by caowei on 2018/12/14.
 */
public class ShipmentInputVO {

    private Long shipmentId;
    private String awbNo;

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    @Override
    public String toString() {
        return "ShipmentInputVO{" +
                "shipmentId=" + shipmentId +
                ", awbNo='" + awbNo + '\'' +
                '}';
    }
}
