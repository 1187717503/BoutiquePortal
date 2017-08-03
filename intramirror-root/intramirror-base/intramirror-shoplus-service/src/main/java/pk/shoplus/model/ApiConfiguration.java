package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

/**
 * mapping 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
 @Entity("api_configuration")
public class ApiConfiguration {
    @Id
    public Long api_configuration_id;

    @Column	public Long vendor_id;

    @Column	public Long api_end_point_id;

    @Column	public String store_code;

    @Column	public String system;

    @Column	public boolean enabled;

    public Long getApi_configuration_id() {
        return api_configuration_id;
    }

    public void setApi_configuration_id(Long api_configuration_id) {
        this.api_configuration_id = api_configuration_id;
    }

    public Long getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Long vendor_id) {
        this.vendor_id = vendor_id;
    }

    public Long getApi_end_point_id() {
        return api_end_point_id;
    }

    public void setApi_end_point_id(Long api_end_point_id) {
        this.api_end_point_id = api_end_point_id;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

