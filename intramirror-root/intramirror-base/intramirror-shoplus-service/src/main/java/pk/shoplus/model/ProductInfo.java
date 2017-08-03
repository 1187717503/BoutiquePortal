package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * product_info 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("product_info") 
public class ProductInfo{
/**
* 1:product_info_id
*/
	 @Id	public Long product_info_id;
/**
* 2:category_prodcut_info_id
*/
	 @Column	public Long category_prodcut_info_id;
/**
* 3:product_id
*/
	 @Column	public Long product_id;
/**
* 4:weight
*/
	 @Column	public Boolean weight;
/**
* 5:weight_amount
*/
	 @Column	public BigDecimal weight_amount;
/**
* 6:weight_unit
*/
	 @Column	public String weight_unit;
/**
* 7:length
*/
	 @Column	public Boolean length;
/**
* 8:length_amount
*/
	 @Column	public BigDecimal length_amount;
/**
* 9:length_unit
*/
	 @Column	public String length_unit;
/**
* 10:width
*/
	 @Column	public Boolean width;
/**
* 11:width_amount
*/
	 @Column	public BigDecimal width_amount;
/**
* 12:width_unit
*/
	 @Column	public String width_unit;
/**
* 13:height
*/
	 @Column	public Boolean height;
/**
* 14:height_amount
*/
	 @Column	public BigDecimal height_amount;
/**
* 15:height_unit
*/
	 @Column	public String height_unit;
/**
* 16:size
*/
	 @Column	public Boolean size;
/**
* 17:size_amount
*/
	 @Column	public BigDecimal size_amount;
/**
* 18:size_unit
*/
	 @Column	public String size_unit;
/**
* 19:other_property
*/
	 @Column	public String other_property;
/**
* 20:remark
*/
	 @Column	public String remark;
/**
* 21:created_at
*/
	 @Column	public Date created_at;
/**
* 22:updated_at
*/
	 @Column	public Date updated_at;
/**
* 23:enabled
*/
	 @Column	public Boolean enabled;
	public Long getProduct_info_id(){
		return product_info_id;
	}
	public Long getCategory_prodcut_info_id(){
		return category_prodcut_info_id;
	}
	public Long getProduct_id(){
		return product_id;
	}
	public Boolean getWeight(){
		return weight;
	}
	public BigDecimal getWeight_amount(){
		return weight_amount;
	}
	public String getWeight_unit(){
		return weight_unit;
	}
	public Boolean getLength(){
		return length;
	}
	public BigDecimal getLength_amount(){
		return length_amount;
	}
	public String getLength_unit(){
		return length_unit;
	}
	public Boolean getWidth(){
		return width;
	}
	public BigDecimal getWidth_amount(){
		return width_amount;
	}
	public String getWidth_unit(){
		return width_unit;
	}
	public Boolean getHeight(){
		return height;
	}
	public BigDecimal getHeight_amount(){
		return height_amount;
	}
	public String getHeight_unit(){
		return height_unit;
	}
	public Boolean getSize(){
		return size;
	}
	public BigDecimal getSize_amount(){
		return size_amount;
	}
	public String getSize_unit(){
		return size_unit;
	}
	public String getOther_property(){
		return other_property;
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

