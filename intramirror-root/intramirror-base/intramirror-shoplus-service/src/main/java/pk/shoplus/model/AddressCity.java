package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * address_city 实体类 Thu Dec 01 18:07:14 CST 2016
 */
@Entity("address_city")
public class AddressCity {
	/**
	 * 1:address_city_id
	 */
	@Id
	public Long address_city_id;
	/**
	 * 2:address_province_id
	 */
	@Column
	public Long address_province_id;
	/**
	 * 3:name
	 */
	@Column
	public String name;

	public Long getAddress_city_id() {
		return address_city_id;
	}

	public Long getAddress_province_id() {
		return address_province_id;
	}

	public String getName() {
		return name;
	}
}
