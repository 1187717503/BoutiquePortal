package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * return_product 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("return_product") 
public class ReturnProduct{
/**
* 1:return_product_id
*/
	 @Id	public Long return_product_id;
/**
* 2:return_application_id
*/
	 @Column	public Long return_application_id;
/**
* 3:shop_product_sku_id
*/
	 @Column	public Long shop_product_sku_id;
/**
* 4:sale_price
*/
	 @Column	public BigDecimal sale_price;
/**
* 5:amount
*/
	 @Column	public Integer amount;
/**
* 6:fee
*/
	 @Column	public BigDecimal fee;
/**
* 7:status
*/
	 @Column	public Integer status;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:in_price
*/
	 @Column	public BigDecimal in_price;
/**
* 11:enabled
*/
	 @Column	public Boolean enabled;
	public Long getReturn_product_id(){
		return return_product_id;
	}
	public Long getReturn_application_id(){
		return return_application_id;
	}
	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
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
	public Integer getStatus(){
		return status;
	}
	public Date getCreated_at(){
		return created_at;
	}
	public Date getUpdated_at(){
		return updated_at;
	}
	public BigDecimal getIn_price(){
		return in_price;
	}
	public Boolean getEnabled(){
		return enabled;
	}
}

