package com.intramirror.order.api.vo;

/**
 * Created by 123 on 2018/6/7.
 */
public class VendorInvoiceVO {
    private Long vendorId;
    private String vendorName;
    private Long shipmentId;
    private String invoiceUrl;
    private Long boutiqueShipmentId;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public Long getBoutiqueShipmentId() {
        return boutiqueShipmentId;
    }

    public void setBoutiqueShipmentId(Long boutiqueShipmentId) {
        this.boutiqueShipmentId = boutiqueShipmentId;
    }
}
