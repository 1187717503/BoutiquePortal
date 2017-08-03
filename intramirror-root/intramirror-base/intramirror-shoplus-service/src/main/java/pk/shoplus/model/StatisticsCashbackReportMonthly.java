package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_cashback_report_monthly 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_cashback_report_monthly") 
public class StatisticsCashbackReportMonthly{
/**
* 1:statistics_cashback_report_monthly_id
*/
	 @Id	public Long statistics_cashback_report_monthly_id;
/**
* 2:account_month
*/
	 @Column	public Integer account_month;
/**
* 3:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 4:shop_id
*/
	 @Column	public Long shop_id;
/**
* 5:product_id
*/
	 @Column	public Long product_id;
/**
* 6:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 7:sales_amount
*/
	 @Column	public Integer sales_amount;
/**
* 8:in_price_total
*/
	 @Column	public BigDecimal in_price_total;
/**
* 9:shop_grade
*/
	 @Column	public Integer shop_grade;
/**
* 10:cashback_type
*/
	 @Column	public Integer cashback_type;
/**
* 11:cashback_full
*/
	 @Column	public Integer cashback_full;
/**
* 12:cashback_back
*/
	 @Column	public BigDecimal cashback_back;
/**
* 13:fee
*/
	 @Column	public BigDecimal fee;
/**
* 14:status
*/
	 @Column	public Integer status;
/**
* 15:created_at
*/
	 @Column	public Date created_at;
/**
* 16:updated_at
*/
	 @Column	public Date updated_at;
/**
* 17:enabled
*/
	 @Column	public Boolean enabled;
	public Long getStatistics_cashback_report_monthly_id(){
		return statistics_cashback_report_monthly_id;
	}
	public Integer getAccount_month(){
		return account_month;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Integer getSales_amount(){
		return sales_amount;
	}
	public BigDecimal getIn_price_total(){
		return in_price_total;
	}
	public Integer getShop_grade(){
		return shop_grade;
	}
	public Integer getCashback_type(){
		return cashback_type;
	}
	public Integer getCashback_full(){
		return cashback_full;
	}
	public BigDecimal getCashback_back(){
		return cashback_back;
	}
	public BigDecimal getFee(){
		return fee;
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

