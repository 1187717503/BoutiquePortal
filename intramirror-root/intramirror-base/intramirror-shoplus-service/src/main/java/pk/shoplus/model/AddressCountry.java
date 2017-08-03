package pk.shoplus.model;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * address_country 实体类
    * Thu Dec 01 18:07:14 CST 2016
    */ 
    @Entity("address_country") 
public class AddressCountry{
/**
* 1:address_country_id
*/
	 @Id	public Long address_country_id;
/**
* 2:name
*/
	 @Column	public String name;

   @Column
   public Long geography_id;

	public Long getAddress_country_id(){
		return address_country_id;
	}
	public String getName(){
		return name;
	}

   public Long getGeography_id() {
	   return geography_id;
   }
   }

