package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * return_application 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("return_application") 
public class ReturnApplication{
/**
* 1:return_application_id
*/
	 @Id	public Long return_application_id;
/**
* 2:return_application_num
*/
	 @Column	public String return_application_num;
/**
* 3:source
*/
	 @Column	public Integer source;
/**
* 4:order_logistics_id
*/
	 @Column	public Long order_logistics_id;
/**
* 5:user_id
*/
	 @Column	public Long user_id;
/**
* 6:user_name
*/
	 @Column	public String user_name;
/**
* 7:return_type
*/
	 @Column	public Integer return_type;
/**
* 8:return_reason
*/
	 @Column	public String return_reason;
/**
* 9:appeal_status
*/
	 @Column	public Integer appeal_status;
/**
* 10:appeal_reason
*/
	 @Column	public String appeal_reason;
/**
* 11:appeal_at
*/
	 @Column	public Date appeal_at;
/**
* 12:pics
*/
	 @Column	public String pics;
/**
* 13:description
*/
	 @Column	public String description;
/**
* 14:fee
*/
	 @Column	public BigDecimal fee;
/**
* 15:reply
*/
	 @Column	public String reply;
/**
* 16:confirmed_at
*/
	 @Column	public Date confirmed_at;
/**
* 17:status
*/
	 @Column	public Integer status;
/**
* 18:shop_id
*/
	 @Column	public Long shop_id;
/**
* 19:shop_replyer
*/
	 @Column	public String shop_replyer;
/**
* 20:shop_reply
*/
	 @Column	public String shop_reply;
/**
* 21:shop_reply_at
*/
	 @Column	public Date shop_reply_at;
/**
* 22:mall_operator
*/
	 @Column	public String mall_operator;
/**
* 23:mall_reply
*/
	 @Column	public String mall_reply;
/**
* 24:mall_reply_at
*/
	 @Column	public Date mall_reply_at;
/**
* 25:created_at
*/
	 @Column	public Date created_at;
/**
* 26:updated_at
*/
	 @Column	public Date updated_at;
/**
* 27:enabled
*/
	 @Column	public Boolean enabled;
	public Long getReturn_application_id(){
		return return_application_id;
	}
	public String getReturn_application_num(){
		return return_application_num;
	}
	public Integer getSource(){
		return source;
	}
	public Long getOrder_logistics_id(){
		return order_logistics_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public String getUser_name(){
		return user_name;
	}
	public Integer getReturn_type(){
		return return_type;
	}
	public String getReturn_reason(){
		return return_reason;
	}
	public Integer getAppeal_status(){
		return appeal_status;
	}
	public String getAppeal_reason(){
		return appeal_reason;
	}
	public Date getAppeal_at(){
		return appeal_at;
	}
	public String getPics(){
		return pics;
	}
	public String getDescription(){
		return description;
	}
	public BigDecimal getFee(){
		return fee;
	}
	public String getReply(){
		return reply;
	}
	public Date getConfirmed_at(){
		return confirmed_at;
	}
	public Integer getStatus(){
		return status;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public String getShop_replyer(){
		return shop_replyer;
	}
	public String getShop_reply(){
		return shop_reply;
	}
	public Date getShop_reply_at(){
		return shop_reply_at;
	}
	public String getMall_operator(){
		return mall_operator;
	}
	public String getMall_reply(){
		return mall_reply;
	}
	public Date getMall_reply_at(){
		return mall_reply_at;
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

