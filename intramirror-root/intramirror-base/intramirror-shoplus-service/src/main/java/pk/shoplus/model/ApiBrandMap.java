package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * brand 实体类
 * Thu Dec 01 18:07:14 CST 2016
 */
 @Entity("api_brand_map")
public class ApiBrandMap {
/**
* 1:api_brand_map_id
*/
  @Id	public Long api_brand_map_id;
/**
* 2:api_configuration_id
*/
  @Column	public Long api_configuration_id;
/**
* 3:brand_id
*/
  @Column	public Long brand_id;
/**
* 4:brand_name
*/
  @Column	public String brand_name;
/**
* 6:boutique_brand_name
*/
  @Column	public String boutique_brand_name;
  /**
  * 18:created_at
  */
  	 @Column	public Date created_at;
  	 @Column	public Date updated_at;
  	 @Column	public Boolean enabled;
	@Column	public Boolean no_img;

	public Long getApi_brand_map_id() {
		return api_brand_map_id;
	}

	public void setApi_brand_map_id(Long api_brand_map_id) {
		this.api_brand_map_id = api_brand_map_id;
	}

	public Long getApi_configuration_id() {
		return api_configuration_id;
	}

	public void setApi_configuration_id(Long api_configuration_id) {
		this.api_configuration_id = api_configuration_id;
	}

	public Long getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Long brand_id) {
		this.brand_id = brand_id;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getBoutique_brand_name() {
		return boutique_brand_name;
	}

	public void setBoutique_brand_name(String boutique_brand_name) {
		this.boutique_brand_name = boutique_brand_name;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getNo_img() {
		return no_img;
	}

	public void setNo_img(Boolean no_img) {
		this.no_img = no_img;
	}
}

