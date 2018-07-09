package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_category 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_category") 
public class ShopCategory{
/**
* 1:shop_category_id
*/
	 @Id	public Long shop_category_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:category_id
*/
	 @Column	public Long category_id;
/**
* 4:shop_organization_application_id
*/
	 @Column	public Long shop_organization_application_id;
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
* 8:remark
*/
	 @Column	public String remark;
/**
* 9:status
*/
	 @Column	public Integer status;
	public Long getShop_category_id(){
		return shop_category_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getCategory_id(){
		return category_id;
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
	public String getRemark(){
		return remark;
	}
	public Integer getStatus(){
		return status;
	}
}

