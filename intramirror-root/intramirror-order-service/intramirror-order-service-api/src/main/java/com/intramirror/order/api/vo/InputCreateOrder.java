package com.intramirror.order.api.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InputCreateOrder {

	public String buyerName;
	public String buyerPhone;
	public String buyerWechat;
	public Long geographyId;
	public Map<String, Object> shipToMap;

	// 商品信息
	public List<CheckoutEntity> checkoutListStr;

	// 金额（不包括运费和增值税）
	public BigDecimal subtotalPrice;

	// 总运费
	public BigDecimal totalShipFee;

	// 总增值税
	public BigDecimal totalTaxFee;

	// 尚未支付的钱
	public BigDecimal balanceDue;

	// 支付方式 1: Wechat 2: Bank card 3: Alipay
	public String paymentMethod;

	// 支付金额
	public BigDecimal amt;

	// 支付时间
	public String paidAt;

	// 买家姓名
	public String paymentBuyerName;

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerPhone() {
		return buyerPhone;
	}

	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}

	public String getBuyerWechat() {
		return buyerWechat;
	}

	public void setBuyerWechat(String buyerWechat) {
		this.buyerWechat = buyerWechat;
	}

	public Long getGeographyId() {
		return geographyId;
	}

	public void setGeographyId(Long geographyId) {
		this.geographyId = geographyId;
	}

	public Map<String, Object> getShipToMap() {
		return shipToMap;
	}

	public void setShipToMap(Map<String, Object> shipToMap) {
		this.shipToMap = shipToMap;
	}

	public List<CheckoutEntity> getCheckoutListStr() {
		return checkoutListStr;
	}

	public void setCheckoutListStr(List<CheckoutEntity> checkoutListStr) {
		this.checkoutListStr = checkoutListStr;
	}

	public BigDecimal getSubtotalPrice() {
		return subtotalPrice;
	}

	public void setSubtotalPrice(BigDecimal subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
	}

	public BigDecimal getTotalShipFee() {
		return totalShipFee;
	}

	public void setTotalShipFee(BigDecimal totalShipFee) {
		this.totalShipFee = totalShipFee;
	}

	public BigDecimal getTotalTaxFee() {
		return totalTaxFee;
	}

	public void setTotalTaxFee(BigDecimal totalTaxFee) {
		this.totalTaxFee = totalTaxFee;
	}

	public BigDecimal getBalanceDue() {
		return balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue) {
		this.balanceDue = balanceDue;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public String getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(String paidAt) {
		this.paidAt = paidAt;
	}

	public String getPaymentBuyerName() {
		return paymentBuyerName;
	}

	public void setPaymentBuyerName(String paymentBuyerName) {
		this.paymentBuyerName = paymentBuyerName;
	}

}
