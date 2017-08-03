package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * chat_user_list 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("chat_user_list") 
public class ChatUserList{
/**
* 1:chat_user_list_id
*/
	 @Id	public Long chat_user_list_id;
/**
* 2:chat_id
*/
	 @Column	public Long chat_id;
/**
* 3:user_id
*/
	 @Column	public Long user_id;
/**
* 4:remark
*/
	 @Column	public String remark;
/**
* 5:chat_time
*/
	 @Column	public Date chat_time;
/**
* 6:created_at
*/
	 @Column	public Date created_at;
/**
* 7:updated_at
*/
	 @Column	public Date updated_at;
/**
* 8:enabled
*/
	 @Column	public Boolean enabled;
	public Long getChat_user_list_id(){
		return chat_user_list_id;
	}
	public Long getChat_id(){
		return chat_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public String getRemark(){
		return remark;
	}
	public Date getChat_time(){
		return chat_time;
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

