package com.intramirror.order.api.model;
import java.util.*;
import java.math.BigDecimal;

public class LogisticsProduct{

	private Long logistics_product_id;
	private Long order_logistics_id;
	private Long shop_product_sku_id;
	private BigDecimal in_price;
	private BigDecimal sale_price;
	private Integer amount;
	private BigDecimal fee;
	private Integer deduct_amount;
	private BigDecimal deduct_fee;
	private Integer remain_amount;
	private BigDecimal remain_fee;
	private Integer status;
	private Date created_at;
	private Date updated_at;
	private Boolean enabled;
	private String order_line_num;
	private Long vendor_id;
	private BigDecimal shipping_fee;
	private BigDecimal tax_fee;
	private String tracking_num;
	private String vat_num;
	private Long container_id;
	private Date confirmed_at;
	private Date est_ship_date;
	private Date packed_at;
	private Date shipped_at;
	
	
	public Integer getAmount() {
		return amount;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public Integer getDeduct_amount() {
		return deduct_amount;
	}
	public BigDecimal getDeduct_fee() {
		return deduct_fee;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public BigDecimal getIn_price() {
		return in_price;
	}
	public Long getLogistics_product_id() {
		return logistics_product_id;
	}
	public String getOrder_line_num() {
		return order_line_num;
	}
	public Long getOrder_logistics_id() {
		return order_logistics_id;
	}
	public Integer getRemain_amount() {
		return remain_amount;
	}
	public BigDecimal getRemain_fee() {
		return remain_fee;
	}
	public BigDecimal getSale_price() {
		return sale_price;
	}
	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}
	public Long getShop_product_sku_id() {
		return shop_product_sku_id;
	}
	public Integer getStatus() {
		return status;
	}
	public BigDecimal getTax_fee() {
		return tax_fee;
	}
	public String getTracking_num() {
		return tracking_num;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public String getVat_num() {
		return vat_num;
	}
	public Long getVendor_id() {
		return vendor_id;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public void setDeduct_amount(Integer deduct_amount) {
		this.deduct_amount = deduct_amount;
	}
	public void setDeduct_fee(BigDecimal deduct_fee) {
		this.deduct_fee = deduct_fee;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public void setIn_price(BigDecimal in_price) {
		this.in_price = in_price;
	}
	public void setLogistics_product_id(Long logistics_product_id) {
		this.logistics_product_id = logistics_product_id;
	}
	public void setOrder_line_num(String order_line_num) {
		this.order_line_num = order_line_num;
	}
	public void setOrder_logistics_id(Long order_logistics_id) {
		this.order_logistics_id = order_logistics_id;
	}
	public void setRemain_amount(Integer remain_amount) {
		this.remain_amount = remain_amount;
	}
	public void setRemain_fee(BigDecimal remain_fee) {
		this.remain_fee = remain_fee;
	}
	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}
	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public void setShop_product_sku_id(Long shop_product_sku_id) {
		this.shop_product_sku_id = shop_product_sku_id;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public void setTax_fee(BigDecimal tax_fee) {
		this.tax_fee = tax_fee;
	}
	public void setTracking_num(String tracking_num) {
		this.tracking_num = tracking_num;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public void setVat_num(String vat_num) {
		this.vat_num = vat_num;
	}
	public void setVendor_id(Long vendor_id) {
		this.vendor_id = vendor_id;
	}
	public Date getConfirmed_at() {
		return confirmed_at;
	}
	public void setConfirmed_at(Date confirmed_at) {
		this.confirmed_at = confirmed_at;
	}
	public Date getEst_ship_date() {
		return est_ship_date;
	}
	public void setEst_ship_date(Date est_ship_date) {
		this.est_ship_date = est_ship_date;
	}
	public Date getPacked_at() {
		return packed_at;
	}
	public void setPacked_at(Date packed_at) {
		this.packed_at = packed_at;
	}
	public Date getShipped_at() {
		return shipped_at;
	}
	public void setShipped_at(Date shipped_at) {
		this.shipped_at = shipped_at;
	}
	public Long getContainer_id() {
		return container_id;
	}
	public void setContainer_id(Long container_id) {
		this.container_id = container_id;
	}

	
	
}

