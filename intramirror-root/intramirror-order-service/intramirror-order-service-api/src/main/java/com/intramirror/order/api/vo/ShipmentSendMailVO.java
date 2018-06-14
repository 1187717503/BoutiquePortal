package com.intramirror.order.api.vo;

/**
 * Created by 123 on 2018/5/22.
 */
public class ShipmentSendMailVO {
    private String shipmentNo;
    private String destination;

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
