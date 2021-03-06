package com.intramirror.web.VO;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/4/23.
 */
public class DHLInputVO {
    private String account;
    private String serviceType;
    private String shipmentTime;
    private Long shipmentDate;
    private BigDecimal customsValue;
    private String description;
    private ShipperVO shipper;
    private RecipientVO recipient;
    private List<Map<String,Object>> packageInfos;
    private String labelType;
    private String labelTemplate;
    private String specialPickupInstruction;
    private String pickupLocation;
    private String pickupType;


    public String getPickupType() {
        return pickupType;
    }

    public void setPickupType(String pickupType) {
        this.pickupType = pickupType;
    }

    public String getSpecialPickupInstruction() {
        return specialPickupInstruction;
    }

    public void setSpecialPickupInstruction(String specialPickupInstruction) {
        this.specialPickupInstruction = specialPickupInstruction;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Long getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Long shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getLabelTemplate() {
        return labelTemplate;
    }

    public void setLabelTemplate(String labelTemplate) {
        this.labelTemplate = labelTemplate;
    }

    public List<Map<String, Object>> getPackageInfos() {
        return packageInfos;
    }

    public void setPackageInfos(List<Map<String, Object>> packageInfos) {
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

    public String getShipmentTime() {
        return shipmentTime;
    }

    public void setShipmentTime(String shipmentTime) {
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
