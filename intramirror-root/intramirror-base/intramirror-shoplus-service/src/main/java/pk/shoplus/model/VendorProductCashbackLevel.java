package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * vendor_product_cashback_level 实体类
    * Thu Dec 01 18:07:19 CST 2016
    */ 
    @Entity("vendor_product_cashback_level") 
public class VendorProductCashbackLevel{
/**
* 1:vendor_product_cashback_level_id
*/
	 @Id	public Long vendor_product_cashback_level_id;
/**
* 2:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 3:product_id
*/
	 @Column	public Long product_id;
/**
* 4:shop_grade
*/
	 @Column	public Long shop_grade;
/**
* 5:type
*/
	 @Column	public Integer type;
/**
* 6:name
*/
	 @Column	public String name;
/**
* 7:full
*/
	 @Column	public Integer full;
/**
* 8:back
*/
	 @Column	public BigDecimal back;
/**
* 9:begin_at
*/
	 @Column	public Date begin_at;
/**
* 10:valid_at
*/
	 @Column	public Date valid_at;
/**
* 11:last_cash_back_at
*/
	 @Column	public Date last_cash_back_at;
/**
* 12:period
*/
	 @Column	public Integer period;
/**
* 13:remark
*/
	 @Column	public String remark;
/**
* 14:status
*/
	 @Column	public Integer status;
/**
* 15:created_at
*/
	 @Column	public Date created_at;
/**
* 16:updated_at
*/
	 @Column	public Date updated_at;
/**
* 17:enabled
*/
	 @Column	public Boolean enabled;
	public Long getVendor_product_cashback_level_id(){
		return vendor_product_cashback_level_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getShop_grade(){
		return shop_grade;
	}
	public Integer getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	public Integer getFull(){
		return full;
	}
	public BigDecimal getBack(){
		return back;
	}
	public Date getBegin_at(){
		return begin_at;
	}
	public Date getValid_at(){
		return valid_at;
	}
	public Date getLast_cash_back_at(){
		return last_cash_back_at;
	}
	public Integer getPeriod(){
		return period;
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
}

