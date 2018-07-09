package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * vendor_application 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("vendor_application") 
public class VendorApplication{
/**
* 1:vendor_application_id
*/
	 @Id	public Long vendor_application_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 4:vendor_name
*/
	 @Column	public String vendor_name;
/**
* 5:telephone
*/
	 @Column	public String telephone;
/**
* 6:contact_person_name
*/
	 @Column	public String contact_person_name;
/**
* 7:identity_card
*/
	 @Column	public String identity_card;
/**
* 8:category_ids
*/
	 @Column	public String category_ids;
/**
* 9:brand_type
*/
	 @Column	public Integer brand_type;
/**
* 10:company_name
*/
	 @Column	public String company_name;
/**
* 11:registered_person
*/
	 @Column	public String registered_person;
/**
* 12:electronic_identification_card
*/
	 @Column	public String electronic_identification_card;
/**
* 13:operate_user_id
*/
	 @Column	public Long operate_user_id;
/**
* 14:business_license_number
*/
	 @Column	public String business_license_number;
/**
* 15:business_license_duration
*/
	 @Column	public Date business_license_duration;
/**
* 16:valid_end_at
*/
	 @Column	public Date valid_end_at;
/**
* 17:electronic_business_license
*/
	 @Column	public String electronic_business_license;
/**
* 18:legal_management_scope
*/
	 @Column	public String legal_management_scope;
/**
* 19:business_license_location
*/
	 @Column	public String business_license_location;
/**
* 20:created_at
*/
	 @Column	public Date created_at;
/**
* 21:updated_at
*/
	 @Column	public Date updated_at;
/**
* 22:enabled
*/
	 @Column	public Boolean enabled;
/**
* 23:status
*/
	 @Column	public Integer status;
/**
* 24:result_status
*/
	 @Column	public String result_status;
/**
* 25:reason
*/
	 @Column	public String reason;
/**
* 26:brand_ids
*/
	 @Column	public String brand_ids;
/**
* 27:step
*/
	 @Column	public Integer step;
/**
* 28:national_mark
*/
	 @Column	public String national_mark;
	public Long getVendor_application_id(){
		return vendor_application_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public String getVendor_name(){
		return vendor_name;
	}
	public String getTelephone(){
		return telephone;
	}
	public String getContact_person_name(){
		return contact_person_name;
	}
	public String getIdentity_card(){
		return identity_card;
	}
	public String getCategory_ids(){
		return category_ids;
	}
	public Integer getBrand_type(){
		return brand_type;
	}
	public String getCompany_name(){
		return company_name;
	}
	public String getRegistered_person(){
		return registered_person;
	}
	public String getElectronic_identification_card(){
		return electronic_identification_card;
	}
	public Long getOperate_user_id(){
		return operate_user_id;
	}
	public String getBusiness_license_number(){
		return business_license_number;
	}
	public Date getBusiness_license_duration(){
		return business_license_duration;
	}
	public Date getValid_end_at(){
		return valid_end_at;
	}
	public String getElectronic_business_license(){
		return electronic_business_license;
	}
	public String getLegal_management_scope(){
		return legal_management_scope;
	}
	public String getBusiness_license_location(){
		return business_license_location;
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
	public String getResult_status(){
		return result_status;
	}
	public String getReason(){
		return reason;
	}
	public String getBrand_ids(){
		return brand_ids;
	}
	public Integer getStep(){
		return step;
	}
	public String getNational_mark(){
		return national_mark;
	}
}

