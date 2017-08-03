package pk.shoplus.model;
import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

   /**
    * shop_product 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop_product") 
public class ShopProduct{
/**
* 1:shop_product_id
*/
	 @Id	public Long shop_product_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:shop_category_id
*/
	 @Column	public Long shop_category_id;
/**
* 4:product_id
*/
	 @Column	public Long product_id;
/**
* 5:name
*/
	 @Column	public String name;
/**
* 6:coverpic
*/
	 @Column	public String coverpic;
/**
* 7:introduction
*/
	 @Column	public String introduction;
/**
* 8:sales_amount
*/
	 @Column	public Integer sales_amount;
/**
* 9:status
*/
	 @Column	public Integer status;
/**
* 10:sale_at
*/
	 @Column	public Date sale_at;
/**
* 11:created_at
*/
	 @Column	public Date created_at;
/**
* 12:updated_at
*/
	 @Column	public Date updated_at;
/**
* 13:enabled
*/
	 @Column	public Boolean enabled;

/**
* 14:product_group_id
*/
	@Column	public Long product_group_id;
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getShop_category_id(){
		return shop_category_id;
	}
	public Long getProduct_id(){
		return product_id;
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
	public Integer getSales_amount(){
		return sales_amount;
	}
	public Integer getStatus(){
		return status;
	}
	public Date getSale_at(){
		return sale_at;
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

	   public Long getProduct_group_id() {
		   return product_group_id;
	   }
   }

