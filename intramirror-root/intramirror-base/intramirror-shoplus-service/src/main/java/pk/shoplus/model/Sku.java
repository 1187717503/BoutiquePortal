package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * sku 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("sku") 
public class Sku{
/**
* 1:sku_id
*/
	 @Id	public Long sku_id;
/**
* 2:product_id
*/
	 @Column	public Long product_id;
/**
* 3:sku_code
*/
	 @Column	public String sku_code;
/**
* 4:name
*/
	 @Column	public String name;
/**
* 5:coverpic
*/
	 @Column	public String coverpic;
/**
* 6:introduction
*/
	 @Column	public String introduction;
/**
* 7:in_price
*/
	 @Column	public BigDecimal in_price;
/**
* 8:price
*/
	 @Column	public BigDecimal price;
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

   @Column	public BigDecimal retail_price;
   
   /**
   * 14:im_price
   */
 @Column	public BigDecimal im_price;
 
	public Long getSku_id(){
		return sku_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public String getSku_code(){
		return sku_code;
	}
	public String getName(){
		return name;
	}
	public String getCoverpic(){
		return coverpic;
	}
	public String getIntroduction(){
		return introduction;
	}
	public BigDecimal getIn_price(){
		return in_price;
	}
	public BigDecimal getIm_price(){
		return im_price;
	}
	public BigDecimal getPrice(){
		return price;
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


	   public BigDecimal getRetail_price() {
		   return retail_price;
	   }

	   public void setRetail_price(BigDecimal retail_price) {
		   this.retail_price = retail_price;
	   }
   }

