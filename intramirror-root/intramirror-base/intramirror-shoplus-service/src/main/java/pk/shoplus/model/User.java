package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * user 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("user") 
public class User{
/**
* 1:user_id
*/
	 @Id	public Long user_id;
/**
* 2:username
*/
	 @Column	public String username;
/**
* 3:email
*/
	 @Column	public String email;
/**
* 4:telephone
*/
	 @Column	public String telephone;
/**
* 5:contact_person_name
*/
	 @Column	public String contact_person_name;
/**
* 6:password
*/
	 @Column	public String password;
/**
* 7:identity_card
*/
	 @Column	public String identity_card;
/**
* 8:identity_card_name
*/
	 @Column	public String identity_card_name;
/**
* 9:electronic_registered_person_id
*/
	 @Column	public String electronic_registered_person_id;
/**
* 10:user_image
*/
	 @Column	public String user_image;
/**
* 11:source
*/
	 @Column	public Integer source;
/**
* 12:last_login
*/
	 @Column	public Date last_login;
/**
* 13:type
*/
	 @Column	public Integer type;
/**
* 14:created_at
*/
	 @Column	public Date created_at;
/**
* 15:updated_at
*/
	 @Column	public Date updated_at;
/**
* 16:enabled
*/
	 @Column	public Boolean enabled;
	 
	 @Column	public String wechat;

	 @Column	public String contact_phone;

	 @Column	public String area_code;
	 
	public Long getUser_id(){
		return user_id;
	}
	public String getUsername(){
		return username;
	}
	public String getEmail(){
		return email;
	}
	public String getTelephone(){
		return telephone;
	}
	public String getContact_person_name(){
		return contact_person_name;
	}
	public String getPassword(){
		return password;
	}
	public String getIdentity_card(){
		return identity_card;
	}
	public String getIdentity_card_name(){
		return identity_card_name;
	}
	public String getElectronic_registered_person_id(){
		return electronic_registered_person_id;
	}
	public String getUser_image(){
		return user_image;
	}
	public Integer getSource(){
		return source;
	}
	public Date getLast_login(){
		return last_login;
	}
	public Integer getType(){
		return type;
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
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	
	   public String getWechat() {
		   return wechat;
	   }

	   public void setWechat(String wechat) {
		   this.wechat = wechat;
	   }

	   public String getContact_phone() {
		   return contact_phone;
	   }

	   public void setContact_phone(String contact_phone) {
		   this.contact_phone = contact_phone;
	   }

	   public String getArea_code() {
		   return area_code;
	   }

	   public void setArea_code(String area_code) {
		   this.area_code = area_code;
	   }
	
}

