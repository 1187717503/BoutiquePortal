package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_sku_return_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_sku_return_daily") 
public class StatisticsSkuReturnDaily{
/**
* 1:statistics_sku_return_daily_id
*/
	 @Id	public Long statistics_sku_return_daily_id;
/**
* 2:sku_id
*/
	 @Column	public Long sku_id;
/**
* 3:return_amount
*/
	 @Column	public Integer return_amount;
/**
* 4:sales_amount
*/
	 @Column	public Integer sales_amount;
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
	public Long getStatistics_sku_return_daily_id(){
		return statistics_sku_return_daily_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public Integer getReturn_amount(){
		return return_amount;
	}
	public Integer getSales_amount(){
		return sales_amount;
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

