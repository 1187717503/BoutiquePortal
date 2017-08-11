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
	 * @Fields 状态
	 */
	private int status;
	/**
	 * @Fields 创建时间
	 */
	private Date createdAt;
	/**
	 * @Fields 修改时间
	 */
	private Date updatedAt;
	
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
	
	
	
}
