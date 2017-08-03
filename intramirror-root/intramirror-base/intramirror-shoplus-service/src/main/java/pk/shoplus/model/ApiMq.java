package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

/**
 * mapping_parma_set 实体类
 * Thu May 15 14:45:15 CST 2017
 */
 @Entity("api_mq")
public class ApiMq {
    @Id	public Long api_mq_id;

	@Column	public Long api_configuration_id;

	@Column	public String name;

	@Column	public Boolean enabled;

	public Long getApi_configuration_id() {
		return api_configuration_id;
	}

    public Long getApi_mq_id() {
		return api_mq_id;
	}

    public Boolean getEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }



    public void setApi_configuration_id(Long api_configuration_id) {
		this.api_configuration_id = api_configuration_id;
	}

    public void setApi_mq_id(Long api_mq_id) {
		this.api_mq_id = api_mq_id;
	}

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }
}

