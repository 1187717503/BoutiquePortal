package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_shop_operation_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_shop_operation_daily") 
public class StatisticsShopOperationDaily{
/**
* 1:statistics_shop_operation_daily_id
*/
	 @Id	public Long statistics_shop_operation_daily_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:account_date
*/
	 @Column	public Date account_date;
/**
* 4:revenue
*/
	 @Column	public BigDecimal revenue;
/**
* 5:order_amount
*/
	 @Column	public Integer order_amount;
/**
* 6:return_amount
*/
	 @Column	public Integer return_amount;
/**
* 7:pv
*/
	 @Column	public Integer pv;
/**
* 8:uv
*/
	 @Column	public Integer uv;
/**
* 9:withdrew_cash
*/
	 @Column	public BigDecimal withdrew_cash;
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
	 
	 @Column    public BigDecimal sales_amount;
	 
	public Long getStatistics_shop_operation_daily_id(){
		return statistics_shop_operation_daily_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Date getAccount_date(){
		return account_date;
	}
	public BigDecimal getRevenue(){
		return revenue;
	}
	public Integer getOrder_amount(){
		return order_amount;
	}
	public Integer getReturn_amount(){
		return return_amount;
	}
	public Integer getPv(){
		return pv;
	}
	public Integer getUv(){
		return uv;
	}
	public BigDecimal getWithdrew_cash(){
		return withdrew_cash;
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
	public BigDecimal getSales_amount() {
		return sales_amount;
	}
	
	
}

