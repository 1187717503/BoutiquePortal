package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_sku_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_sku_daily") 
public class StatisticsSkuDaily{
/**
* 1:statistics_sku_daily_id
*/
	 @Id	public Long statistics_sku_daily_id;
/**
* 2:sku_id
*/
	 @Column	public Long sku_id;
/**
* 3:fee
*/
	 @Column	public BigDecimal fee;
/**
* 4:return_amount
*/
	 @Column	public Integer return_amount;
/**
* 5:amount
*/
	 @Column	public Integer amount;
/**
* 6:account_day
*/
	 @Column	public Date account_day;
/**
* 7:status
*/
	 @Column	public Integer status;
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
	public Long getStatistics_sku_daily_id(){
		return statistics_sku_daily_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public BigDecimal getFee(){
		return fee;
	}
	public Integer getReturn_amount(){
		return return_amount;
	}
	public Integer getAmount(){
		return amount;
	}
	public Date getAccount_day(){
		return account_day;
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

