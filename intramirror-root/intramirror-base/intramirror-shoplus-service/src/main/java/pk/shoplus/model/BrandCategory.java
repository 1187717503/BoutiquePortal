package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * brand_category 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("brand_category") 
public class BrandCategory{
/**
* 1:brand_category_id
*/
	 @Id	public Long brand_category_id;
/**
* 2:brand_id
*/
	 @Column	public Long brand_id;
/**
* 3:category_id
*/
	 @Column	public Long category_id;
/**
* 4:created_at
*/
	 @Column	public Date created_at;
/**
* 5:updated_at
*/
	 @Column	public Date updated_at;
/**
* 6:enabled
*/
	 @Column	public Boolean enabled;
	public Long getBrand_category_id(){
		return brand_category_id;
	}
	public Long getBrand_id(){
		return brand_id;
	}
	public Long getCategory_id(){
		return category_id;
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

