package com.intramirror.order.api.model;

import java.util.Date;

/**
 * database table is logistic_product_container
 * @mbg.generated do_not_delete_during_merge
 */
public class LogisticProductContainer {
    /**
     * 主键
     * database column is logistic_product_container.id
     * @mbg.generated
     */
    private Long id;

    /**
     * carton id
     * database column is logistic_product_container.container_id
     * @mbg.generated
     */
    private Long containerId;

    /**
     * shipment  id
     * database column is logistic_product_container.shipment_id
     * @mbg.generated
     */
    private Long shipmentId;

    /**
     * logistics product id
     * database column is logistic_product_container.logistics_product_id
     * @mbg.generated
     */
    private Long logisticsProductId;

    /**
     * 订单编号
     * database column is logistic_product_container.order_line_num
     * @mbg.generated
     */
    private String orderLineNum;

    /**
     * 订单所属vendor id
     * database column is logistic_product_container.vendor_id
     * @mbg.generated
     */
    private Long vendorId;

    /**
     * 收货状态：1 待收货，2 已收货
     * database column is logistic_product_container.recieve_status
     * @mbg.generated
     */
    private Integer recieveStatus;

    /**
     * database column is logistic_product_container.version
     * @mbg.generated
     */
    private Integer version;

    /**
     * 创建时间
     * database column is logistic_product_container.create_time
     * @mbg.generated
     */
    private Date createTime;

    /**
     * 更新时间
     * database column is logistic_product_container.update_time
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * 是否删除 0：未删除 1：已删除
     * database column is logistic_product_container.is_deleted
     * @mbg.generated
     */
    private Integer isDeleted;

    /**
     * @return the value of logistic_product_container.id
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the value for logistic_product_container.id
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the value of logistic_product_container.container_id
     * @mbg.generated
     */
    public Long getContainerId() {
        return containerId;
    }

    /**
     * @param containerId the value for logistic_product_container.container_id
     * @mbg.generated
     */
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    /**
     * @return the value of logistic_product_container.shipment_id
     * @mbg.generated
     */
    public Long getShipmentId() {
        return shipmentId;
    }

    /**
     * @param shipmentId the value for logistic_product_container.shipment_id
     * @mbg.generated
     */
    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    /**
     * @return the value of logistic_product_container.logistics_product_id
     * @mbg.generated
     */
    public Long getLogisticsProductId() {
        return logisticsProductId;
    }

    /**
     * @param logisticsProductId the value for logistic_product_container.logistics_product_id
     * @mbg.generated
     */
    public void setLogisticsProductId(Long logisticsProductId) {
        this.logisticsProductId = logisticsProductId;
    }

    /**
     * @return the value of logistic_product_container.order_line_num
     * @mbg.generated
     */
    public String getOrderLineNum() {
        return orderLineNum;
    }

    /**
     * @param orderLineNum the value for logistic_product_container.order_line_num
     * @mbg.generated
     */
    public void setOrderLineNum(String orderLineNum) {
        this.orderLineNum = orderLineNum == null ? null : orderLineNum.trim();
    }

    /**
     * @return the value of logistic_product_container.vendor_id
     * @mbg.generated
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * @param vendorId the value for logistic_product_container.vendor_id
     * @mbg.generated
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * @return the value of logistic_product_container.recieve_status
     * @mbg.generated
     */
    public Integer getRecieveStatus() {
        return recieveStatus;
    }

    /**
     * @param recieveStatus the value for logistic_product_container.recieve_status
     * @mbg.generated
     */
    public void setRecieveStatus(Integer recieveStatus) {
        this.recieveStatus = recieveStatus;
    }

    /**
     * @return the value of logistic_product_container.version
     * @mbg.generated
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the value for logistic_product_container.version
     * @mbg.generated
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the value of logistic_product_container.create_time
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the value for logistic_product_container.create_time
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the value of logistic_product_container.update_time
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the value for logistic_product_container.update_time
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the value of logistic_product_container.is_deleted
     * @mbg.generated
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the value for logistic_product_container.is_deleted
     * @mbg.generated
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}