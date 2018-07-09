package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

/**
 * mapping_parma_set 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
 @Entity("api_end_point")
public class ApiEndpoint {
    @Id	public Long api_end_point_id;

    @Column	public String url;

    @Column	public String name;

    @Column	public Boolean enabled;

    public Long getApi_end_point_id() {
        return api_end_point_id;
    }

    public void setApi_end_point_id(Long api_end_point_id) {
        this.api_end_point_id = api_end_point_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

