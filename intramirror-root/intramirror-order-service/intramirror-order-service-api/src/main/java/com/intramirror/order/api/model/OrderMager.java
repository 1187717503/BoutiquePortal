package com.intramirror.order.api.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 总订单
 * @author wenzhihao
 *
 */
public class OrderMager {

	/** logistics_product_id **/
	public String orderId;
	
	/** logistics_product.order_line_num **/
	public String orderNumber;
	
	/** logistics_product.created_at **/
	public Date createTime;
	
	/** logistics_product.updated_at **/
	public Date updatedTime;
	
	/** 购买数量  logistics_product.amount **/
	public String itemsCount;
	
	/** product.productCode **/
	public String productCode;
	
	/** Brand ID + Color Code + Size **/
	public String sku;
	
	/** product_sku_property_value.size **/
	public String size;
	
	/** sku.inPrice **/
	public BigDecimal price;
	
	/** logistics_product.fee **/
	public BigDecimal totalPrice;
	
	/** 订单状态   logistics_product.status**/
	public String status;
	
	/** 收件人姓名   logistics**/
	public String customerName;
	
	/** 收件人地址   logistics**/
	public String address;
	
	/** 收件人地区   logistics**/
	public String area;
	
	/** 收件人城市   logistics**/
	public String city;
	
	/** 收件人省份   logistics **/
	public String province;
	
	/** 收件人国家   logistics  **/
	public String country;
	
	/** 邮政编码   logistics **/
	public String zipcode;
	
	/** 收件人手机号码   logistics**/
	public String mobilePhone;
	
	/** 邮箱   logistics**/
	public String email;
	
	/** order_line_num **/
	public String orderLineId;
	
	/** product.productCode **/
	public String boutiqueId;
	
	/** sku.sku_code **/
	public String barcode;
	
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getItemsCount() {
		return itemsCount;
	}
	public void setItemsCount(String itemsCount) {
		this.itemsCount = itemsCount;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(String orderLineId) {
		this.orderLineId = orderLineId;
	}
	public String getBoutiqueId() {
		return boutiqueId;
	}
	public void setBoutiqueId(String boutiqueId) {
		this.boutiqueId = boutiqueId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}


}
