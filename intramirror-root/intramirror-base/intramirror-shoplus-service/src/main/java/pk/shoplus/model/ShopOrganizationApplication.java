package pk.shoplus.model;

import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

/**
 * shop_organization_application 实体类 Thu Dec 01 18:07:17 CST 2016
 */
@Entity("shop_organization_application")
public class ShopOrganizationApplication {
	/**
	 * 1:shop_organization_application_id
	 */
	@Id
	public Long shop_organization_application_id;
	/**
	 * 2:shop_name
	 */
	@Column
	public String shop_name;
	/**
	 * 3:shop_pic
	 */
	@Column
	public String shop_pic;
	/**
	 * 4:user_id
	 */
	@Column
	public Long user_id;
	/**
	 * 5:brand_ids
	 */
	@Column
	public String brand_ids;
	/**
	 * 6:category_ids
	 */
	@Column
	public String category_ids;
	/**
	 * 7:identity_card
	 */
	@Column
	public String identity_card;
	/**
	 * 8:contact_person_name
	 */
	@Column
	public String contact_person_name;
	/**
	 * 9:company_bank_account
	 */
	@Column
	public String company_bank_account;
	/**
	 * 10:province
	 */
	@Column
	public String province;
	/**
	 * 11:city
	 */
	@Column
	public String city;
	/**
	 * 12:enabled
	 */
	@Column
	public Boolean enabled;
	/**
	 * 13:status
	 */
	@Column
	public Integer status;
	/**
	 * 14:type
	 */
	@Column
	public Integer type;
	/**
	 * 15:reason
	 */
	@Column
	public String reason;
	/**
	 * 16:operate_user_id
	 */
	@Column
	public Long operate_user_id;
	/**
	 * 17:company_name
	 */
	@Column
	public String company_name;
	/**
	 * 18:registered_person
	 */
	@Column
	public String registered_person;
	/**
	 * 19:electronic_identification_card
	 */
	@Column
	public String electronic_identification_card;
	/**
	 * 20:business_license_number
	 */
	@Column
	public String business_license_number;
	/**
	 * 21:business_license_duration
	 */
	@Column
	public Date business_license_duration;
	/**
	 * 22:electronic_business_license
	 */
	@Column
	public String electronic_business_license;
	/**
	 * 23:legal_management_scope
	 */
	@Column
	public String legal_management_scope;
	/**
	 * 24:business_license_location
	 */
	@Column
	public String business_license_location;
	/**
	 * 25:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 26:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 27:shop_brand_type
	 */
	@Column
	public Integer shop_brand_type;
	/**
	 * 28:telephone
	 */
	@Column
	public String telephone;
	/**
	 * 29:address
	 */
	@Column
	public String address;
	/**
	 * 30:shipping_contact_name
	 */
	@Column
	public String shipping_contact_name;
	
	@Column
	public Long promoter_id;

	public Long getPromoter_id() {
		return promoter_id;
	}

	public Long getShop_organization_application_id() {
		return shop_organization_application_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public String getShop_pic() {
		return shop_pic;
	}

	public Long getUser_id() {
		return user_id;
	}

	public String getBrand_ids() {
		return brand_ids;
	}

	public String getCategory_ids() {
		return category_ids;
	}

	public String getIdentity_card() {
		return identity_card;
	}

	public String getContact_person_name() {
		return contact_person_name;
	}

	public String getCompany_bank_account() {
		return company_bank_account;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getType() {
		return type;
	}

	public String getReason() {
		return reason;
	}

	public Long getOperate_user_id() {
		return operate_user_id;
	}

	public String getCompany_name() {
		return company_name;
	}

	public String getRegistered_person() {
		return registered_person;
	}

	public String getElectronic_identification_card() {
		return electronic_identification_card;
	}

	public String getBusiness_license_number() {
		return business_license_number;
	}

	public Date getBusiness_license_duration() {
		return business_license_duration;
	}

	public String getElectronic_business_license() {
		return electronic_business_license;
	}

	public String getLegal_management_scope() {
		return legal_management_scope;
	}

	public String getBusiness_license_location() {
		return business_license_location;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public Integer getShop_brand_type() {
		return shop_brand_type;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getAddress() {
		return address;
	}

	public String getShipping_contact_name() {
		return shipping_contact_name;
	}
}
