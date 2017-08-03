package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_vendor_sales_monthly 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_vendor_sales_monthly") 
public class StatisticsVendorSalesMonthly{
/**
* 1:statistics_vendor_sales_monthly_id
*/
	 @Id	public Long statistics_vendor_sales_monthly_id;
/**
* 2:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 3:account_month
*/
	 @Column	public Integer account_month;
/**
* 4:sales_fee
*/
	 @Column	public BigDecimal sales_fee;
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
/**
* 8:status
*/
	 @Column	public Integer status;
	public Long getStatistics_vendor_sales_monthly_id(){
		return statistics_vendor_sales_monthly_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Integer getAccount_month(){
		return account_month;
	}
	public BigDecimal getSales_fee(){
		return sales_fee;
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
	public Integer getStatus(){
		return status;
	}
}

