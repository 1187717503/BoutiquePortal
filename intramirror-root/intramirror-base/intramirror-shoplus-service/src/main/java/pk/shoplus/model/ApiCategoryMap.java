package pk.shoplus.model;

import java.util.Date;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * mapping_category 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
@Entity("api_category_map")
public class ApiCategoryMap {
    @Id
    public Long api_category_map_id;

    @Column
    public Long api_configuration_id;

    @Column
    public Long category_id;

    @Column
    public String first_category;

    @Column
    public String second_category;

    @Column
    public String third_category;

    @Column
    public String boutique_first_category;

    @Column
    public String boutique_second_category;

    @Column
    public String boutique_third_category;

    public String getBoutique_category_id() {
        return boutique_category_id;
    }

    public void setBoutique_category_id(String boutique_category_id) {
        this.boutique_category_id = boutique_category_id;
    }

    @Column
    public String boutique_category_id;
    
	 @Column	public Date created_at;
	 @Column	public Date updated_at;
	 @Column	public Boolean enabled;

    public Long getApi_category_map_id() {
        return api_category_map_id;
    }

    public void setApi_category_map_id(Long api_category_map_id) {
        this.api_category_map_id = api_category_map_id;
    }

    public Long getApi_configuration_id() {
        return api_configuration_id;
    }

    public void setApi_configuration_id(Long api_configuration_id) {
        this.api_configuration_id = api_configuration_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public String getFirst_category() {
        return first_category;
    }

    public void setFirst_category(String first_category) {
        this.first_category = first_category;
    }

    public String getSecond_category() {
        return second_category;
    }

    public void setSecond_category(String second_category) {
        this.second_category = second_category;
    }

    public String getThird_category() {
        return third_category;
    }

    public void setThird_category(String third_category) {
        this.third_category = third_category;
    }

    public String getBoutique_first_category() {
        return boutique_first_category;
    }

    public void setBoutique_first_category(String boutique_first_category) {
        this.boutique_first_category = boutique_first_category;
    }

    public String getBoutique_second_category() {
        return boutique_second_category;
    }

    public void setBoutique_second_category(String boutique_second_category) {
        this.boutique_second_category = boutique_second_category;
    }

    public String getBoutique_third_category() {
        return boutique_third_category;
    }

    public void setBoutique_third_category(String boutique_third_category) {
        this.boutique_third_category = boutique_third_category;
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
    
    
}

