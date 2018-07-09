package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_product_rate 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_product_rate") 
public class ScoringProductRate{
/**
* 1:scoring_product_rate_id
*/
	 @Id	public Long scoring_product_rate_id;
/**
* 2:product_id
*/
	 @Column	public Long product_id;
/**
* 3:return_num
*/
	 @Column	public Integer return_num;
/**
* 4:sales_num
*/
	 @Column	public Integer sales_num;
/**
* 5:customer_rate
*/
	 @Column	public BigDecimal customer_rate;
/**
* 6:customer_rate_amount
*/
	 @Column	public Integer customer_rate_amount;
/**
* 7:shop_rate
*/
	 @Column	public BigDecimal shop_rate;
/**
* 8:shop_rate_amount
*/
	 @Column	public Integer shop_rate_amount;
/**
* 9:logistics_rate
*/
	 @Column	public BigDecimal logistics_rate;
/**
* 10:customer_score
*/
	 @Column	public BigDecimal customer_score;
/**
* 11:shop_score
*/
	 @Column	public BigDecimal shop_score;
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
	public Long getScoring_product_rate_id(){
		return scoring_product_rate_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Integer getReturn_num(){
		return return_num;
	}
	public Integer getSales_num(){
		return sales_num;
	}
	public BigDecimal getCustomer_rate(){
		return customer_rate;
	}
	public Integer getCustomer_rate_amount(){
		return customer_rate_amount;
	}
	public BigDecimal getShop_rate(){
		return shop_rate;
	}
	public Integer getShop_rate_amount(){
		return shop_rate_amount;
	}
	public BigDecimal getLogistics_rate(){
		return logistics_rate;
	}
	public BigDecimal getCustomer_score(){
		return customer_score;
	}
	public BigDecimal getShop_score(){
		return shop_score;
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

