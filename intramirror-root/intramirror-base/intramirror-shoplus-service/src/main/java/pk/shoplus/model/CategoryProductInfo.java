package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * category_product_info 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("category_product_info") 
public class CategoryProductInfo{
/**
* 1:category_product_info_id
*/
	 @Id	public Long category_product_info_id;
/**
* 2:category_id
*/
	 @Column	public Long category_id;
/**
* 3:weight
*/
	 @Column	public Boolean weight;
/**
* 4:weight_unit
*/
	 @Column	public String weight_unit;
/**
* 5:length
*/
	 @Column	public Boolean length;
/**
* 6:length_unit
*/
	 @Column	public String length_unit;
/**
* 7:width
*/
	 @Column	public Boolean width;
/**
* 8:width_unit
*/
	 @Column	public String width_unit;
/**
* 9:height
*/
	 @Column	public Boolean height;
/**
* 10:height_unit
*/
	 @Column	public String height_unit;
/**
* 11:size
*/
	 @Column	public Boolean size;
/**
* 12:size_unit
*/
	 @Column	public String size_unit;
/**
* 13:other_property
*/
	 @Column	public String other_property;
/**
* 14:commission
*/
	 @Column	public BigDecimal commission;
/**
* 15:agree_return_rate
*/
	 @Column	public BigDecimal agree_return_rate;
/**
* 16:remark
*/
	 @Column	public String remark;
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
	public Long getCategory_product_info_id(){
		return category_product_info_id;
	}
	public Long getCategory_id(){
		return category_id;
	}
	public Boolean getWeight(){
		return weight;
	}
	public String getWeight_unit(){
		return weight_unit;
	}
	public Boolean getLength(){
		return length;
	}
	public String getLength_unit(){
		return length_unit;
	}
	public Boolean getWidth(){
		return width;
	}
	public String getWidth_unit(){
		return width_unit;
	}
	public Boolean getHeight(){
		return height;
	}
	public String getHeight_unit(){
		return height_unit;
	}
	public Boolean getSize(){
		return size;
	}
	public String getSize_unit(){
		return size_unit;
	}
	public String getOther_property(){
		return other_property;
	}
	public BigDecimal getCommission(){
		return commission;
	}
	public BigDecimal getAgree_return_rate(){
		return agree_return_rate;
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

