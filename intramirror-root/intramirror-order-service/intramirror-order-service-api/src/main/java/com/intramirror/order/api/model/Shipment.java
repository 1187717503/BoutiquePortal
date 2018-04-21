/**
 * 
 */
package com.intramirror.order.api.model;

import java.util.Date;

/**
 * shipment Entry
 * @author yuan
 *
 */
public class Shipment {

	/**
	 * @Fields 装货ID
	 */
	private Long shipmentId;
	/**
	 * @Fields 地理区域
	 */
	private String shipToGeography;

	/**
	 * 发货地id
	 */
	private Integer stockLocationId;
	/**
	 * @Fields 状态
	 */
	private int status;
	/**
	 * @Fields 门店Id
	 */
	private Long vendorId;
	/**
	 * shipment分类 1.发往质检仓  2.发往用户
	 */
	private Integer shipmentCategory;
	/**
	 * @Fields 装货条码
	 */
	private String shipmentNo;
	/**
	 * @Fields 创建时间
	 */
	private Date createdAt;
	/**
	 * @Fields 修改时间
	 */
	private Date updatedAt;

	public Integer getStockLocationId() {
		return stockLocationId;
	}

	public void setStockLocationId(Integer stockLocationId) {
		this.stockLocationId = stockLocationId;
	}

	public Long getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}
	public String getShipToGeography() {
		return shipToGeography;
	}
	public void setShipToGeography(String shipToGeography) {
		this.shipToGeography = shipToGeography;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public Integer getShipmentCategory() {
		return shipmentCategory;
	}

	public void setShipmentCategory(Integer shipmentCategory) {
		this.shipmentCategory = shipmentCategory;
	}
}
