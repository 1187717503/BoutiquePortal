package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop_product_sku 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("shop_product_sku") 
public class ShopProductSku{
/**
* 1:shop_product_sku_id
*/
	 @Id	public Long shop_product_sku_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:shop_product_id
*/
	 @Column	public Long shop_product_id;
/**
* 4:sku_id
*/
	 @Column	public Long sku_id;
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
* 8:sale_price
*/
	 @Column	public BigDecimal sale_price;
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

	public Long getShop_product_sku_id(){
		return shop_product_sku_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getShop_product_id(){
		return shop_product_id;
	}
	public Long getSku_id(){
		return sku_id;
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
	public BigDecimal getSale_price(){
		return sale_price;
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

