package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

/**
 * api_log_batch 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
 @Entity("api_log_batch")
public class ApiLogBatch {
    @Id
    public Long api_log_batch_id;

    @Column public Long api_configuration_id;

    @Column	public Integer offset_num;

    @Column	public Integer limit_num;

    @Column	public Integer success_count;

    @Column	public Integer error_count;

    @Column	public Date create_time;

    @Column public String product_code ;

    @Column public String images ;

    public Long getApi_configuration_id() {
        return api_configuration_id;
    }

    public void setApi_configuration_id(Long api_configuration_id) {
        this.api_configuration_id = api_configuration_id;
    }

    public Long getApi_log_batch_id() {
        return api_log_batch_id;
    }

    public void setApi_log_batch_id(Long api_log_batch_id) {
        this.api_log_batch_id = api_log_batch_id;
    }

    public Integer getOffset_num() {
        return offset_num;
    }

    public void setOffset_num(Integer offset_num) {
        this.offset_num = offset_num;
    }

    public Integer getLimit_num() {
        return limit_num;
    }

    public void setLimit_num(Integer limit_num) {
        this.limit_num = limit_num;
    }

    public Integer getSuccess_count() {
        return success_count;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public void setSuccess_count(Integer success_count) {
        this.success_count = success_count;
    }

    public Integer getError_count() {
        return error_count;
    }

    public void setError_count(Integer error_count) {
        this.error_count = error_count;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}

