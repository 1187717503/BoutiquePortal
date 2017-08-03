package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * api_log_error 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
 @Entity("api_log_error")
public class ApiLogError {
    @Id
    public Long api_log_error_id;

    @Column	public Long api_log_batch_id;

    @Column	public String boutique_id;

    @Column	public String error_info;

    @Column	public String josn_data;

    @Column	public Date create_time;
    public Long getApi_log_error_id() {
        return api_log_error_id;
    }

    public void setApi_log_error_id(Long api_log_error_id) {
        this.api_log_error_id = api_log_error_id;
    }

    public Long getApi_log_batch_id() {
        return api_log_batch_id;
    }

    public void setApi_log_batch_id(Long api_log_batch_id) {
        this.api_log_batch_id = api_log_batch_id;
    }

    public String getBoutique_id() {
        return boutique_id;
    }

    public void setBoutique_id(String boutique_id) {
        this.boutique_id = boutique_id;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public String getJosn_data() {
        return josn_data;
    }

    public void setJosn_data(String josn_data) {
        this.josn_data = josn_data;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}

