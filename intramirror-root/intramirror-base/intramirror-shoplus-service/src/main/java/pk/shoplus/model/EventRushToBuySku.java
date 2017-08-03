package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * event_rush_to_buy_sku 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("event_rush_to_buy_sku") 
public class EventRushToBuySku{
/**
* 1:event_rush_to_buy_sku_id
*/
	 @Id	public Long event_rush_to_buy_sku_id;
/**
* 2:event_rush_to_buy_id
*/
	 @Column	public Long event_rush_to_buy_id;
/**
* 3:shop_product_sku_id
*/
	 @Column	public Long shop_product_sku_id;
/**
* 4:quantity
*/
	 @Column	public Integer quantity;
/**
* 5:remaining
*/
	 @Column	public Integer remaining;
/**
* 6:rush_price
*/
	 @Column	public BigDecimal rush_price;
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
/**
* 10:remark
*/
	 @Column	public String remark;
	public Long getEvent_rush_to_buy_sku_id(){
		return event_rush_to_buy_sku_id;
	}
	public Long getEvent_rush_to_buy_id(){
		return event_rush_to_buy_id;
	}
	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
	}
	public Integer getQuantity(){
		return quantity;
	}
	public Integer getRemaining(){
		return remaining;
	}
	public BigDecimal getRush_price(){
		return rush_price;
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
}

