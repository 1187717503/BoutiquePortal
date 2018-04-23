package com.intramirror.web.VO;

import com.intramirror.order.api.model.Container;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caowei on 2018/4/23.
 */
public class DHLInputVO {
    private String account;
    private String serviceType;
    private Long shipmentTime;
    private BigDecimal customsValue;
    private String description;
    private ShipperVO shipper;
    private RecipientVO recipient;
    private List<Container> packageInfos;

    public List<Container> getPackageInfos() {
        return packageInfos;
    }

    public void setPackageInfos(List<Container> packageInfos) {
        this.packageInfos = packageInfos;
    }

    public BigDecimal getCustomsValue() {
        return customsValue;
    }

    public void setCustomsValue(BigDecimal customsValue) {
        this.customsValue = customsValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getShipmentTime() {
        return shipmentTime;
    }

    public void setShipmentTime(Long shipmentTime) {
        this.shipmentTime = shipmentTime;
    }

    public ShipperVO getShipper() {
        return shipper;
    }

    public void setShipper(ShipperVO shipper) {
        this.shipper = shipper;
    }

    public RecipientVO getRecipient() {
        return recipient;
    }

    public void setRecipient(RecipientVO recipient) {
        this.recipient = recipient;
    }
}
