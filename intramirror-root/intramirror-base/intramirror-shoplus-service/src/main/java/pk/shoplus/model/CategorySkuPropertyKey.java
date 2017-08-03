package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * category_sku_property_key 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("category_sku_property_key") 
public class CategorySkuPropertyKey{
/**
* 1:category_sku_property_key_id
*/
	 @Id	public Long category_sku_property_key_id;
/**
* 2:category_id
*/
	 @Column	public Long category_id;
/**
* 3:name
*/
	 @Column	public String name;
/**
* 4:remark
*/
	 @Column	public String remark;
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
	public Long getCategory_sku_property_key_id(){
		return category_sku_property_key_id;
	}
	public Long getCategory_id(){
		return category_id;
	}
	public String getName(){
		return name;
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
}

