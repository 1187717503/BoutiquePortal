package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * logistics_product 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("logistics_product") 
public class LogisticsProduct{
/**
* 1:logistics_product_id
*/
	 @Id	public Long logistics_product_id;
/**
* 2:order_logistics_id
*/
	 @Column	public Long order_logistics_id;
/**
* 3:shop_product_sku_id
*/
	 @Column	public Long shop_product_sku_id;
/**
* 4:in_price
*/
	 @Column	public BigDecimal in_price;
/**
* 5:sale_price
*/
	 @Column	public BigDecimal sale_price;
/**
* 6:amount
*/
	 @Column	public Integer amount;
/**
* 7:fee
*/
	 @Column	public BigDecimal fee;
/**
* 8:deduct_amount
*/
	 @Column	public Integer deduct_amount;
/**
* 9:deduct_fee
*/
	 @Column	public BigDecimal deduct_fee;
/**
* 10:remain_amount
*/
	 @Column	public Integer remain_amount;
/**
* 11:remain_fee
*/
	 @Column	public BigDecimal remain_fee;
/**
* 12:status
*/
	 @Column	public Integer status;
/**
* 13:created_at
*/
	 @Column	public Date created_at;
/**
* 14:updated_at
*/
	 @Column	public Date updated_at;
/**
* 15:enabled
*/
	@Column	public Boolean enabled;

   @Column	public String order_line_num;

   @Column	public Long vendor_id;

   @Column	public BigDecimal shipping_fee;

   @Column	public BigDecimal tax_fee;

   @Column	public String tracking_num;

   @Column	public String vat_num;

	   public String getTracking_num() {
		   return tracking_num;
	   }

	   public void setTracking_num(String tracking_num) {
		   this.tracking_num = tracking_num;
	   }

	   public String getVat_num() {
		   return vat_num;
	   }

	   public void setVat_num(String vat_num) {
		   this.vat_num = vat_num;
	   }

	   public String getOrder_line_num() {
		   return order_line_num;
	   }

	   public Long getVendor_id() {
		   return vendor_id;
	   }

	   public BigDecimal getShipping_fee() {
		   return shipping_fee;
	   }

	   public BigDecimal getTax_fee() {
		   return tax_fee;
	   }

	   public Long getLogistics_product_id(){
		return logistics_product_id;
	}
	public Long getOrder_logistics_id(){
		return order_logistics_id;
	}
	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
	}
	public BigDecimal getIn_price(){
		return in_price;
	}
	public BigDecimal getSale_price(){
		return sale_price;
	}
	public Integer getAmount(){
		return amount;
	}
	public BigDecimal getFee(){
		return fee;
	}
	public Integer getDeduct_amount(){
		return deduct_amount;
	}
	public BigDecimal getDeduct_fee(){
		return deduct_fee;
	}
	public Integer getRemain_amount(){
		return remain_amount;
	}
	public BigDecimal getRemain_fee(){
		return remain_fee;
	}
	public Integer getStatus(){
		return status;
	}
	public Date getCreated_at(){
		return created_at;
	}
	public Date getUpdated_at(){
		return updated_at;
	}
	public Boolean getEnabled(){
		return enabled;
	}

	public void setLogistics_product_id(Long logistics_product_id) {
		this.logistics_product_id = logistics_product_id;
	}

	public void setOrder_logistics_id(Long order_logistics_id) {
		this.order_logistics_id = order_logistics_id;
	}

	public void setShop_product_sku_id(Long shop_product_sku_id) {
		this.shop_product_sku_id = shop_product_sku_id;
	}

	public void setIn_price(BigDecimal in_price) {
		this.in_price = in_price;
	}

	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public void setDeduct_amount(Integer deduct_amount) {
		this.deduct_amount = deduct_amount;
	}

	public void setDeduct_fee(BigDecimal deduct_fee) {
		this.deduct_fee = deduct_fee;
	}

	public void setRemain_amount(Integer remain_amount) {
		this.remain_amount = remain_amount;
	}

	public void setRemain_fee(BigDecimal remain_fee) {
		this.remain_fee = remain_fee;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setOrder_line_num(String order_line_num) {
		this.order_line_num = order_line_num;
	}

	public void setVendor_id(Long vendor_id) {
		this.vendor_id = vendor_id;
	}

	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}

	public void setTax_fee(BigDecimal tax_fee) {
		this.tax_fee = tax_fee;
	}
	
	
}

