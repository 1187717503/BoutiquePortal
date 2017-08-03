package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shipping_cost 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shipping_cost") 
public class ShippingCost{
/**
* 1:shipping_cost_id
*/
	 @Id	public Long shipping_cost_id;
/**
* 2:address_city_id
*/
	 @Column	public Long address_city_id;
/**
* 3:the_first_heavy
*/
	 @Column	public BigDecimal the_first_heavy;
/**
* 4:the_first_cost
*/
	 @Column	public BigDecimal the_first_cost;
/**
* 5:expend_heavy
*/
	 @Column	public BigDecimal expend_heavy;
/**
* 6:expend_cost
*/
	 @Column	public BigDecimal expend_cost;
/**
* 7:enabled
*/
	 @Column	public Boolean enabled;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:is_default
*/
	 @Column	public Boolean is_default;
/**
* 11:category
*/
	 @Column	public Long category;
	public Long getShipping_cost_id(){
		return shipping_cost_id;
	}
	public Long getAddress_city_id(){
		return address_city_id;
	}
	public BigDecimal getThe_first_heavy(){
		return the_first_heavy;
	}
	public BigDecimal getThe_first_cost(){
		return the_first_cost;
	}
	public BigDecimal getExpend_heavy(){
		return expend_heavy;
	}
	public BigDecimal getExpend_cost(){
		return expend_cost;
	}
	public Boolean getEnabled(){
		return enabled;
	}
	public Date getCreated_at(){
		return created_at;
	}
	public Date getUpdated_at(){
		return updated_at;
	}
	public Boolean getIs_default(){
		return is_default;
	}
	public Long getCategory(){
		return category;
	}
}

