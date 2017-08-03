package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * cheezmall_log 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("cheezmall_log") 
public class CheezmallLog{
/**
* 1:cheezmall_log_id
*/
	 @Id	public Long cheezmall_log_id;
/**
* 2:module
*/
	 @Column	public String module;
/**
* 3:content
*/
	 @Column	public String content;
/**
* 4:level
*/
	 @Column	public Integer level;
/**
* 5:status
*/
	 @Column	public Integer status;
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
	public Long getCheezmall_log_id(){
		return cheezmall_log_id;
	}
	public String getModule(){
		return module;
	}
	public String getContent(){
		return content;
	}
	public Integer getLevel(){
		return level;
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

