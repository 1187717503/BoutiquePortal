package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_shop_sales_monthly 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_shop_sales_monthly") 
public class StatisticsShopSalesMonthly{
/**
* 1:statistics_shop_sales_monthly_id
*/
	 @Id	public Long statistics_shop_sales_monthly_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:account_month
*/
	 @Column	public Integer account_month;
/**
* 4:sales_fee
*/
	 @Column	public BigDecimal sales_fee;
/**
* 5:status
*/
	 @Column	public Integer status;
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
	public Long getStatistics_shop_sales_monthly_id(){
		return statistics_shop_sales_monthly_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Integer getAccount_month(){
		return account_month;
	}
	public BigDecimal getSales_fee(){
		return sales_fee;
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

