package com.intramirror.order.api.model;

import java.util.Date;

/**
 * database table is vendor_shipment
 * @mbg.generated do_not_delete_during_merge
 */
public class VendorShipment {
    /**
     * 主键
     * database column is vendor_shipment.id
     * @mbg.generated
     */
    private Long id;

    /**
     * vendor id
     * database column is vendor_shipment.vendor_id
     * @mbg.generated
     */
    private Long vendorId;

    /**
     * shipment id
     * database column is vendor_shipment.shipment_id
     * @mbg.generated
     */
    private Long shipmentId;

    /**
     * CDE文件所在路径
     * database column is vendor_shipment.cde_url
     * @mbg.generated
     */
    private String cdeUrl;

    /**
     * invoice文件所在路径
     * database column is vendor_shipment.invoice_url
     * @mbg.generated
     */
    private String invoiceUrl;

    /**
     * 发票申请状态：0 未发送，1 已发送申请，2 申请通过，3 申请拒绝
     * database column is vendor_shipment.invoice_status
     * @mbg.generated
     */
    private Integer invoiceStatus;

    /**
     * database column is vendor_shipment.version
     * @mbg.generated
     */
    private Integer version;

    /**
     * 创建时间
     * database column is vendor_shipment.create_time
     * @mbg.generated
     */
    private Date createTime;

    /**
     * 更新时间
     * database column is vendor_shipment.update_time
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * 是否删除 0：未删除 1：已删除
     * database column is vendor_shipment.is_deleted
     * @mbg.generated
     */
    private Integer isDeleted;

    /**
     * @return the value of vendor_shipment.id
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the value for vendor_shipment.id
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the value of vendor_shipment.vendor_id
     * @mbg.generated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * @param vendorId the value for vendor_shipment.vendor_id
     * @mbg.generated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * @return the value of vendor_shipment.shipment_id
     * @mbg.generated
     */
    public Long getShipmentId() {
        return shipmentId;
    }

    /**
     * @param shipmentId the value for vendor_shipment.shipment_id
     * @mbg.generated
     */
    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    /**
     * @return the value of vendor_shipment.cde_url
     * @mbg.generated
     */
    public String getCdeUrl() {
        return cdeUrl;
    }

    /**
     * @param cdeUrl the value for vendor_shipment.cde_url
     * @mbg.generated
     */
    public void setCdeUrl(String cdeUrl) {
        this.cdeUrl = cdeUrl == null ? null : cdeUrl.trim();
    }

    /**
     * @return the value of vendor_shipment.invoice_url
     * @mbg.generated
     */
    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    /**
     * @param invoiceUrl the value for vendor_shipment.invoice_url
     * @mbg.generated
     */
    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl == null ? null : invoiceUrl.trim();
    }

    /**
     * @return the value of vendor_shipment.invoice_status
     * @mbg.generated
     */
    public Integer getInvoiceStatus() {
        return invoiceStatus;
    }

    /**
     * @param invoiceStatus the value for vendor_shipment.invoice_status
     * @mbg.generated
     */
    public void setInvoiceStatus(Integer invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    /**
     * @return the value of vendor_shipment.version
     * @mbg.generated
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the value for vendor_shipment.version
     * @mbg.generated
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the value of vendor_shipment.create_time
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the value for vendor_shipment.create_time
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the value of vendor_shipment.update_time
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the value for vendor_shipment.update_time
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the value of vendor_shipment.is_deleted
     * @mbg.generated
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the value for vendor_shipment.is_deleted
     * @mbg.generated
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}