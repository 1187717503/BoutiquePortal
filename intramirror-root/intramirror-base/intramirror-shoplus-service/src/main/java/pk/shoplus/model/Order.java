package pk.shoplus.model;

import java.math.BigDecimal;
import java.util.Date;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * `order` 实体类 Thu Dec 01 18:07:15 CST 2016
 */
@Entity("`order`")
public class Order {
	/**
	 * 1:order_id
	 */
	@Id
	public Long order_id;
	/**
	 * 2:order_num
	 */
	@Column
	public String order_num;
	/**
	 * 3:user_id
	 */
	@Column
	public Long user_id;
	/**
	 * 4:fee
	 */
	@Column
	public BigDecimal fee;
	/**
	 * 5:coupon
	 */
	@Column
	public String coupon;
	/**
	 * 6:coupon_fee
	 */
	@Column
	public BigDecimal coupon_fee;
	/**
	 * 7:discount
	 */
	@Column
	public Integer discount;
	/**
	 * 8:discount_fee
	 */
	@Column
	public BigDecimal discount_fee;
	/**
	 * 9:ship_fee
	 */
	@Column
	public BigDecimal ship_fee;
	/**
	 * 10:pay_fee
	 */
	@Column
	public BigDecimal pay_fee;
	/**
	 * 11:pay_way
	 */
	@Column
	public Integer pay_way;
	/**
	 * 12:online_payment_id
	 */
	@Column
	public Long online_payment_id;
	/**
	 * 13:confirmed_at
	 */
	@Column
	public Date confirmed_at;
	/**
	 * 14:status
	 */
	@Column
	public Integer status;
	/**
	 * 15:cancel_type
	 */
	@Column
	public Integer cancel_type;
	/**
	 * 16:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 17:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 18:enabled
	 */
	@Column
	public Boolean enabled;
	
	@Column
	public BigDecimal tax_fee;
	
	@Column
	public BigDecimal current_rate;

	@Column
	public BigDecimal total_sale_price_rmb;

	@Column
	public BigDecimal total_shipping_fee_rmb;

	@Column
	public BigDecimal total_tax_rmb;

	@Column
	public BigDecimal total_rmb;

	@Column
	public String wechat;

	@Column
	public String contact_phone;

	@Column
	public String area_code;

	public Long getOrder_id() {
		return order_id;
	}

	public String getOrder_num() {
		return order_num;
	}

	public Long getUser_id() {
		return user_id;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public String getCoupon() {
		return coupon;
	}

	public BigDecimal getCoupon_fee() {
		return coupon_fee;
	}

	public Integer getDiscount() {
		return discount;
	}

	public BigDecimal getDiscount_fee() {
		return discount_fee;
	}

	public BigDecimal getShip_fee() {
		return ship_fee;
	}

	public BigDecimal getPay_fee() {
		return pay_fee;
	}

	public Integer getPay_way() {
		return pay_way;
	}

	public Long getOnline_payment_id() {
		return online_payment_id;
	}

	public Date getConfirmed_at() {
		return confirmed_at;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getCancel_type() {
		return cancel_type;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public Boolean getEnabled() {
		return enabled;
	}
	
	public BigDecimal getTax_fee() {
		return tax_fee;
	}
	
	public BigDecimal getCurrent_rate() {
		return current_rate;
	}
	
	public BigDecimal getTotal_sale_price_rmb() {
		return total_sale_price_rmb;
	}

	public void setTotal_sale_price_rmb(BigDecimal total_sale_price_rmb) {
		this.total_sale_price_rmb = total_sale_price_rmb;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public BigDecimal getTotal_shipping_fee_rmb() {
		return total_shipping_fee_rmb;
	}

	public void setTotal_shipping_fee_rmb(BigDecimal total_shipping_fee_rmb) {
		this.total_shipping_fee_rmb = total_shipping_fee_rmb;
	}

	public BigDecimal getTotal_tax_rmb() {
		return total_tax_rmb;
	}

	public void setTotal_tax_rmb(BigDecimal total_tax_rmb) {
		this.total_tax_rmb = total_tax_rmb;
	}

	public BigDecimal getTotal_rmb() {
		return total_rmb;
	}

	public void setTotal_rmb(BigDecimal total_rmb) {
		this.total_rmb = total_rmb;
	}
}
