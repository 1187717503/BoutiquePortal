package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_shop_detail 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_shop_detail") 
public class ScoringShopDetail{
/**
* 1:scoring_shop_detail_id
*/
	 @Id	public Long scoring_shop_detail_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:last_month_sales_score
*/
	 @Column	public BigDecimal last_month_sales_score;
/**
* 4:total_sales_score
*/
	 @Column	public String total_sales_score;
/**
* 5:flow_score
*/
	 @Column	public BigDecimal flow_score;
/**
* 6:conversion_score
*/
	 @Column	public BigDecimal conversion_score;
/**
* 7:return_score
*/
	 @Column	public BigDecimal return_score;
/**
* 8:reject_score
*/
	 @Column	public BigDecimal reject_score;
/**
* 9:confirm_score
*/
	 @Column	public BigDecimal confirm_score;
/**
* 10:delivery_speed_score
*/
	 @Column	public BigDecimal delivery_speed_score;
/**
* 11:shop_service_score
*/
	 @Column	public BigDecimal shop_service_score;
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
	public Long getScoring_shop_detail_id(){
		return scoring_shop_detail_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getLast_month_sales_score(){
		return last_month_sales_score;
	}
	public String getTotal_sales_score(){
		return total_sales_score;
	}
	public BigDecimal getFlow_score(){
		return flow_score;
	}
	public BigDecimal getConversion_score(){
		return conversion_score;
	}
	public BigDecimal getReturn_score(){
		return return_score;
	}
	public BigDecimal getReject_score(){
		return reject_score;
	}
	public BigDecimal getConfirm_score(){
		return confirm_score;
	}
	public BigDecimal getDelivery_speed_score(){
		return delivery_speed_score;
	}
	public BigDecimal getShop_service_score(){
		return shop_service_score;
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

