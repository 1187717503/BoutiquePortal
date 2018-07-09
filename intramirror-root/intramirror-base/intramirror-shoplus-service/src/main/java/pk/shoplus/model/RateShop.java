package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * rate_shop 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("rate_shop") 
public class RateShop{
/**
* 1:rate_shop_id
*/
	 @Id	public Long rate_shop_id;
/**
* 2:rate_order_id
*/
	 @Column	public Long rate_order_id;
/**
* 3:user_id
*/
	 @Column	public Long user_id;
/**
* 4:shop_id
*/
	 @Column	public Long shop_id;
/**
* 5:comment_id
*/
	 @Column	public Long comment_id;
/**
* 6:rate
*/
	 @Column	public Integer rate;
/**
* 7:status
*/
	 @Column	public Integer status;
/**
* 8:remark
*/
	 @Column	public String remark;
/**
* 9:created_at
*/
	 @Column	public Date created_at;
/**
* 10:updated_at
*/
	 @Column	public Date updated_at;
/**
* 11:enabled
*/
	 @Column	public Boolean enabled;
	public Long getRate_shop_id(){
		return rate_shop_id;
	}
	public Long getRate_order_id(){
		return rate_order_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public Long getComment_id(){
		return comment_id;
	}
	public Integer getRate(){
		return rate;
	}
	public Integer getStatus(){
		return status;
	}
	public String getRemark(){
		return remark;
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

