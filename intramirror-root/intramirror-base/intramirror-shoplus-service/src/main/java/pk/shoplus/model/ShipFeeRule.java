package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * ship_fee_rule 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("ship_fee_rule") 
public class ShipFeeRule{
/**
* 1:ship_fee_rule_id
*/
	 @Id	public Long ship_fee_rule_id;
/**
* 2:type
*/
	 @Column	public Integer type;
/**
* 3:name
*/
	 @Column	public String name;
/**
* 4:payer
*/
	 @Column	public Integer payer;
/**
* 5:total_fee
*/
	 @Column	public BigDecimal total_fee;
/**
* 6:ship_fee_type
*/
	 @Column	public Integer ship_fee_type;
/**
* 7:ship_fee
*/
	 @Column	public BigDecimal ship_fee;
/**
* 8:remark
*/
	 @Column	public String remark;
/**
* 9:created_at
*/
	 @Column	public Date created_at;
/**
* 10:updated_at
*/
	 @Column	public Date updated_at;
/**
* 11:enabled
*/
	 @Column	public Boolean enabled;
/**
* 12:effective_at
*/
	 @Column	public Date effective_at;
/**
* 13:expiry_at
*/
	 @Column	public Date expiry_at;
	public Long getShip_fee_rule_id(){
		return ship_fee_rule_id;
	}
	public Integer getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	public Integer getPayer(){
		return payer;
	}
	public BigDecimal getTotal_fee(){
		return total_fee;
	}
	public Integer getShip_fee_type(){
		return ship_fee_type;
	}
	public BigDecimal getShip_fee(){
		return ship_fee;
	}
	public String getRemark(){
		return remark;
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
	public Date getEffective_at(){
		return effective_at;
	}
	public Date getExpiry_at(){
		return expiry_at;
	}
}

