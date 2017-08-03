package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * chat_user_content 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("chat_user_content") 
public class ChatUserContent{
/**
* 1:chat_user_content_id
*/
	 @Id	public Long chat_user_content_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:content
*/
	 @Column	public String content;
/**
* 4:chat_id
*/
	 @Column	public Long chat_id;
/**
* 5:send_time
*/
	 @Column	public Date send_time;
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
/**
* 9:remark
*/
	 @Column	public String remark;
	public Long getChat_user_content_id(){
		return chat_user_content_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public String getContent(){
		return content;
	}
	public Long getChat_id(){
		return chat_id;
	}
	public Date getSend_time(){
		return send_time;
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
	public String getRemark(){
		return remark;
	}
}

