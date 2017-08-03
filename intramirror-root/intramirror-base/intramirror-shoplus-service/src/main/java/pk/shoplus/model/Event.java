package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * event 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("event") 
public class Event{
/**
* 1:event_id
*/
	 @Id	public Long event_id;
/**
* 2:name
*/
	 @Column	public String name;
/**
* 3:type
*/
	 @Column	public String type;
/**
* 4:shop_id
*/
	 @Column	public Long shop_id;
/**
* 5:created_at
*/
	 @Column	public Date created_at;
/**
* 6:updated_at
*/
	 @Column	public Date updated_at;
/**
* 7:enabled
*/
	 @Column	public Boolean enabled;
/**
* 8:remark
*/
	 @Column	public String remark;
/**
* 9:start_at
*/
	 @Column	public Date start_at;
/**
* 10:end_at
*/
	 @Column	public Date end_at;
	public Long getEvent_id(){
		return event_id;
	}
	public String getName(){
		return name;
	}
	public String getType(){
		return type;
	}
	public Long getShop_id(){
		return shop_id;
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
	public Date getStart_at(){
		return start_at;
	}
	public Date getEnd_at(){
		return end_at;
	}
}

