package pk.shoplus.model;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * address_province 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("address_province") 
public class AddressProvince{
/**
* 1:address_province_id
*/
	 @Id	public Long address_province_id;
/**
* 2:address_country_id
*/
	 @Column	public Long address_country_id;
/**
* 3:name
*/
	 @Column	public String name;
	public Long getAddress_province_id(){
		return address_province_id;
	}
	public Long getAddress_country_id(){
		return address_country_id;
	}
	public String getName(){
		return name;
	}
}

