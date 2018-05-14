/**
 * 
 */
package com.intramirror.order.api.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 袁孟亮
 *
 */
public class Container {

	/**
	 * @Fields 容器ID
	 */
	private Long containerId;
	
	/**
	 * @Fields 容器类型
	 */
	private String containerType;
	
	/**
	 * @Fields 地理区域
	 */
	private String shipToGeography;

	private Integer shipFromLocationId;

	private String stockLocation;
	
	/**
	 * @Fields 状态
	 */
	private int status;
	
	/**
	 * @Fields 高度
	 */
	private Long height;
	
	/**
	 * @Fields 宽度
	 */
	private Long width;
	
	/**
	 * @Fields 长度
	 */
	private Long length;
	
	/**
	 * @Fields 重量
	 */
	private BigDecimal weight;
	
	/**
	 * @Fields 条形码
	 */
	private String barcode;
	
	/**
	 * @Fields 创建时间
	 */
	private Date createdAt;
	
	/**
	 * @Fields 修改时间
	 */
	private Date updatedAt;
	
	/**
	 * @Fields 装货ID
	 */
	private Long shipmentId;

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

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
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

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
}
