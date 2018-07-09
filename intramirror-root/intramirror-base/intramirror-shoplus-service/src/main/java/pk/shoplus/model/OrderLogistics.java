package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * order_logistics 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("order_logistics") 
public class OrderLogistics{
/**
* 1:order_logistics_id
*/
	 @Id	public Long order_logistics_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:order_id
*/
	 @Column	public Long order_id;
/**
* 4:destination_type
*/
	 @Column	public Integer destination_type;
/**
* 5:type
*/
	 @Column	public Integer type;
/**
* 6:ship_payer
*/
	 @Column	public Integer ship_payer;
/**
* 7:ship_fee
*/
	 @Column	public BigDecimal ship_fee;
/**
* 8:fee
*/
	 @Column	public BigDecimal fee;
/**
* 9:deduct_fee
*/
	 @Column	public BigDecimal deduct_fee;
/**
* 10:remain_fee
*/
	 @Column	public BigDecimal remain_fee;
/**
* 11:receive_fee
*/
	 @Column	public BigDecimal receive_fee;
/**
* 12:status
*/
	 @Column	public Integer status;
/**
* 13:logistics_id
*/
	 @Column	public Long logistics_id;
/**
* 14:return_application_id
*/
	 @Column	public Long return_application_id;
/**
* 15:remark
*/
	 @Column	public String remark;
/**
* 16:created_at
*/
	 @Column	public Date created_at;
/**
* 17:updated_at
*/
	 @Column	public Date updated_at;
/**
* 18:enabled
*/
	 @Column	public Boolean enabled;

	   @Column	public Boolean is_ship_fee;

	   public Boolean getIs_ship_fee() {
		   return is_ship_fee;
	   }

	   public Long getOrder_logistics_id(){
		return order_logistics_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getOrder_id(){
		return order_id;
	}
	public Integer getDestination_type(){
		return destination_type;
	}
	public Integer getType(){
		return type;
	}
	public Integer getShip_payer(){
		return ship_payer;
	}
	public BigDecimal getShip_fee(){
		return ship_fee;
	}
	public BigDecimal getFee(){
		return fee;
	}
	public BigDecimal getDeduct_fee(){
		return deduct_fee;
	}
	public BigDecimal getRemain_fee(){
		return remain_fee;
	}
	public BigDecimal getReceive_fee(){
		return receive_fee;
	}
	public Integer getStatus(){
		return status;
	}
	public Long getLogistics_id(){
		return logistics_id;
	}
	public Long getReturn_application_id(){
		return return_application_id;
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
}

