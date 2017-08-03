package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * favourite_shop_product 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("favourite_shop_product") 
public class FavouriteShopProduct{
/**
* 1:favourite_shop_product_id
*/
	 @Id	public Long favourite_shop_product_id;
/**
* 2:product_id
*/
	 @Column	public Long product_id;
/**
* 3:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 4:user_id
*/
	 @Column	public Long user_id;
/**
* 5:created_at
*/
	 @Column	public Date created_at;
/**
* 6:updated_at
*/
	 @Column	public Date updated_at;
/**
* 7:enabled
*/
	 @Column	public Boolean enabled;
/**
* 8:status
*/
	 @Column	public Boolean status;
/**
* 9:remark
*/
	 @Column	public String remark;
	public Long getFavourite_shop_product_id(){
		return favourite_shop_product_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getUser_id(){
		return user_id;
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
	public Boolean getStatus(){
		return status;
	}
	public String getRemark(){
		return remark;
	}
}

