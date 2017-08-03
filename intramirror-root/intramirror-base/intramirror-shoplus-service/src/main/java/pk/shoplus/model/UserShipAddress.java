package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * user_ship_address 实体类
    * Thu Dec 01 18:07:18 CST 2016
    */ 
    @Entity("user_ship_address") 
public class UserShipAddress{
/**
* 1:user_ship_address_id
*/
	 @Id	public Long user_ship_address_id;
/**
* 2:user_id
*/
	 @Column	public Long user_id;
/**
* 3:name
*/
	 @Column	public String name;
/**
* 4:country
*/
	 @Column	public String country;
/**
* 5:province
*/
	 @Column	public String province;
/**
* 6:city
*/
	 @Column	public String city;
/**
* 7:area
*/
	 @Column	public String area;
/**
* 8:address
*/
	 @Column	public String address;
/**
* 9:telephone
*/
	 @Column	public String telephone;
/**
* 10:code
*/
	 @Column	public String code;
/**
* 11:flag
*/
	 @Column	public Boolean flag;
/**
* 12:status
*/
	 @Column	public Integer status;
/**
* 13:created_at
*/
	 @Column	public Date created_at;
/**
* 14:updated_at
*/
	 @Column	public Date updated_at;
/**
* 15:enabled
*/
	 @Column	public Boolean enabled;
	public Long getUser_ship_address_id(){
		return user_ship_address_id;
	}
	public Long getUser_id(){
		return user_id;
	}
	public String getName(){
		return name;
	}
	public String getCountry(){
		return country;
	}
	public String getProvince(){
		return province;
	}
	public String getCity(){
		return city;
	}
	public String getArea(){
		return area;
	}
	public String getAddress(){
		return address;
	}
	public String getTelephone(){
		return telephone;
	}
	public String getCode(){
		return code;
	}
	public Boolean getFlag(){
		return flag;
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

