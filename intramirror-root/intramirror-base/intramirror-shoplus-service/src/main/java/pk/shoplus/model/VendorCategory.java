package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * vendor_category 实体类
    * Thu Dec 01 18:07:19 CST 2016
    */ 
    @Entity("vendor_category") 
public class VendorCategory{
/**
* 1:vendor_category_id
*/
	 @Id	public Long vendor_category_id;
/**
* 2:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 3:category_id
*/
	 @Column	public Long category_id;
/**
* 4:vendor_application_id
*/
	 @Column	public Long vendor_application_id;
/**
* 5:remark
*/
	 @Column	public String remark;
/**
* 6:category_name
*/
	 @Column	public String category_name;
/**
* 7:status
*/
	 @Column	public Integer status;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:enabled
*/
	 @Column	public Boolean enabled;
	public Long getVendor_category_id(){
		return vendor_category_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Long getCategory_id(){
		return category_id;
	}
	public Long getVendor_application_id(){
		return vendor_application_id;
	}
	public String getRemark(){
		return remark;
	}
	public String getCategory_name(){
		return category_name;
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

