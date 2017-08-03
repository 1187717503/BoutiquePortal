package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_vendor_level 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_vendor_level") 
public class ScoringVendorLevel{
/**
* 1:scoring_vendor_level_id
*/
	 @Id	public Long scoring_vendor_level_id;
/**
* 2:vendor_level
*/
	 @Column	public String vendor_level;
/**
* 3:upper_limit
*/
	 @Column	public BigDecimal upper_limit;
/**
* 4:lower_limit
*/
	 @Column	public BigDecimal lower_limit;
/**
* 5:product_amount
*/
	 @Column	public Integer product_amount;
/**
* 6:product_fee
*/
	 @Column	public Long product_fee;
/**
* 7:order_weighting
*/
	 @Column	public Integer order_weighting;
/**
* 8:application_registration
*/
	 @Column	public String application_registration;
/**
* 9:commission
*/
	 @Column	public Integer commission;
/**
* 10:service_charge
*/
	 @Column	public Integer service_charge;
/**
* 11:remark
*/
	 @Column	public String remark;
/**
* 12:status
*/
	 @Column	public Integer status;
/**
* 13:created_at
*/
	 @Column	public Date created_at;
/**
* 14:updated_at
*/
	 @Column	public Date updated_at;
/**
* 15:enabled
*/
	 @Column	public Boolean enabled;
	public Long getScoring_vendor_level_id(){
		return scoring_vendor_level_id;
	}
	public String getVendor_level(){
		return vendor_level;
	}
	public BigDecimal getUpper_limit(){
		return upper_limit;
	}
	public BigDecimal getLower_limit(){
		return lower_limit;
	}
	public Integer getProduct_amount(){
		return product_amount;
	}
	public Long getProduct_fee(){
		return product_fee;
	}
	public Integer getOrder_weighting(){
		return order_weighting;
	}
	public String getApplication_registration(){
		return application_registration;
	}
	public Integer getCommission(){
		return commission;
	}
	public Integer getService_charge(){
		return service_charge;
	}
	public String getRemark(){
		return remark;
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

