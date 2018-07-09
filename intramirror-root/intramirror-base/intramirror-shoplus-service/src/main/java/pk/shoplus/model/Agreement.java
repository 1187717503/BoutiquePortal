package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * agreement 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("agreement") 
public class Agreement{
/**
* 1:agreement_id
*/
	 @Id	public Long agreement_id;
/**
* 2:type
*/
	 @Column	public Integer type;
/**
* 3:content_type
*/
	 @Column	public Integer content_type;
/**
* 4:url
*/
	 @Column	public String url;
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
	public Long getAgreement_id(){
		return agreement_id;
	}
	public Integer getType(){
		return type;
	}
	public Integer getContent_type(){
		return content_type;
	}
	public String getUrl(){
		return url;
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

