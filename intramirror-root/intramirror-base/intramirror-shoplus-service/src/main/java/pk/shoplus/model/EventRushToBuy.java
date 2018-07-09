package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * event_rush_to_buy 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("event_rush_to_buy") 
public class EventRushToBuy{
/**
* 1:event_rush_to_buy_id
*/
	 @Id	public Long event_rush_to_buy_id;
/**
* 2:event_id
*/
	 @Column	public Long event_id;
/**
* 3:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 4:product_id
*/
	 @Column	public Long product_id;
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
/**
* 9:max_up_to_percentage
*/
	 @Column	public BigDecimal max_up_to_percentage;
	public Long getEvent_rush_to_buy_id(){
		return event_rush_to_buy_id;
	}
	public Long getEvent_id(){
		return event_id;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getProduct_id(){
		return product_id;
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
	public BigDecimal getMax_up_to_percentage(){
		return max_up_to_percentage;
	}
}

