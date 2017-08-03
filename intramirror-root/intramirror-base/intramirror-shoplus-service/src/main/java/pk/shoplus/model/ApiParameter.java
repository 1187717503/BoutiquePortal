package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * mapping_parma 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
@Entity("api_parameter")
public class ApiParameter {
    @Id
    public Long api_paramater_id;

    @Column
    public String param_key;

    @Column
    public String param_value;

    @Column
    public Long api_end_point_id;
    
    @Column	public boolean enabled;

    public Long getApi_paramater_id() {
        return api_paramater_id;
    }

    public void setApi_paramater_id(Long api_paramater_id) {
        this.api_paramater_id = api_paramater_id;
    }

    public String getParam_key() {
        return param_key;
    }

    public void setParam_key(String param_key) {
        this.param_key = param_key;
    }

    public String getParam_value() {
        return param_value;
    }

    public void setParam_value(String param_value) {
        this.param_value = param_value;
    }

    public Long getApi_end_point_id() {
        return api_end_point_id;
    }

    public void setApi_end_point_id(Long api_end_point_id) {
        this.api_end_point_id = api_end_point_id;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
}

