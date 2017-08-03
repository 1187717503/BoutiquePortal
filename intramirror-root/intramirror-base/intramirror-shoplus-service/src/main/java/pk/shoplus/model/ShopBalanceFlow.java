package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_balance_flow 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_balance_flow") 
public class ShopBalanceFlow{
/**
* 1:shop_balance_flow_id
*/
	 @Id	public Long shop_balance_flow_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:arrear_before
*/
	 @Column	public BigDecimal arrear_before;
/**
* 4:freeze_before
*/
	 @Column	public BigDecimal freeze_before;
/**
* 5:balance_before
*/
	 @Column	public BigDecimal balance_before;
/**
* 6:amount
*/
	 @Column	public BigDecimal amount;
/**
* 7:flag
*/
	 @Column	public Integer flag;
/**
* 8:type
*/
	 @Column	public Integer type;
/**
* 9:arrear_after
*/
	 @Column	public BigDecimal arrear_after;
/**
* 10:freeze_after
*/
	 @Column	public BigDecimal freeze_after;
/**
* 11:balance_after
*/
	 @Column	public BigDecimal balance_after;
/**
* 12:remark
*/
	 @Column	public String remark;
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
* 16:enabled
*/
	 @Column	public Boolean enabled;
/**
* 17:product_id
*/
	 @Column	public Long product_id;
/**
* 18:order_id
*/
	 @Column	public Long order_id;
/**
* 19:return_application_id
*/
	 @Column	public Long return_application_id;
	public Long getShop_balance_flow_id(){
		return shop_balance_flow_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getArrear_before(){
		return arrear_before;
	}
	public BigDecimal getFreeze_before(){
		return freeze_before;
	}
	public BigDecimal getBalance_before(){
		return balance_before;
	}
	public BigDecimal getAmount(){
		return amount;
	}
	public Integer getFlag(){
		return flag;
	}
	public Integer getType(){
		return type;
	}
	public BigDecimal getArrear_after(){
		return arrear_after;
	}
	public BigDecimal getFreeze_after(){
		return freeze_after;
	}
	public BigDecimal getBalance_after(){
		return balance_after;
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
	public Long getProduct_id(){
		return product_id;
	}
	public Long getOrder_id(){
		return order_id;
	}
	public Long getReturn_application_id(){
		return return_application_id;
	}
}

