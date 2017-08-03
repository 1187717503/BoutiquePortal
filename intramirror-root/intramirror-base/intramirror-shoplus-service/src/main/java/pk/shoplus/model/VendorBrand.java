package pk.shoplus.model;

import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

/**
 * vendor_brand 实体类 Thu Dec 01 18:07:18 CST 2016
 */
@Entity("vendor_brand")
public class VendorBrand {
	/**
	 * 1:vendor_brand_id
	 */
	@Id
	public Long vendor_brand_id;
	/**
	 * 2:vendor_id
	 */
	@Column
	public Long vendor_id;
	/**
	 * 3:brand_id
	 */
	@Column
	public Long brand_id;
	/**
	 * 4:vendor_application_id
	 */
	@Column
	public Long vendor_application_id;
	/**
	 * 5:type
	 */
	@Column
	public Integer type;
	/**
	 * 6:authorizer
	 */
	@Column
	public String authorizer;
	/**
	 * 7:authorized_person
	 */
	@Column
	public String authorized_person;
	/**
	 * 8:valid_auth_duration
	 */
	@Column
	public Date valid_auth_duration;
	/**
	 * 9:authorization_pic
	 */
	@Column
	public String authorization_pic;
	/**
	 * 10:remark
	 */
	@Column
	public String remark;
	/**
	 * 11:brand_name
	 */
	@Column
	public String brand_name;
	/**
	 * 12:status
	 */
	@Column
	public Integer status;
	/**
	 * 13:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 14:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 15:enabled
	 */
	@Column
	public Boolean enabled;

	@Column
	public Date valid_auth_duration_end_at;

	public Date getValid_auth_duration_end_at() {
		return valid_auth_duration_end_at;
	}

	public Long getVendor_brand_id() {
		return vendor_brand_id;
	}

	public Long getVendor_id() {
		return vendor_id;
	}

	public Long getBrand_id() {
		return brand_id;
	}

	public Long getVendor_application_id() {
		return vendor_application_id;
	}

	public Integer getType() {
		return type;
	}

	public String getAuthorizer() {
		return authorizer;
	}

	public String getAuthorized_person() {
		return authorized_person;
	}

	public Date getValid_auth_duration() {
		return valid_auth_duration;
	}

	public String getAuthorization_pic() {
		return authorization_pic;
	}

	public String getRemark() {
		return remark;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public Integer getStatus() {
		return status;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public Boolean getEnabled() {
		return enabled;
	}
}
