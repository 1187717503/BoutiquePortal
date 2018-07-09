package pk.shoplus.model;
import java.math.BigDecimal;

import pk.shoplus.model.annotation.*;

   /**
    * geography 实体类
    * 
    */ 
    @Entity("geography") 
public class Geography{
    	
	/**
	* 1:geography_id
	*/
		 @Id	public Long geography_id;
	/**
	* 2:name
	*/
		 @Column	public String name;
	 /**
	 * 3:ship_fee
	 */
	 	 @Column	public BigDecimal ship_fee;
	 /**
 	 * 4:tax_fee
 	 */
 	 	 @Column	public BigDecimal tax_fee;
	/**
	* 5:display_order
	*/
		 @Column	public Long display_order;
		 
		 @Column	public String english_name;
		 
		 
    
	public Long getDisplay_order() {
		return display_order;
	}
	public Long getGeography_id() {
		return geography_id;
	}
	public String getName() {
		return name;
	}
	public BigDecimal getShip_fee() {
		return ship_fee;
	}
	public BigDecimal getTax_fee() {
		return tax_fee;
	}
	public String getEnglish_name() {
		return english_name;
	}
   }

