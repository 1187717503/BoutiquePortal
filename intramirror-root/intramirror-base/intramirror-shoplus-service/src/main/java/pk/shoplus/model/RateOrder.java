package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * rate_order 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("rate_order") 
public class RateOrder{
/**
* 1:rate_order_id
*/
	 @Id	public Long rate_order_id;
/**
* 2:order_id
*/
	 @Column	public Long order_id;
/**
* 3:shop_id
*/
	 @Column	public Long shop_id;
/**
* 4:user_id
*/
	 @Column	public Long user_id;
/**
* 5:comment_id
*/
	 @Column	public Long comment_id;
/**
* 6:remark
*/
	 @Column	public String remark;
/**
* 7:status
*/
	 @Column	public Integer status;
/**
* 8:created_at
*/
	 @Column	public Date created_at;
/**
* 9:updated_at
*/
	 @Column	public Date updated_at;
/**
* 10:enabled
*/
	 @Column	public Boolean enabled;
	public Long getRate_order_id(){
		return rate_order_id;
	}
	public Long getOrder_id(){
		return order_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getComment_id(){
		return comment_id;
	}
	public String getRemark(){
		return remark;
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

