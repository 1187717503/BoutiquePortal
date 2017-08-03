package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_product 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_product") 
public class ScoringProduct{
/**
* 1:scoring_product_id
*/
	 @Id	public Long scoring_product_id;
/**
* 2:product_id
*/
	 @Column	public Long product_id;
/**
* 3:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 4:sales_amount
*/
	 @Column	public BigDecimal sales_amount;
/**
* 5:sales_fee
*/
	 @Column	public BigDecimal sales_fee;
/**
* 6:product_flow
*/
	 @Column	public BigDecimal product_flow;
/**
* 7:product_conversion
*/
	 @Column	public BigDecimal product_conversion;
/**
* 8:stock_days
*/
	 @Column	public BigDecimal stock_days;
/**
* 9:shop_rate
*/
	 @Column	public BigDecimal shop_rate;
/**
* 10:customer_rate
*/
	 @Column	public BigDecimal customer_rate;
/**
* 11:weighting
*/
	 @Column	public BigDecimal weighting;
/**
* 12:total_score
*/
	 @Column	public BigDecimal total_score;
/**
* 13:status
*/
	 @Column	public Integer status;
/**
* 14:created_at
*/
	 @Column	public Date created_at;
/**
* 15:updated_at
*/
	 @Column	public Date updated_at;
/**
* 16:remark
*/
	 @Column	public String remark;
/**
* 17:enabled
*/
	 @Column	public Boolean enabled;
	public Long getScoring_product_id(){
		return scoring_product_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public BigDecimal getSales_amount(){
		return sales_amount;
	}
	public BigDecimal getSales_fee(){
		return sales_fee;
	}
	public BigDecimal getProduct_flow(){
		return product_flow;
	}
	public BigDecimal getProduct_conversion(){
		return product_conversion;
	}
	public BigDecimal getStock_days(){
		return stock_days;
	}
	public BigDecimal getShop_rate(){
		return shop_rate;
	}
	public BigDecimal getCustomer_rate(){
		return customer_rate;
	}
	public BigDecimal getWeighting(){
		return weighting;
	}
	public BigDecimal getTotal_score(){
		return total_score;
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
	public String getRemark(){
		return remark;
	}
	public Boolean getEnabled(){
		return enabled;
	}
}

