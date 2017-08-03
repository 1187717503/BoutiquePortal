package pk.shoplus.model;

import java.util.Date;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * product_sku_property 实体类 Mon Nov 14 12:55:11 CST 2016
 */
@Entity("product_sku_property")
public class ProductSkuProperty {
	/**
	 * 1:product_sku_property_id
	 */
	@Id
	public Long product_sku_property_id;
	/**
	 * 2:category_id
	 */
	@Column
	public Long category_id;
	/**
	 * 3:product_id
	 */
	@Column
	public Long product_id;
	/**
	 * 4:key_name
	 */
	@Column
	public String key_name;
	/**
	 * 5:type
	 */
	@Column
	public Integer type;
	/**
	 * 6:value
	 */
	@Column
	public String value;
	/**
	 * 7:remark
	 */
	@Column
	public String remark;
	/**
	 * 8:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 9:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 10:enabled
	 */
	@Column
	public Boolean enabled;

	public Long getProduct_sku_property_id() {
		return product_sku_property_id;
	}

	public Long getCategory_id() {
		return category_id;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public String getKey_name() {
		return key_name;
	}

	public Integer getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getRemark() {
		return remark;
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
