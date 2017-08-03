package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_bank 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_bank") 
public class ShopBank{
/**
* 1:shop_bank_id
*/
	 @Id	public Long shop_bank_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:bank_name
*/
	 @Column	public String bank_name;
/**
* 4:account
*/
	 @Column	public String account;
/**
* 5:acct_name
*/
	 @Column	public String acct_name;
/**
* 6:is_default
*/
	 @Column	public Boolean is_default;
/**
* 7:security_code
*/
	 @Column	public String security_code;
/**
* 8:expired_at
*/
	 @Column	public Date expired_at;
/**
* 9:mobile
*/
	 @Column	public String mobile;
/**
* 10:created_at
*/
	 @Column	public Date created_at;
/**
* 11:updated_at
*/
	 @Column	public Date updated_at;
/**
* 12:enabled
*/
	 @Column	public Boolean enabled;
	public Long getShop_bank_id(){
		return shop_bank_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public String getBank_name(){
		return bank_name;
	}
	public String getAccount(){
		return account;
	}
	public String getAcct_name(){
		return acct_name;
	}
	public Boolean getIs_default(){
		return is_default;
	}
	public String getSecurity_code(){
		return security_code;
	}
	public Date getExpired_at(){
		return expired_at;
	}
	public String getMobile(){
		return mobile;
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

