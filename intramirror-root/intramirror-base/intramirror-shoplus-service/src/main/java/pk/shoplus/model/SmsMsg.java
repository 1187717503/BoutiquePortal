package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * sms_msg 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("sms_msg") 
public class SmsMsg{
/**
* 1:sms_msg_id
*/
	 @Id	public Long sms_msg_id;
/**
* 2:type
*/
	 @Column	public Integer type;
/**
* 3:to_userid
*/
	 @Column	public Long to_userid;
/**
* 4:telephone
*/
	 @Column	public String telephone;
/**
* 5:content
*/
	 @Column	public String content;
/**
* 6:status
*/
	 @Column	public Integer status;
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
	public Long getSms_msg_id(){
		return sms_msg_id;
	}
	public Integer getType(){
		return type;
	}
	public Long getTo_userid(){
		return to_userid;
	}
	public String getTelephone(){
		return telephone;
	}
	public String getContent(){
		return content;
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

