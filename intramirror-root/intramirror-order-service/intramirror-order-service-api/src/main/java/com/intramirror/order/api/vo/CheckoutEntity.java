package com.intramirror.order.api.vo;

import java.math.BigDecimal;

public class CheckoutEntity {

	private BigDecimal in_price;
	private Integer quantity;
	private BigDecimal sale_price;
	private BigDecimal shipping_fee;
	private Long shop_product_sku_id;
	private BigDecimal tax_fee;
	private Long tax_id;

	public BigDecimal getIn_price() {
		return in_price;
	}

	public void setIn_price(BigDecimal in_price) {
		this.in_price = in_price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSale_price() {
		return sale_price;
	}

	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}

	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}

	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}

	public Long getShop_product_sku_id() {
		return shop_product_sku_id;
	}

	public void setShop_product_sku_id(Long shop_product_sku_id) {
		this.shop_product_sku_id = shop_product_sku_id;
	}

	public BigDecimal getTax_fee() {
		return tax_fee;
	}

	public void setTax_fee(BigDecimal tax_fee) {
		this.tax_fee = tax_fee;
	}

	public Long getTax_id() {
		return tax_id;
	}

	public void setTax_id(Long tax_id) {
		this.tax_id = tax_id;
	}

}
