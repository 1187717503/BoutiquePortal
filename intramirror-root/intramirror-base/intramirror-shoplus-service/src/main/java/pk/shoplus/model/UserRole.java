package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * user_role 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("user_role") 
public class UserRole{
/**
* 1:user_role_id
*/
	 @Id	public Long user_role_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:role_id
*/
	 @Column	public Long role_id;
/**
* 4:operator
*/
	 @Column	public String operator;
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
	public Long getUser_role_id(){
		return user_role_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public Long getRole_id(){
		return role_id;
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
}

