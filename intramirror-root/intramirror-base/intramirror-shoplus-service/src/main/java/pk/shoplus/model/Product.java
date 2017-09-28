package pk.shoplus.model;
import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

   /**
    * product 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("product") 
public class Product{
/**
* 1:product_id
*/
	 @Id	public Long product_id;
/**
* 2:category_id
*/
	 @Column	public Long category_id;
/**
* 3:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 4:brand_id
*/
	 @Column	public Long brand_id;
/**
* 5:product_code
*/
	 @Column	public String product_code;
/**
* 6:cover_img
*/
	 @Column	public String cover_img;
/**
* 7:name
*/
	 @Column	public String name;
/**
* 8:description
*/
	 @Column	public String description;
/**
* 9:description_img
*/
	 @Column	public String description_img;
/**
* 10:remark
*/
	 @Column	public String remark;
/**
* 11:status
*/
	 @Column	public Integer status;
/**
* 12:publish_at
*/
	 @Column	public Date publish_at;
/**
* 13:valid_at
*/
	 @Column	public Date valid_at;
/**
* 14:feature
*/
	 @Column	public Integer feature;
/**
* 15:rejected_reason
*/
	 @Column	public String rejected_reason;
/**
* 16:rejected_by
*/
	 @Column	public String rejected_by;
/**
* 17:rejected_at
*/
	 @Column	public Date rejected_at;
/**
* 18:created_at
*/
	 @Column	public Date created_at;
/**
* 19:updated_at
*/
	 @Column	public Date updated_at;
/**
* 20:enabled
*/
	 @Column	public Boolean enabled;
/**
* 21:customer_rating
*/
	 @Column	public BigDecimal customer_rating;
/**
* 22:shop_rating
*/
	 @Column	public BigDecimal shop_rating;
/**
* 23:score
*/
	 @Column	public BigDecimal score;
/**
* 24:product_group_id
*/
@Column	public Long product_group_id;

/**
* 25:img_modified
*/
	 @Column	public Boolean img_modified;

	   /**
		* 25:img_modified
		*/
	   @Column	public String season_code;

	   @Column public Date last_check;

	   @Column public BigDecimal min_retail_price;

	   @Column public BigDecimal max_retail_price;


	   public Long getProduct_id(){
		return product_id;
	}
	public Long getCategory_id(){
		return category_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public Long getBrand_id(){
		return brand_id;
	}
	public String getProduct_code(){
		return product_code;
	}
	public String getCover_img(){
		return cover_img;
	}
	public String getName(){
		return name;
	}
	public String getDescription(){
		return description;
	}
	public String getDescription_img(){
		return description_img;
	}
	public String getRemark(){
		return remark;
	}
	public Integer getStatus(){
		return status;
	}
	public Date getPublish_at(){
		return publish_at;
	}
	public Date getValid_at(){
		return valid_at;
	}
	public Integer getFeature(){
		return feature;
	}
	public String getRejected_reason(){
		return rejected_reason;
	}
	public String getRejected_by(){
		return rejected_by;
	}
	public Date getRejected_at(){
		return rejected_at;
	}
	public Date getCreated_at(){
		return created_at;
	}
	public Date getUpdated_at(){
		return updated_at;
	}
	public Boolean getEnabled(){
		return enabled;
	}
	public Boolean getImg_modified(){
		return img_modified;
	}
	
	public BigDecimal getCustomer_rating(){
		return customer_rating;
	}
	public BigDecimal getShop_rating(){
		return shop_rating;
	}
	public BigDecimal getScore(){
		return score;
	}
	public Long getProduct_group_id() {
		   return product_group_id;
	   }

	   public void setProduct_id(Long product_id) {
		   this.product_id = product_id;
	   }

	   public void setCategory_id(Long category_id) {
		   this.category_id = category_id;
	   }

	   public void setVendor_id(Long vendor_id) {
		   this.vendor_id = vendor_id;
	   }

	   public void setBrand_id(Long brand_id) {
		   this.brand_id = brand_id;
	   }

	   public void setProduct_code(String product_code) {
		   this.product_code = product_code;
	   }

	   public void setCover_img(String cover_img) {
		   this.cover_img = cover_img;
	   }

	   public void setName(String name) {
		   this.name = name;
	   }

	   public void setDescription(String description) {
		   this.description = description;
	   }

	   public void setDescription_img(String description_img) {
		   this.description_img = description_img;
	   }

	   public void setRemark(String remark) {
		   this.remark = remark;
	   }

	   public void setStatus(Integer status) {
		   this.status = status;
	   }

	   public void setPublish_at(Date publish_at) {
		   this.publish_at = publish_at;
	   }

	   public void setValid_at(Date valid_at) {
		   this.valid_at = valid_at;
	   }

	   public void setFeature(Integer feature) {
		   this.feature = feature;
	   }

	   public void setRejected_reason(String rejected_reason) {
		   this.rejected_reason = rejected_reason;
	   }

	   public void setRejected_by(String rejected_by) {
		   this.rejected_by = rejected_by;
	   }

	   public void setRejected_at(Date rejected_at) {
		   this.rejected_at = rejected_at;
	   }

	   public void setCreated_at(Date created_at) {
		   this.created_at = created_at;
	   }

	   public void setUpdated_at(Date updated_at) {
		   this.updated_at = updated_at;
	   }

	   public void setEnabled(Boolean enabled) {
		   this.enabled = enabled;
	   }

	   public void setCustomer_rating(BigDecimal customer_rating) {
		   this.customer_rating = customer_rating;
	   }

	   public void setShop_rating(BigDecimal shop_rating) {
		   this.shop_rating = shop_rating;
	   }

	   public void setScore(BigDecimal score) {
		   this.score = score;
	   }

	   public void setProduct_group_id(Long product_group_id) {
		   this.product_group_id = product_group_id;
	   }

	   public void setImg_modified(Boolean img_modified) {
		   this.img_modified = img_modified;
	   }

	   public String getSeason_code() {
		   return season_code;
	   }

	   public void setSeason_code(String season_code) {
		   this.season_code = season_code;
	   }

	   public Date getLast_check() {
		   return last_check;
	   }

	   public void setLast_check(Date last_check) {
		   this.last_check = last_check;
	   }

	   public BigDecimal getMin_retail_price() {
		   return min_retail_price;
	   }

	   public void setMin_retail_price(BigDecimal min_retail_price) {
		   this.min_retail_price = min_retail_price;
	   }

	   public BigDecimal getMax_retail_price() {
		   return max_retail_price;
	   }

	   public void setMax_retail_price(BigDecimal max_retail_price) {
		   this.max_retail_price = max_retail_price;
	   }
   }

