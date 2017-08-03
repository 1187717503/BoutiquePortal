package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * category_product_property 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("category_product_property") 
public class CategoryProductProperty{
/**
* 1:category_product_property_id
*/
	 @Id	public Long category_product_property_id;
/**
* 2:category_id
*/
	 @Column	public Long category_id;
/**
* 3:key_name
*/
	 @Column	public String key_name;
/**
* 4:type
*/
	 @Column	public Integer type;
/**
* 5:remark
*/
	 @Column	public String remark;
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
	public Long getCategory_product_property_id(){
		return category_product_property_id;
	}
	public Long getCategory_id(){
		return category_id;
	}
	public String getKey_name(){
		return key_name;
	}
	public Integer getType(){
		return type;
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

