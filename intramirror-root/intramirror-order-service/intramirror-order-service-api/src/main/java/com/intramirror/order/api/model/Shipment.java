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
	 * @Fields 收货人
	 */
	private String consignee;
	/**
	 * @Fields 地址
	 */
	private String shipToAddr;
	/**
	 * @Fields 区
	 */
	private String shipToDistrict;
	/**
	 * @Fields 城市
	 */
	private String shipToCity;
	/**
	 * @Fields 省份
	 */
	private String shipToProvince;
	/**
	 * @Fields 国家
	 */
	private String shipToCountry;
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
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getShipToAddr() {
		return shipToAddr;
	}
	public void setShipToAddr(String shipToAddr) {
		this.shipToAddr = shipToAddr;
	}
	public String getShipToDistrict() {
		return shipToDistrict;
	}
	public void setShipToDistrict(String shipToDistrict) {
		this.shipToDistrict = shipToDistrict;
	}
	public String getShipToCity() {
		return shipToCity;
	}
	public void setShipToCity(String shipToCity) {
		this.shipToCity = shipToCity;
	}
	public String getShipToProvince() {
		return shipToProvince;
	}
	public void setShipToProvince(String shipToProvince) {
		this.shipToProvince = shipToProvince;
	}
	public String getShipToCountry() {
		return shipToCountry;
	}
	public void setShipToCountry(String shipToCountry) {
		this.shipToCountry = shipToCountry;
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
