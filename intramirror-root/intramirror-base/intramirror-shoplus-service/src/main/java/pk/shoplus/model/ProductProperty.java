package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * product_property 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("product_property") 
public class ProductProperty{
/**
* 1:product_property_id
*/
	 @Id	public Long product_property_id;
/**
* 2:category_product_property_id
*/
	 @Column	public Long category_product_property_id;
/**
* 3:product_id
*/
	 @Column	public Long product_id;
/**
* 4:key_name
*/
	 @Column	public String key_name;
/**
* 5:type
*/
	 @Column	public Integer type;
/**
* 6:value
*/
	 @Column	public String value;
/**
* 7:remark
*/
	 @Column	public String remark;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:enabled
*/
	 @Column	public Boolean enabled;
	public Long getProduct_property_id(){
		return product_property_id;
	}
	public Long getCategory_product_property_id(){
		return category_product_property_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public String getKey_name(){
		return key_name;
	}
	public Integer getType(){
		return type;
	}
	public String getValue(){
		return value;
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

