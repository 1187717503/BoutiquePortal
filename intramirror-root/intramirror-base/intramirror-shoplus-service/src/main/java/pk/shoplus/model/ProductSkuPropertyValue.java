package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * product_sku_property_value 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("product_sku_property_value") 
public class ProductSkuPropertyValue{
/**
* 1:product_sku_property_value_id
*/
	 @Id	public Long product_sku_property_value_id;
/**
* 2:product_sku_property_key_id
*/
	 @Column	public Long product_sku_property_key_id;
/**
* 3:value
*/
	 @Column	public String value;
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
	public Long getProduct_sku_property_value_id(){
		return product_sku_property_value_id;
	}
	public Long getProduct_sku_property_key_id(){
		return product_sku_property_key_id;
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

