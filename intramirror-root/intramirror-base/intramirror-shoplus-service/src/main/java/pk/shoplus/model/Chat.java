package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * chat 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("chat") 
public class Chat{
/**
* 1:chat_id
*/
	 @Id	public Long chat_id;
/**
* 2:created_at
*/
	 @Column	public Date created_at;
/**
* 3:updated_at
*/
	 @Column	public Date updated_at;
/**
* 4:enabled
*/
	 @Column	public Boolean enabled;
/**
* 5:chat_user_count
*/
	 @Column	public Integer chat_user_count;
/**
* 6:chat_user_list
*/
	 @Column	public String chat_user_list;
/**
* 7:remark
*/
	 @Column	public String remark;
	public Long getChat_id(){
		return chat_id;
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
	public Integer getChat_user_count(){
		return chat_user_count;
	}
	public String getChat_user_list(){
		return chat_user_list;
	}
	public String getRemark(){
		return remark;
	}
}

