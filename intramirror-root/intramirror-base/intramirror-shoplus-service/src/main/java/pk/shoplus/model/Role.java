package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * role 实体类
    * Thu Dec 01 18:07:16 CST 2016
    */ 
    @Entity("role") 
public class Role{
/**
* 1:role_id
*/
	 @Id	public Long role_id;
/**
* 2:role_name
*/
	 @Column	public String role_name;
/**
* 3:remark
*/
	 @Column	public String remark;
/**
* 4:privilege
*/
	 @Column	public Long privilege;
/**
* 5:operator
*/
	 @Column	public String operator;
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
* 9:can_show
*/
	 @Column	public Boolean can_show;
	public Long getRole_id(){
		return role_id;
	}
	public String getRole_name(){
		return role_name;
	}
	public String getRemark(){
		return remark;
	}
	public Long getPrivilege(){
		return privilege;
	}
	public String getOperator(){
		return operator;
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
	public Boolean getCan_show(){
		return can_show;
	}
}

