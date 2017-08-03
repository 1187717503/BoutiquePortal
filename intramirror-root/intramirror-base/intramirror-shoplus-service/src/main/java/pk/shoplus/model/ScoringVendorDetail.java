package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * scoring_vendor_detail 实体类
    * Thu Dec 01 18:07:17 CST 2016
    */ 
    @Entity("scoring_vendor_detail") 
public class ScoringVendorDetail{
/**
* 1:scoring_vendor_detail_id
*/
	 @Id	public Long scoring_vendor_detail_id;
/**
* 2:vendor_id
*/
	 @Column	public Long vendor_id;
/**
* 3:last_month_sales_score
*/
	 @Column	public BigDecimal last_month_sales_score;
/**
* 4:total_sales_score
*/
	 @Column	public BigDecimal total_sales_score;
/**
* 5:short_score
*/
	 @Column	public BigDecimal short_score;
/**
* 6:return_score
*/
	 @Column	public BigDecimal return_score;
/**
* 7:active_selling_score
*/
	 @Column	public BigDecimal active_selling_score;
/**
* 8:picture_content_score
*/
	 @Column	public BigDecimal picture_content_score;
/**
* 9:vendor_score
*/
	 @Column	public BigDecimal vendor_score;
/**
* 10:overall_product_score
*/
	 @Column	public BigDecimal overall_product_score;
/**
* 11:status
*/
	 @Column	public Integer status;
/**
* 12:created_at
*/
	 @Column	public Date created_at;
/**
* 13:updated_at
*/
	 @Column	public Date updated_at;
/**
* 14:enabled
*/
	 @Column	public Boolean enabled;
	public Long getScoring_vendor_detail_id(){
		return scoring_vendor_detail_id;
	}
	public Long getVendor_id(){
		return vendor_id;
	}
	public BigDecimal getLast_month_sales_score(){
		return last_month_sales_score;
	}
	public BigDecimal getTotal_sales_score(){
		return total_sales_score;
	}
	public BigDecimal getShort_score(){
		return short_score;
	}
	public BigDecimal getReturn_score(){
		return return_score;
	}
	public BigDecimal getActive_selling_score(){
		return active_selling_score;
	}
	public BigDecimal getPicture_content_score(){
		return picture_content_score;
	}
	public BigDecimal getVendor_score(){
		return vendor_score;
	}
	public BigDecimal getOverall_product_score(){
		return overall_product_score;
	}
	public Integer getStatus(){
		return status;
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
}

