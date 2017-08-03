package pk.shoplus.model;

import java.util.Date;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

@Entity("payment_result")
public class PaymentResultModel {
	
	@Id public Long payment_result_id;
	@Column public Long payment_id;
	@Column public String marchant_id;
	@Column public String request_id;
	@Column public String scan_code;
	@Column public String app_params;
	@Column public String redirect_url;
	@Column public String serial_number;
	@Column public String total_refund_count;
	@Column public String total_refund_amount;
	@Column public String order_currency;
	@Column public String order_amount;
	@Column public String process_status;
	@Column public Date complete_date_time;
	@Column public String remark;
	@Column public String hmac;
	@Column public Date created_at;
	@Column public Date update_at;
	@Column public String bind_card_id;
	public Long getPayment_result_id() {
		return payment_result_id;
	}
	public void setPayment_result_id(Long payment_result_id) {
		this.payment_result_id = payment_result_id;
	}
	public Long getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Long payment_id) {
		this.payment_id = payment_id;
	}
	public String getMarchant_id() {
		return marchant_id;
	}
	public void setMarchant_id(String marchant_id) {
		this.marchant_id = marchant_id;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public String getScan_code() {
		return scan_code;
	}
	public void setScan_code(String scan_code) {
		this.scan_code = scan_code;
	}
	public String getApp_params() {
		return app_params;
	}
	public void setApp_params(String app_params) {
		this.app_params = app_params;
	}
	public String getRedirect_url() {
		return redirect_url;
	}
	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getTotal_refund_count() {
		return total_refund_count;
	}
	public void setTotal_refund_count(String total_refund_count) {
		this.total_refund_count = total_refund_count;
	}
	public String getTotal_refund_amount() {
		return total_refund_amount;
	}
	public void setTotal_refund_amount(String total_refund_amount) {
		this.total_refund_amount = total_refund_amount;
	}
	public String getOrder_currency() {
		return order_currency;
	}
	public void setOrder_currency(String order_currency) {
		this.order_currency = order_currency;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public String getProcess_status() {
		return process_status;
	}
	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}
	public Date getComplete_date_time() {
		return complete_date_time;
	}
	public void setComplete_date_time(Date complete_date_time) {
		this.complete_date_time = complete_date_time;
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
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdate_at() {
		return update_at;
	}
	public void setUpdate_at(Date update_at) {
		this.update_at = update_at;
	}
	public String getBind_card_id() {
		return bind_card_id;
	}
	public void setBind_card_id(String bind_card_id) {
		this.bind_card_id = bind_card_id;
	}
	
}
