package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_balance 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_balance") 
public class ShopBalance{
/**
* 1:shop_balance_id
*/
	 @Id	public Long shop_balance_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:balance
*/
	 @Column	public BigDecimal balance;
/**
* 4:arrear
*/
	 @Column	public BigDecimal arrear;
/**
* 5:freeze
*/
	 @Column	public BigDecimal freeze;
/**
* 6:created_at
*/
	 @Column	public Date created_at;
/**
* 7:updated_at
*/
	 @Column	public Date updated_at;
/**
* 8:enabled
*/
	 @Column	public Boolean enabled;
/**
* 9:revenues
*/
	 @Column	public BigDecimal revenues;
/**
* 10:pending
*/
	 @Column	public BigDecimal pending;
/**
* 11:given_balance
*/
	 @Column	public BigDecimal given_balance;
/**
* 12:debt
*/
	 @Column	public BigDecimal debt;
/**
* 13:withdrew_cash
*/
	 @Column	public BigDecimal withdrew_cash;
	public Long getShop_balance_id(){
		return shop_balance_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getBalance(){
		return balance;
	}
	public BigDecimal getArrear(){
		return arrear;
	}
	public BigDecimal getFreeze(){
		return freeze;
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
	public BigDecimal getRevenues(){
		return revenues;
	}
	public BigDecimal getPending(){
		return pending;
	}
	public BigDecimal getGiven_balance(){
		return given_balance;
	}
	public BigDecimal getDebt(){
		return debt;
	}
	public BigDecimal getWithdrew_cash(){
		return withdrew_cash;
	}
}

