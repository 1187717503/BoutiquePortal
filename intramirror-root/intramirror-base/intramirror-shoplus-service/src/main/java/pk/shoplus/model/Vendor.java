package pk.shoplus.model;
import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

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
	   public String edd_desc;

	   @Column
	   public String edd_title;
//@Column public String vendor_code;

	   public String getVendor_code() {
		   return vendor_code;
	   }

//	 @Column public String vendor_code;

//	 @Column public String contact;

	   @Column
	   public Long address_country_id;
	   
	   
	/**
	 * contact(email)
	 */
	@Column
	public String contact;
	
	/**
	 * vendor_code
	 */
	@Column
	public String vendor_code;
	
		

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
	
	public String getContact() {
		return contact;
	}

	   public String getEdd_desc() {
		   return edd_desc;
	   }

	   public void setEdd_desc(String edd_desc) {
		   this.edd_desc = edd_desc;
	   }

	   public String getEdd_title() {
		   return edd_title;
	   }

	   public void setEdd_title(String edd_title) {
		   this.edd_title = edd_title;
	   }

	   public void setContact(String contact) {
		   this.contact = contact;
	   }

	   public void setVendor_code(String vendor_code) {
		   this.vendor_code = vendor_code;
	   }

	   //	public String getVendor_code() {
//		return vendor_code;
//	}
	

	   public void setVendor_id(Long vendor_id) {
		   this.vendor_id = vendor_id;
	   }

	   public void setGrade(Integer grade) {
		   this.grade = grade;
	   }

	   public void setVendor_name(String vendor_name) {
		   this.vendor_name = vendor_name;
	   }

	   public void setUser_id(Long user_id) {
		   this.user_id = user_id;
	   }

	   public void setVendor_applicant_id(Long vendor_applicant_id) {
		   this.vendor_applicant_id = vendor_applicant_id;
	   }

	   public void setIdentity_card(String identity_card) {
		   this.identity_card = identity_card;
	   }

	   public void setBrand_type(Integer brand_type) {
		   this.brand_type = brand_type;
	   }

	   public void setCredit_card(String credit_card) {
		   this.credit_card = credit_card;
	   }

	   public void setStatus(Integer status) {
		   this.status = status;
	   }

	   public void setRemark(String remark) {
		   this.remark = remark;
	   }

	   public void setCompany_name(String company_name) {
		   this.company_name = company_name;
	   }

	   public void setRegistered_person(String registered_person) {
		   this.registered_person = registered_person;
	   }

	   public void setElectronic_identification_card(String electronic_identification_card) {
		   this.electronic_identification_card = electronic_identification_card;
	   }

	   public void setBusiness_license_number(String business_license_number) {
		   this.business_license_number = business_license_number;
	   }

	   public void setBusiness_license_duration(Date business_license_duration) {
		   this.business_license_duration = business_license_duration;
	   }

	   public void setValid_end_at(Date valid_end_at) {
		   this.valid_end_at = valid_end_at;
	   }

	   public void setElectronic_business_license(String electronic_business_license) {
		   this.electronic_business_license = electronic_business_license;
	   }

	   public void setLegal_management_scope(String legal_management_scope) {
		   this.legal_management_scope = legal_management_scope;
	   }

	   public void setBusiness_license_location(String business_license_location) {
		   this.business_license_location = business_license_location;
	   }

	   public void setUpdated_at(Date updated_at) {
		   this.updated_at = updated_at;
	   }

	   public void setCreated_at(Date created_at) {
		   this.created_at = created_at;
	   }

	   public void setEnabled(Boolean enabled) {
		   this.enabled = enabled;
	   }

	   public void setLast_login(Date last_login) {
		   this.last_login = last_login;
	   }

	   public void setNational_mark(String national_mark) {
		   this.national_mark = national_mark;
	   }

	   public void setProduct_image_score(Integer product_image_score) {
		   this.product_image_score = product_image_score;
	   }

	   public void setVendor_score(Integer vendor_score) {
		   this.vendor_score = vendor_score;
	   }

	   public void setScore(BigDecimal score) {
		   this.score = score;
	   }


	   public void setAddress_country_id(Long address_country_id) {
		   this.address_country_id = address_country_id;
	   }
   }

