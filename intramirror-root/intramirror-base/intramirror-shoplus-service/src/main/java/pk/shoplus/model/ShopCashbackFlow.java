package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_cashback_flow 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_cashback_flow") 
public class ShopCashbackFlow{
/**
* 1:shop_cashback_flow_id
*/
	 @Id	public Long shop_cashback_flow_id;
/**
* 2:shop_balance_flow_id
*/
	 @Column	public String shop_balance_flow_id;
/**
* 3:shop_id
*/
	 @Column	public Long shop_id;
/**
* 4:vendor_id
*/
	 @Column	public Integer vendor_id;
/**
* 5:vendor_shop_cashback_level_id
*/
	 @Column	public Long vendor_shop_cashback_level_id;
/**
* 6:vendor_shop_cashback_level_name
*/
	 @Column	public String vendor_shop_cashback_level_name;
/**
* 7:cashback_type
*/
	 @Column	public Integer cashback_type;
/**
* 8:full
*/
	 @Column	public Integer full;
/**
* 9:back
*/
	 @Column	public BigDecimal back;
/**
* 10:period
*/
	 @Column	public Integer period;
/**
* 11:begin_time
*/
	 @Column	public Date begin_time;
/**
* 12:end_time
*/
	 @Column	public Date end_time;
/**
* 13:shop_sales_amout
*/
	 @Column	public Integer shop_sales_amout;
/**
* 14:shop_sales
*/
	 @Column	public Integer shop_sales;
/**
* 15:cashback_amount
*/
	 @Column	public Integer cashback_amount;
/**
* 16:cash_dest
*/
	 @Column	public Integer cash_dest;
/**
* 17:status
*/
	 @Column	public Integer status;
/**
* 18:remark
*/
	 @Column	public String remark;
/**
* 19:created_at
*/
	 @Column	public Date created_at;
/**
* 20:updated_at
*/
	 @Column	public Date updated_at;
/**
* 21:enabled
*/
	 @Column	public Boolean enabled;
	public Long getShop_cashback_flow_id(){
		return shop_cashback_flow_id;
	}
	public String getShop_balance_flow_id(){
		return shop_balance_flow_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Integer getVendor_id(){
		return vendor_id;
	}
	public Long getVendor_shop_cashback_level_id(){
		return vendor_shop_cashback_level_id;
	}
	public String getVendor_shop_cashback_level_name(){
		return vendor_shop_cashback_level_name;
	}
	public Integer getCashback_type(){
		return cashback_type;
	}
	public Integer getFull(){
		return full;
	}
	public BigDecimal getBack(){
		return back;
	}
	public Integer getPeriod(){
		return period;
	}
	public Date getBegin_time(){
		return begin_time;
	}
	public Date getEnd_time(){
		return end_time;
	}
	public Integer getShop_sales_amout(){
		return shop_sales_amout;
	}
	public Integer getShop_sales(){
		return shop_sales;
	}
	public Integer getCashback_amount(){
		return cashback_amount;
	}
	public Integer getCash_dest(){
		return cash_dest;
	}
	public Integer getStatus(){
		return status;
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

