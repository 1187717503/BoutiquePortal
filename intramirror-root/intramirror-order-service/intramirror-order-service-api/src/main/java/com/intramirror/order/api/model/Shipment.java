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

	private Long geographyId;

	/**
	 * 发货地id
	 */
	private Integer shipFromLocationId;

	private String stockLocation;
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

	private Integer fromType;

	private Long fromRefId;

	private Integer toType;

	private Long toRefId;

	private Integer printStep;

	public Long getGeographyId() {
		return geographyId;
	}

	public void setGeographyId(Long geographyId) {
		this.geographyId = geographyId;
	}

	public Integer getFromType() {
		return fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	public Long getFromRefId() {
		return fromRefId;
	}

	public void setFromRefId(Long fromRefId) {
		this.fromRefId = fromRefId;
	}

	public Integer getToType() {
		return toType;
	}

	public void setToType(Integer toType) {
		this.toType = toType;
	}

	public Long getToRefId() {
		return toRefId;
	}

	public void setToRefId(Long toRefId) {
		this.toRefId = toRefId;
	}

	public String getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(String stockLocation) {
		this.stockLocation = stockLocation;
	}

	public Integer getShipFromLocationId() {
		return shipFromLocationId;
	}

	public void setShipFromLocationId(Integer shipFromLocationId) {
		this.shipFromLocationId = shipFromLocationId;
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

	public Integer getPrintStep() {
		return printStep;
	}

	public void setPrintStep(Integer printStep) {
		this.printStep = printStep;
	}
}
