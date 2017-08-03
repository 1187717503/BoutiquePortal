package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_order_settlement_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_order_settlement_daily") 
public class StatisticsOrderSettlementDaily{
/**
* 1:statistics_order_settlement_daily_id
*/
	 @Id	public Long statistics_order_settlement_daily_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 4:order_id
*/
	 @Column	public Long order_id;
/**
* 5:pay_way
*/
	 @Column	public Integer pay_way;
/**
* 6:revenue
*/
	 @Column	public BigDecimal revenue;
/**
* 7:in_price_fee
*/
	 @Column	public BigDecimal in_price_fee;
/**
* 8:shipping_fee
*/
	 @Column	public BigDecimal shipping_fee;
/**
* 9:status
*/
	 @Column	public Integer status;
/**
* 10:created_at
*/
	 @Column	public Date created_at;
/**
* 11:updated_at
*/
	 @Column	public Date updated_at;
/**
* 12:enabled
*/
	 @Column	public Boolean enabled;
	public Long getStatistics_order_settlement_daily_id(){
		return statistics_order_settlement_daily_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Long getOrder_id(){
		return order_id;
	}
	public Integer getPay_way(){
		return pay_way;
	}
	public BigDecimal getRevenue(){
		return revenue;
	}
	public BigDecimal getIn_price_fee(){
		return in_price_fee;
	}
	public BigDecimal getShipping_fee(){
		return shipping_fee;
	}
	public Integer getStatus(){
		return status;
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

