package com.intramirror.payment.api.model;

/**
 * 退款VO
 * @author wzh
 * @since 2017-7-17 10:48:25
 */
public class RefundVO {
	/** 商户编号  */
	private String merchantId;
	
	/** 订单号 */
	private String requestId;
	
	/** 金额 */
	private String amount;
	
	/** 原订单流水号 */
	private String orderId;
	
	/** 通知回调URL */
	private String notifyUrl;
	
	/** 备注 */
	private String remark = "";
	
	/** 参数签名 */
	private String hmac;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
}
