package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_shop 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_shop") 
public class ScoringShop{
/**
* 1:scoring_shop_id
*/
	 @Id	public Long scoring_shop_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:sales_score
*/
	 @Column	public BigDecimal sales_score;
/**
* 4:dynamic_score
*/
	 @Column	public BigDecimal dynamic_score;
/**
* 5:score
*/
	 @Column	public BigDecimal score;
/**
* 6:level
*/
	 @Column	public Integer level;
/**
* 7:status
*/
	 @Column	public Integer status;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:enabled
*/
	 @Column	public Boolean enabled;
	public Long getScoring_shop_id(){
		return scoring_shop_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getSales_score(){
		return sales_score;
	}
	public BigDecimal getDynamic_score(){
		return dynamic_score;
	}
	public BigDecimal getScore(){
		return score;
	}
	public Integer getLevel(){
		return level;
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

