package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * report_sku_daily 实体类
    * Thu Dec 01 19:59:33 CST 2016
    */ 
    @Entity("report_sku_daily") 
public class ReportSkuDaily{
/**
* 1:report_sku_daily_id
*/
	 @Id	public Long report_sku_daily_id;
/**
* 2:sku_id
*/
	 @Column	public Long sku_id;
/**
* 3:fee_yesterday
*/
	 @Column	public BigDecimal fee_yesterday;
/**
* 4:fee_7days
*/
	 @Column	public BigDecimal fee_7days;
/**
* 5:fee_15days
*/
	 @Column	public BigDecimal fee_15days;
/**
* 6:fee_30days
*/
	 @Column	public BigDecimal fee_30days;
/**
* 7:amount_yesterday
*/
	 @Column	public Integer amount_yesterday;
/**
* 8:amount_7days
*/
	 @Column	public Integer amount_7days;
/**
* 9:amount_15days
*/
	 @Column	public Integer amount_15days;
/**
* 10:amount_30days
*/
	 @Column	public Integer amount_30days;
/**
* 11:status
*/
	 @Column	public Integer status;
/**
* 12:created_at
*/
	 @Column	public Date created_at;
/**
* 13:updated_at
*/
	 @Column	public Date updated_at;
/**
* 14:enabled
*/
	 @Column	public Boolean enabled;
	public Long getReport_sku_daily_id(){
		return report_sku_daily_id;
	}
	public Long getSku_id(){
		return sku_id;
	}
	public BigDecimal getFee_yesterday(){
		return fee_yesterday;
	}
	public BigDecimal getFee_7days(){
		return fee_7days;
	}
	public BigDecimal getFee_15days(){
		return fee_15days;
	}
	public BigDecimal getFee_30days(){
		return fee_30days;
	}
	public Integer getAmount_yesterday(){
		return amount_yesterday;
	}
	public Integer getAmount_7days(){
		return amount_7days;
	}
	public Integer getAmount_15days(){
		return amount_15days;
	}
	public Integer getAmount_30days(){
		return amount_30days;
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

