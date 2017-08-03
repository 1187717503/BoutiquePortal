package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_vendor_sku_short_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_vendor_sku_short_daily") 
public class StatisticsVendorSkuShortDaily{
/**
* 1:statistics_vendor_sku_short_daily_id
*/
	 @Id	public Long statistics_vendor_sku_short_daily_id;
/**
* 2:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 3:sku_amount
*/
	 @Column	public Integer sku_amount;
/**
* 4:sku_short
*/
	 @Column	public Integer sku_short;
/**
* 5:rate
*/
	 @Column	public BigDecimal rate;
/**
* 6:status
*/
	 @Column	public Integer status;
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
	public Long getStatistics_vendor_sku_short_daily_id(){
		return statistics_vendor_sku_short_daily_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Integer getSku_amount(){
		return sku_amount;
	}
	public Integer getSku_short(){
		return sku_short;
	}
	public BigDecimal getRate(){
		return rate;
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

