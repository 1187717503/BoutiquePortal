package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_individual_application 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_individual_application") 
public class ShopIndividualApplication{
/**
* 1:shop_individual_application_id
*/
	 @Id	public Long shop_individual_application_id;
/**
* 2:shop_name
*/
	 @Column	public String shop_name;
/**
* 3:shop_pic
*/
	 @Column	public String shop_pic;
/**
* 4:user_id
*/
	 @Column	public Long user_id;
/**
* 5:brand_ids
*/
	 @Column	public String brand_ids;
/**
* 6:category_ids
*/
	 @Column	public String category_ids;
/**
* 7:identity_card
*/
	 @Column	public String identity_card;
/**
* 8:province
*/
	 @Column	public String province;
/**
* 9:city
*/
	 @Column	public String city;
/**
* 10:type
*/
	 @Column	public Integer type;
/**
* 11:status
*/
	 @Column	public Integer status;
/**
* 12:reason
*/
	 @Column	public String reason;
/**
* 13:operate_user_id
*/
	 @Column	public Long operate_user_id;
/**
* 14:created_at
*/
	 @Column	public Date created_at;
/**
* 15:updated_at
*/
	 @Column	public Date updated_at;
/**
* 16:enabled
*/
	 @Column	public Boolean enabled;
/**
* 17:shop_brand_type
*/
	 @Column	public Integer shop_brand_type;
	public Long getShop_individual_application_id(){
		return shop_individual_application_id;
	}
	public String getShop_name(){
		return shop_name;
	}
	public String getShop_pic(){
		return shop_pic;
	}
	public Long getUser_id(){
		return user_id;
	}
	public String getBrand_ids(){
		return brand_ids;
	}
	public String getCategory_ids(){
		return category_ids;
	}
	public String getIdentity_card(){
		return identity_card;
	}
	public String getProvince(){
		return province;
	}
	public String getCity(){
		return city;
	}
	public Integer getType(){
		return type;
	}
	public Integer getStatus(){
		return status;
	}
	public String getReason(){
		return reason;
	}
	public Long getOperate_user_id(){
		return operate_user_id;
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
	public Integer getShop_brand_type(){
		return shop_brand_type;
	}
}

