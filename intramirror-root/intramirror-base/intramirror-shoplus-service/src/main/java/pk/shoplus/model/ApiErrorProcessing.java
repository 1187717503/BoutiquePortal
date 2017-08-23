package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.util.Date;

 @Entity("api_error_processing")
public class ApiErrorProcessing {
  @Id	public Long api_error_processing_id;
  @Column	public Long api_mq_id;
  @Column	public Long api_error_type_id;
    @Column	public String error_id;
    @Column	public String boutique_id;
    @Column	public String brand_id;
     @Column	public String sku_size;
     @Column	public String color_code;
    @Column	public String data_field;
    @Column	public String boutique_data;
    @Column	public Date create_time;
    @Column	public Date process_time;
    @Column	public Boolean no_process;
    @Column	public Boolean enabled;

    public Long getApi_error_processing_id() {
        return api_error_processing_id;
    }

    public void setApi_error_processing_id(Long api_error_processing_id) {
        this.api_error_processing_id = api_error_processing_id;
    }

    public Long getApi_mq_id() {
        return api_mq_id;
    }

    public void setApi_mq_id(Long api_mq_id) {
        this.api_mq_id = api_mq_id;
    }

    public Long getApi_error_type_id() {
        return api_error_type_id;
    }

    public void setApi_error_type_id(Long api_error_type_id) {
        this.api_error_type_id = api_error_type_id;
    }

    public String getError_id() {
        return error_id;
    }

    public void setError_id(String error_id) {
        this.error_id = error_id;
    }

    public String getBoutique_id() {
        return boutique_id;
    }

    public void setBoutique_id(String boutique_id) {
        this.boutique_id = boutique_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

     public String getData_field() {
         return data_field;
     }

     public void setData_field(String data_field) {
         this.data_field = data_field;
     }

     public String getBoutique_data() {
         return boutique_data;
     }

     public void setBoutique_data(String boutique_data) {
         this.boutique_data = boutique_data;
     }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getProcess_time() {
        return process_time;
    }

    public void setProcess_time(Date process_time) {
        this.process_time = process_time;
    }

     public Boolean getNo_process() {
         return no_process;
     }

     public void setNo_process(Boolean no_process) {
         this.no_process = no_process;
     }

     public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

     public String getSku_size() {
         return sku_size;
     }

     public void setSku_size(String sku_size) {
         this.sku_size = sku_size;
     }
 }

