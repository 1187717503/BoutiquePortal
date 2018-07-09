package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * favourite_shop 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("favourite_shop") 
public class FavouriteShop{
/**
* 1:favourite_shop_id
*/
	 @Id	public Long favourite_shop_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:shop_id
*/
	 @Column	public Long shop_id;
/**
* 4:created_at
*/
	 @Column	public Date created_at;
/**
* 5:updated_at
*/
	 @Column	public Date updated_at;
/**
* 6:enabled
*/
	 @Column	public Boolean enabled;
/**
* 7:remark
*/
	 @Column	public String remark;
/**
* 8:status
*/
	 @Column	public Boolean status;
	public Long getFavourite_shop_id(){
		return favourite_shop_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getShop_id(){
		return shop_id;
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
	public Boolean getStatus(){
		return status;
	}
}

