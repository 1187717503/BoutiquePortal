package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * order_message 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("order_message") 
public class OrderMessage{
/**
* 1:order_message_id
*/
	 @Id	public Long order_message_id;
/**
* 2:order_id
*/
	 @Column	public Long order_id;
/**
* 3:order_status
*/
	 @Column	public Integer order_status;
/**
* 4:user_id
*/
	 @Column	public Long user_id;
/**
* 5:user_type
*/
	 @Column	public Integer user_type;
/**
* 6:message
*/
	 @Column	public String message;
/**
* 7:created_at
*/
	 @Column	public Date created_at;
/**
* 8:updated_at
*/
	 @Column	public Date updated_at;
/**
* 9:enabled
*/
	 @Column	public Boolean enabled;
	public Long getOrder_message_id(){
		return order_message_id;
	}
	public Long getOrder_id(){
		return order_id;
	}
	public Integer getOrder_status(){
		return order_status;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Integer getUser_type(){
		return user_type;
	}
	public String getMessage(){
		return message;
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

