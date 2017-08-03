package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * brand 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("brand") 
public class Brand{
/**
* 1:brand_id
*/
	 @Id	public Long brand_id;
/**
* 2:english_name
*/
	 @Column	public String english_name;
/**
* 3:chinese_name
*/
	 @Column	public String chinese_name;
/**
* 4:logo
*/
	 @Column	public String logo;
/**
* 5:type
*/
	 @Column	public Integer type;
/**
* 6:description
*/
	 @Column	public String description;
/**
* 7:remark
*/
	 @Column	public String remark;
/**
* 8:creator
*/
	 @Column	public String creator;
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
/**
* 12:status
*/
	 @Column	public Integer status;
/**
* 13:vendor_application_id
*/
	 @Column	public Long vendor_application_id;

	   @Column public Integer hot_brand;

	   public Integer getHot_brand() {
		   return hot_brand;
	   }

	   public void setHot_brand(Integer hot_brand) {
		   this.hot_brand = hot_brand;
	   }

	   public Long getBrand_id(){
		return brand_id;
	}
	public String getEnglish_name(){
		return english_name;
	}
	public String getChinese_name(){
		return chinese_name;
	}
	public String getLogo(){
		return logo;
	}
	public Integer getType(){
		return type;
	}
	public String getDescription(){
		return description;
	}
	public String getRemark(){
		return remark;
	}
	public String getCreator(){
		return creator;
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
	public Integer getStatus(){
		return status;
	}
	public Long getVendor_application_id(){
		return vendor_application_id;
	}
}

