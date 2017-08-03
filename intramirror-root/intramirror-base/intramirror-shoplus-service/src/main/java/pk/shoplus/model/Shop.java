package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * shop 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("shop") 
public class Shop{
/**
* 1:shop_id
*/
	 @Id	public Long shop_id;
/**
* 2:shop_name
*/
	 @Column	public String shop_name;
/**
* 3:shop_pic
*/
	 @Column	public String shop_pic;
/**
* 4:service_rating
*/
	 @Column	public BigDecimal service_rating;
/**
* 5:user_id
*/
	 @Column	public Long user_id;
/**
* 6:type
*/
	 @Column	public Integer type;
/**
* 7:shop_organization_application_id
*/
	 @Column	public Long shop_organization_application_id;
/**
* 8:shop_brand_type
*/
	 @Column	public Integer shop_brand_type;
/**
* 9:introduction
*/
	 @Column	public String introduction;
/**
* 10:status
*/
	 @Column	public Integer status;
/**
* 11:identity_card
*/
	 @Column	public String identity_card;
/**
* 12:company_bank_account
*/
	 @Column	public String company_bank_account;
/**
* 13:company_name
*/
	 @Column	public String company_name;
/**
* 14:registered_person
*/
	 @Column	public String registered_person;
/**
* 15:electronic_identification_card
*/
	 @Column	public String electronic_identification_card;
/**
* 16:business_license_number
*/
	 @Column	public String business_license_number;
/**
* 17:business_license_duration
*/
	 @Column	public Date business_license_duration;
/**
* 18:electronic_business_license
*/
	 @Column	public String electronic_business_license;
/**
* 19:legal_management_scope
*/
	 @Column	public String legal_management_scope;
/**
* 20:business_license_location
*/
	 @Column	public String business_license_location;
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
/**
* 24:returned_rate
*/
	 @Column	public BigDecimal returned_rate;
/**
* 25:balance
*/
	 @Column	public BigDecimal balance;
/**
* 26:given_balance
*/
	 @Column	public BigDecimal given_balance;
/**
* 27:revenue
*/
	 @Column	public BigDecimal revenue;
/**
* 28:banner_pic
*/
	 @Column	public String banner_pic;
/**
* 29:banner_shop_product_ids
*/
	 @Column	public String banner_shop_product_ids;
/**
* 30:grade
*/
	 @Column	public Integer grade;
/**
* 31:contact_person_name
*/
	 @Column	public String contact_person_name;
/**
* 32:score
*/
	 @Column	public BigDecimal score;
	public Long getShop_id(){
		return shop_id;
	}
	public String getShop_name(){
		return shop_name;
	}
	public String getShop_pic(){
		return shop_pic;
	}
	public BigDecimal getService_rating(){
		return service_rating;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Integer getType(){
		return type;
	}
	public Long getShop_organization_application_id(){
		return shop_organization_application_id;
	}
	public Integer getShop_brand_type(){
		return shop_brand_type;
	}
	public String getIntroduction(){
		return introduction;
	}
	public Integer getStatus(){
		return status;
	}
	public String getIdentity_card(){
		return identity_card;
	}
	public String getCompany_bank_account(){
		return company_bank_account;
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
	public BigDecimal getReturned_rate(){
		return returned_rate;
	}
	public BigDecimal getBalance(){
		return balance;
	}
	public BigDecimal getGiven_balance(){
		return given_balance;
	}
	public BigDecimal getRevenue(){
		return revenue;
	}
	public String getBanner_pic(){
		return banner_pic;
	}
	public String getBanner_shop_product_ids(){
		return banner_shop_product_ids;
	}
	public Integer getGrade(){
		return grade;
	}
	public String getContact_person_name(){
		return contact_person_name;
	}
	public BigDecimal getScore(){
		return score;
	}
}

