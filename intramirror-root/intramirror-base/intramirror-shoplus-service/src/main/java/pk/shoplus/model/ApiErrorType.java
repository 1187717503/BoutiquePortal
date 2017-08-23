package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

@Entity("api_error_type")
public class ApiErrorType {
    @Id	public Long api_error_type_id;
    @Column	public String error_type_name;
    @Column	public String error_type_desc;
    @Column	public Boolean enabled;

    public Long getApi_error_type_id() {
        return api_error_type_id;
    }

    public void setApi_error_type_id(Long api_error_type_id) {
        this.api_error_type_id = api_error_type_id;
    }

    public String getError_type_name() {
        return error_type_name;
    }

    public void setError_type_name(String error_type_name) {
        this.error_type_name = error_type_name;
    }

    public String getError_type_desc() {
        return error_type_desc;
    }

    public void setError_type_desc(String error_type_desc) {
        this.error_type_desc = error_type_desc;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

