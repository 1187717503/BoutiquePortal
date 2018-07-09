package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_shop_level 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_shop_level") 
public class ScoringShopLevel{
/**
* 1:scoring_shop_level_id
*/
	 @Id	public Long scoring_shop_level_id;
/**
* 2:level
*/
	 @Column	public String level;
/**
* 3:upper_limit
*/
	 @Column	public BigDecimal upper_limit;
/**
* 4:lower_limit
*/
	 @Column	public BigDecimal lower_limit;
/**
* 5:application_registration
*/
	 @Column	public String application_registration;
/**
* 6:product_amount
*/
	 @Column	public Integer product_amount;
/**
* 7:logistics_cost
*/
	 @Column	public Long logistics_cost;
/**
* 8:product_training
*/
	 @Column	public Integer product_training;
/**
* 9:service_line
*/
	 @Column	public Integer service_line;
/**
* 10:status
*/
	 @Column	public Integer status;
/**
* 11:created_at
*/
	 @Column	public Date created_at;
/**
* 12:updated_at
*/
	 @Column	public Date updated_at;
/**
* 13:enabled
*/
	 @Column	public Boolean enabled;
	public Long getScoring_shop_level_id(){
		return scoring_shop_level_id;
	}
	public String getLevel(){
		return level;
	}
	public BigDecimal getUpper_limit(){
		return upper_limit;
	}
	public BigDecimal getLower_limit(){
		return lower_limit;
	}
	public String getApplication_registration(){
		return application_registration;
	}
	public Integer getProduct_amount(){
		return product_amount;
	}
	public Long getLogistics_cost(){
		return logistics_cost;
	}
	public Integer getProduct_training(){
		return product_training;
	}
	public Integer getService_line(){
		return service_line;
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

