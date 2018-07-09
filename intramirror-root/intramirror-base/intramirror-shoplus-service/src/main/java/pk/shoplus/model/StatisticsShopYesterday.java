package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_shop_yesterday 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_shop_yesterday") 
public class StatisticsShopYesterday{
/**
* 1:statistics_shop_yesterday_id
*/
	 @Id	public Long statistics_shop_yesterday_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:revenue_yesterday
*/
	 @Column	public BigDecimal revenue_yesterday;
/**
* 4:revenue_7days
*/
	 @Column	public BigDecimal revenue_7days;
/**
* 5:revenue_total
*/
	 @Column	public BigDecimal revenue_total;
/**
* 6:orders_yesterday
*/
	 @Column	public Integer orders_yesterday;
/**
* 7:orders_7days
*/
	 @Column	public Integer orders_7days;
/**
* 8:orders_total
*/
	 @Column	public Integer orders_total;
/**
* 9:returns_yesterday
*/
	 @Column	public Integer returns_yesterday;
/**
* 10:returns_7days
*/
	 @Column	public Integer returns_7days;
/**
* 11:returns_total
*/
	 @Column	public Integer returns_total;
/**
* 12:uv_yesterday
*/
	 @Column	public Integer uv_yesterday;
/**
* 13:uv_7days
*/
	 @Column	public Integer uv_7days;
/**
* 14:uv_total
*/
	 @Column	public Integer uv_total;
/**
* 15:pv_yesterday
*/
	 @Column	public Integer pv_yesterday;
/**
* 16:pv_7days
*/
	 @Column	public Integer pv_7days;
/**
* 17:pv_total
*/
	 @Column	public Integer pv_total;
/**
* 18:withdrew_cash_yesterday
*/
	 @Column	public BigDecimal withdrew_cash_yesterday;
/**
* 19:withdrew_cash_7days
*/
	 @Column	public BigDecimal withdrew_cash_7days;
/**
* 20:withdrew_cash_total
*/
	 @Column	public BigDecimal withdrew_cash_total;
/**
* 21:status
*/
	 @Column	public Integer status;
/**
* 22:created_at
*/
	 @Column	public Date created_at;
/**
* 23:updated_at
*/
	 @Column	public Date updated_at;
/**
* 24:enabled
*/
	 @Column	public Boolean enabled;
	public Long getStatistics_shop_yesterday_id(){
		return statistics_shop_yesterday_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getRevenue_yesterday(){
		return revenue_yesterday;
	}
	public BigDecimal getRevenue_7days(){
		return revenue_7days;
	}
	public BigDecimal getRevenue_total(){
		return revenue_total;
	}
	public Integer getOrders_yesterday(){
		return orders_yesterday;
	}
	public Integer getOrders_7days(){
		return orders_7days;
	}
	public Integer getOrders_total(){
		return orders_total;
	}
	public Integer getReturns_yesterday(){
		return returns_yesterday;
	}
	public Integer getReturns_7days(){
		return returns_7days;
	}
	public Integer getReturns_total(){
		return returns_total;
	}
	public Integer getUv_yesterday(){
		return uv_yesterday;
	}
	public Integer getUv_7days(){
		return uv_7days;
	}
	public Integer getUv_total(){
		return uv_total;
	}
	public Integer getPv_yesterday(){
		return pv_yesterday;
	}
	public Integer getPv_7days(){
		return pv_7days;
	}
	public Integer getPv_total(){
		return pv_total;
	}
	public BigDecimal getWithdrew_cash_yesterday(){
		return withdrew_cash_yesterday;
	}
	public BigDecimal getWithdrew_cash_7days(){
		return withdrew_cash_7days;
	}
	public BigDecimal getWithdrew_cash_total(){
		return withdrew_cash_total;
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

