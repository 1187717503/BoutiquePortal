package pk.shoplus.model;

import java.util.Date;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

@Entity("payment")
public class PaymentModel {
	
	@Id public Long payment_id;
	@Column	public String order_num;
	@Column	public String merchant_id;
	@Column	public Integer order_amount;
	@Column	public Integer serial_number;
	@Column	public String order_currency;
	@Column	public String request_id;
	@Column	public String notify_url;
	@Column	public String callback_url;
	@Column	public String remark;
	@Column	public String payment_mode_code;
	@Column	public String product_details;
	@Column	public String payer_name;
	@Column	public String payer_phone_number;
	@Column	public String payer_id_type;
	@Column	public String payer_id_num;
	@Column	public String payer_bank_card_num;
	@Column	public String payer_email;
	@Column	public String bank_card_name;
	@Column	public String bank_card_card_no;
	@Column	public String bank_card_cvv2;
	@Column	public String bank_card_id_no;
	@Column	public String bank_card_expiry_date;
	@Column	public String bank_card_mobile_no;
	@Column	public String cashier_version;
	@Column	public String for_use;
	@Column	public String merchant_user_id;
	@Column	public String bind_card_id;
	@Column	public String client_ip;
	@Column	public String timeout;
	@Column	public String auth_code;
	@Column	public String open_id;
	@Column	public String reveiver_name;
	@Column	public String reveiver_phone_num;
	@Column	public String reveiver_address;
	@Column	public String hmac;
	@Column	public Date created_at;
	@Column	public Date updated_at;
	@Column	public Integer submit_status;
	@Column	public String input_charset;
	@Column	public String body;
	@Column	public String out_trade_no;
	@Column	public String partner;
	@Column	public String payment_type;
	@Column	public String seller_id;
	@Column	public String service;
	@Column	public String sign;
	@Column	public String sign_type;
	@Column	public String subject;
	@Column	public String total_fee;
	@Column	public String redirect_url;
	@Column	public String message;
	
	
	public Long getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Long payment_id) {
		this.payment_id = payment_id;
	}
	public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	
	public Integer getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(Integer order_amount) {
		this.order_amount = order_amount;
	}
	public Integer getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(Integer serial_number) {
		this.serial_number = serial_number;
	}
	public String getOrder_currency() {
		return order_currency;
	}
	public void setOrder_currency(String order_currency) {
		this.order_currency = order_currency;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getCallback_url() {
		return callback_url;
	}
	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPayment_mode_code() {
		return payment_mode_code;
	}
	public void setPayment_mode_code(String payment_mode_code) {
		this.payment_mode_code = payment_mode_code;
	}
	public String getProduct_details() {
		return product_details;
	}
	public void setProduct_details(String product_details) {
		this.product_details = product_details;
	}
	public String getPayer_name() {
		return payer_name;
	}
	public void setPayer_name(String payer_name) {
		this.payer_name = payer_name;
	}
	public String getPayer_phone_number() {
		return payer_phone_number;
	}
	public void setPayer_phone_number(String payer_phone_number) {
		this.payer_phone_number = payer_phone_number;
	}
	public String getPayer_id_type() {
		return payer_id_type;
	}
	public void setPayer_id_type(String payer_id_type) {
		this.payer_id_type = payer_id_type;
	}
	public String getPayer_id_num() {
		return payer_id_num;
	}
	public void setPayer_id_num(String payer_id_num) {
		this.payer_id_num = payer_id_num;
	}
	public String getPayer_bank_card_num() {
		return payer_bank_card_num;
	}
	public void setPayer_bank_card_num(String payer_bank_card_num) {
		this.payer_bank_card_num = payer_bank_card_num;
	}
	public String getPayer_email() {
		return payer_email;
	}
	public void setPayer_email(String payer_email) {
		this.payer_email = payer_email;
	}
	public String getBank_card_name() {
		return bank_card_name;
	}
	public void setBank_card_name(String bank_card_name) {
		this.bank_card_name = bank_card_name;
	}
	public String getBank_card_card_no() {
		return bank_card_card_no;
	}
	public void setBank_card_card_no(String bank_card_card_no) {
		this.bank_card_card_no = bank_card_card_no;
	}
	public String getBank_card_cvv2() {
		return bank_card_cvv2;
	}
	public void setBank_card_cvv2(String bank_card_cvv2) {
		this.bank_card_cvv2 = bank_card_cvv2;
	}
	public String getBank_card_id_no() {
		return bank_card_id_no;
	}
	public void setBank_card_id_no(String bank_card_id_no) {
		this.bank_card_id_no = bank_card_id_no;
	}
	public String getBank_card_expiry_date() {
		return bank_card_expiry_date;
	}
	public void setBank_card_expiry_date(String bank_card_expiry_date) {
		this.bank_card_expiry_date = bank_card_expiry_date;
	}
	public String getBank_card_mobile_no() {
		return bank_card_mobile_no;
	}
	public void setBank_card_mobile_no(String bank_card_mobile_no) {
		this.bank_card_mobile_no = bank_card_mobile_no;
	}
	public String getCashier_version() {
		return cashier_version;
	}
	public void setCashier_version(String cashier_version) {
		this.cashier_version = cashier_version;
	}
	public String getFor_use() {
		return for_use;
	}
	public void setFor_use(String for_use) {
		this.for_use = for_use;
	}
	public String getMerchant_user_id() {
		return merchant_user_id;
	}
	public void setMerchant_user_id(String merchant_user_id) {
		this.merchant_user_id = merchant_user_id;
	}
	public String getBind_card_id() {
		return bind_card_id;
	}
	public void setBind_card_id(String bind_card_id) {
		this.bind_card_id = bind_card_id;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getOpen_id() {
		return open_id;
	}
	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}
	public String getReveiver_name() {
		return reveiver_name;
	}
	public void setReveiver_name(String reveiver_name) {
		this.reveiver_name = reveiver_name;
	}
	public String getReveiver_phone_num() {
		return reveiver_phone_num;
	}
	public void setReveiver_phone_num(String reveiver_phone_num) {
		this.reveiver_phone_num = reveiver_phone_num;
	}
	public String getReveiver_address() {
		return reveiver_address;
	}
	public void setReveiver_address(String reveiver_address) {
		this.reveiver_address = reveiver_address;
	}
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public Integer getSubmit_status() {
		return submit_status;
	}
	public void setSubmit_status(Integer submit_status) {
		this.submit_status = submit_status;
	}
	public String getInput_charset() {
		return input_charset;
	}
	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getRedirect_url() {
		return redirect_url;
	}
	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
