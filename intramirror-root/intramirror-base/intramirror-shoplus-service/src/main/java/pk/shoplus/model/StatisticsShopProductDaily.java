package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * statistics_shop_product_daily 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("statistics_shop_product_daily") 
public class StatisticsShopProductDaily{
/**
* 1:statistics_shop_product_daily_id
*/
	 @Id	public Long statistics_shop_product_daily_id;
/**
* 2:date
*/
	 @Column	public Date date;
/**
* 3:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 4:product_id
*/
	 @Column	public Long product_id;
/**
* 5:shop_id
*/
	 @Column	public Long shop_id;
/**
* 6:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 7:sales_amount
*/
	 @Column	public Integer sales_amount;
/**
* 8:sales_fee
*/
	 @Column	public BigDecimal sales_fee;
/**
* 9:in_price_fee
*/
	 @Column	public BigDecimal in_price_fee;
/**
* 10:return_amount
*/
	 @Column	public Integer return_amount;
/**
* 11:customer_rate
*/
	 @Column	public BigDecimal customer_rate;
/**
* 12:customer_rate_amount
*/
	 @Column	public Integer customer_rate_amount;
/**
* 13:shop_rate
*/
	 @Column	public BigDecimal shop_rate;
/**
* 14:shop_rate_amount
*/
	 @Column	public Integer shop_rate_amount;
/**
* 15:pv
*/
	 @Column	public Integer pv;
/**
* 16:uv
*/
	 @Column	public Integer uv;
/**
* 17:created_at
*/
	 @Column	public Date created_at;
/**
* 18:updated_at
*/
	 @Column	public Date updated_at;
/**
* 19:enabled
*/
	 @Column	public Boolean enabled;
	public Long getStatistics_shop_product_daily_id(){
		return statistics_shop_product_daily_id;
	}
	public Date getDate(){
		return date;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Integer getSales_amount(){
		return sales_amount;
	}
	public BigDecimal getSales_fee(){
		return sales_fee;
	}
	public BigDecimal getIn_price_fee(){
		return in_price_fee;
	}
	public Integer getReturn_amount(){
		return return_amount;
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
	public Integer getPv(){
		return pv;
	}
	public Integer getUv(){
		return uv;
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

