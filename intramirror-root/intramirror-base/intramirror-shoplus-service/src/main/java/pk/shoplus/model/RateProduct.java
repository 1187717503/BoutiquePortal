package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * rate_product 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("rate_product") 
public class RateProduct{
/**
* 1:rate_product_id
*/
	 @Id	public Long rate_product_id;
/**
* 2:rate_order_id
*/
	 @Column	public Long rate_order_id;
/**
* 3:user_id
*/
	 @Column	public Long user_id;
/**
* 4:shop_id
*/
	 @Column	public Long shop_id;
/**
* 5:comment_id
*/
	 @Column	public Long comment_id;
/**
* 6:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 7:shop_product_sku_id
*/
	 @Column	public Long shop_product_sku_id;
/**
* 8:product_id
*/
	 @Column	public Long product_id;
/**
* 9:sku_id
*/
	 @Column	public Long sku_id;
/**
* 10:rate
*/
	 @Column	public Integer rate;
/**
* 11:status
*/
	 @Column	public Integer status;
/**
* 12:remark
*/
	 @Column	public String remark;
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
/**
* 16:type
*/
	 @Column	public Integer type;
	public Long getRate_product_id(){
		return rate_product_id;
	}
	public Long getRate_order_id(){
		return rate_order_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getComment_id(){
		return comment_id;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public Integer getRate(){
		return rate;
	}
	public Integer getStatus(){
		return status;
	}
	public String getRemark(){
		return remark;
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
	public Integer getType(){
		return type;
	}
}

