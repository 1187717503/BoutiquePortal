package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * vendor 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("vendor") 
public class Vendor{
/**
* 1:vendor_id
*/
	 @Id	public Long vendor_id;
/**
* 2:grade
*/
	 @Column	public Integer grade;
/**
* 3:vendor_name
*/
	 @Column	public String vendor_name;
/**
* 4:user_id
*/
	 @Column	public Long user_id;
/**
* 5:vendor_applicant_id
*/
	 @Column	public Long vendor_applicant_id;
/**
* 6:identity_card
*/
	 @Column	public String identity_card;
/**
* 7:brand_type
*/
	 @Column	public Integer brand_type;
/**
* 8:credit_card
*/
	 @Column	public String credit_card;
/**
* 9:status
*/
	 @Column	public Integer status;
/**
* 10:remark
*/
	 @Column	public String remark;
/**
* 11:company_name
*/
	 @Column	public String company_name;
/**
* 12:registered_person
*/
	 @Column	public String registered_person;
/**
* 13:electronic_identification_card
*/
	 @Column	public String electronic_identification_card;
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
* 20:updated_at
*/
	 @Column	public Date updated_at;
/**
* 21:created_at
*/
	 @Column	public Date created_at;
/**
* 22:enabled
*/
	 @Column	public Boolean enabled;
/**
* 23:last_login
*/
	 @Column	public Date last_login;
/**
* 24:national_mark
*/
	 @Column	public String national_mark;
/**
* 25:product_image_score
*/
	 @Column	public Integer product_image_score;
/**
* 26:vendor_score
*/
	 @Column	public Integer vendor_score;
/**
* 27:score
*/
	 @Column	public BigDecimal score;

	   @Column
	   public Long address_country_id;

	   public Long getAddress_country_id() {
		   return address_country_id;
	   }

	   public Long getVendor_id(){
		return vendor_id;
	}
	public Integer getGrade(){
		return grade;
	}
	public String getVendor_name(){
		return vendor_name;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getVendor_applicant_id(){
		return vendor_applicant_id;
	}
	public String getIdentity_card(){
		return identity_card;
	}
	public Integer getBrand_type(){
		return brand_type;
	}
	public String getCredit_card(){
		return credit_card;
	}
	public Integer getStatus(){
		return status;
	}
	public String getRemark(){
		return remark;
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
	public Date getUpdated_at(){
		return updated_at;
	}
	public Date getCreated_at(){
		return created_at;
	}
	public Boolean getEnabled(){
		return enabled;
	}
	public Date getLast_login(){
		return last_login;
	}
	public String getNational_mark(){
		return national_mark;
	}
	public Integer getProduct_image_score(){
		return product_image_score;
	}
	public Integer getVendor_score(){
		return vendor_score;
	}
	public BigDecimal getScore(){
		return score;
	}
}

