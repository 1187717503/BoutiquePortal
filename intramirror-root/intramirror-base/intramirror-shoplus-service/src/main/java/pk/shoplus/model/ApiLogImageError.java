package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * api_log_error 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
 @Entity("api_log_image_error")
public class ApiLogImageError {
    @Id
    public Long api_log_image_error_id;

    @Column	public String boutique_id;

    @Column	public Integer error_type;

    @Column public String images;

    @Column	public String error_info;

    @Column	public Date create_time;

    @Column public Boolean enabled;

    public Long getApi_log_image_error_id() {
        return api_log_image_error_id;
    }

    public void setApi_log_image_error_id(Long api_log_image_error_id) {
        this.api_log_image_error_id = api_log_image_error_id;
    }

    public String getBoutique_id() {
        return boutique_id;
    }

    public void setBoutique_id(String boutique_id) {
        this.boutique_id = boutique_id;
    }

    public Integer getError_type() {
        return error_type;
    }

    public void setError_type(Integer error_type) {
        this.error_type = error_type;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

