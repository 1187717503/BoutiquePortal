package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * sku_property 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("sku_property") 
public class SkuProperty{
/**
* 1:sku_property_id
*/
	 @Id	public Long sku_property_id;
/**
* 2:sku_id
*/
	 @Column	public Long sku_id;
/**
* 3:product_sku_property_key_id
*/
	 @Column	public Long product_sku_property_key_id;
/**
* 4:product_sku_property_value_id
*/
	 @Column	public Long product_sku_property_value_id;
/**
* 5:name
*/
	 @Column	public String name;
/**
* 6:remark
*/
	 @Column	public String remark;
/**
* 7:created_at
*/
	 @Column	public Date created_at;
/**
* 8:updated_at
*/
	 @Column	public Date updated_at;
/**
* 9:enabled
*/
	 @Column	public Boolean enabled;
	public Long getSku_property_id(){
		return sku_property_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public Long getProduct_sku_property_key_id(){
		return product_sku_property_key_id;
	}
	public Long getProduct_sku_property_value_id(){
		return product_sku_property_value_id;
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

