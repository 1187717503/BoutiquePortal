package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * verify_code 实体类
    * Thu Dec 01 18:07:19 CST 2016
    */ 
    @Entity("verify_code") 
public class VerifyCode{
/**
* 1:verify_code_id
*/
	 @Id	public Long verify_code_id;
/**
* 2:telephone
*/
	 @Column	public String telephone;
/**
* 3:type
*/
	 @Column	public Integer type;
/**
* 4:code
*/
	 @Column	public String code;
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
	public Long getVerify_code_id(){
		return verify_code_id;
	}
	public String getTelephone(){
		return telephone;
	}
	public Integer getType(){
		return type;
	}
	public String getCode(){
		return code;
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

