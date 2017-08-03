package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_brand 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_brand") 
public class ShopBrand{
/**
* 1:shop_brand_id
*/
	 @Id	public Long shop_brand_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:brand_id
*/
	 @Column	public Long brand_id;
/**
* 4:brand_name
*/
	 @Column	public String brand_name;
/**
* 5:shop_organization_application_id
*/
	 @Column	public Long shop_organization_application_id;
/**
* 6:created_at
*/
	 @Column	public Date created_at;
/**
* 7:updated_at
*/
	 @Column	public Date updated_at;
/**
* 8:enabled
*/
	 @Column	public Boolean enabled;
/**
* 9:status
*/
	 @Column	public Integer status;
	public Long getShop_brand_id(){
		return shop_brand_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getBrand_id(){
		return brand_id;
	}
	public String getBrand_name(){
		return brand_name;
	}
	public Long getShop_organization_application_id(){
		return shop_organization_application_id;
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
	public Integer getStatus(){
		return status;
	}
}

