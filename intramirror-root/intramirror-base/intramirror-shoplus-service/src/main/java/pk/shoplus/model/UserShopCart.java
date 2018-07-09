package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * user_shop_cart 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("user_shop_cart") 
public class UserShopCart{
/**
* 1:user_shop_cart_id
*/
	 @Id	public Long user_shop_cart_id;
/**
* 2:shop_product_sku_id
*/
	 @Column	public Long shop_product_sku_id;
/**
* 3:sku_id
*/
	 @Column	public Long sku_id;
/**
* 4:shop_id
*/
	 @Column	public Long shop_id;
/**
* 5:user_id
*/
	 @Column	public Long user_id;
/**
* 6:product_id
*/
	 @Column	public Long product_id;
/**
* 7:quantity
*/
	 @Column	public Integer quantity;
/**
* 8:status
*/
	 @Column	public Integer status;
/**
* 9:created_at
*/
	 @Column	public Date created_at;
/**
* 10:updated_at
*/
	 @Column	public Date updated_at;
/**
* 11:enabled
*/
	 @Column	public Boolean enabled;
/**
* 12:remark
*/
	 @Column	public String remark;
	public Long getUser_shop_cart_id(){
		return user_shop_cart_id;
	}
	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Integer getQuantity(){
		return quantity;
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
	public String getRemark(){
		return remark;
	}
}

