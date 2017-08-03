package pk.shoplus.model;

import java.util.Date;
import java.util.List;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * product_sku_property_key 实体类 Thu Dec 01 18:07:16 CST 2016
 */
@Entity("product_sku_property_key")
public class ProductSkuPropertyKey {
	/**
	 * 1:product_sku_property_key_id
	 */
	@Id
	public Long product_sku_property_key_id;
	/**
	 * 2:product_id
	 */
	@Column
	public Long product_id;
	/**
	 * 3:name
	 */
	@Column
	public String name;
	/**
	 * 4:remark
	 */
	@Column
	public String remark;
	/**
	 * 5:created_at
	 */
	@Column
	public Date created_at;
	/**
	 * 6:updated_at
	 */
	@Column
	public Date updated_at;
	/**
	 * 7:enabled
	 */
	@Column
	public Boolean enabled;
	/**
	 * 每个key中可选 value值
	 */
	public List<String> valueList;

	/**
	 * 每个key中的propertyvalue list;
	 */
	public List<ProductSkuPropertyValue> propertyValueList;

	public Long getProduct_sku_property_key_id() {
		return product_sku_property_key_id;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public String getName() {
		return name;
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

	public List<String> getValueList() {
		return valueList;
	}

	public List<ProductSkuPropertyValue> getPropertyValueList() {
		return propertyValueList;
	}

}
