package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * comment 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("comment") 
public class Comment{
/**
* 1:comment_id
*/
	 @Id	public Long comment_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:type
*/
	 @Column	public Integer type;
/**
* 4:reply_comment_id
*/
	 @Column	public Long reply_comment_id;
/**
* 5:root_id
*/
	 @Column	public Long root_id;
/**
* 6:content
*/
	 @Column	public String content;
/**
* 7:remark
*/
	 @Column	public String remark;
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
	public Long getComment_id(){
		return comment_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Integer getType(){
		return type;
	}
	public Long getReply_comment_id(){
		return reply_comment_id;
	}
	public Long getRoot_id(){
		return root_id;
	}
	public String getContent(){
		return content;
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

