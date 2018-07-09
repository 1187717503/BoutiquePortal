package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * logistics 实体类
    * Thu Dec 01 18:07:15 CST 2016
    */ 
    @Entity("logistics") 
public class Logistics{
/**
* 1:logistics_id
*/
	 @Id	public Long logistics_id;
/**
* 2:company
*/
	 @Column	public String company;
/**
* 3:order_logistics_id
*/
	 @Column	public Long order_logistics_id;
/**
* 4:user_rec_name
*/
	 @Column	public String user_rec_name;
/**
* 5:user_rec_province
*/
	 @Column	public String user_rec_province;
/**
* 6:user_rec_city
*/
	 @Column	public String user_rec_city;
/**
* 7:user_rec_area
*/
	 @Column	public String user_rec_area;
/**
* 8:user_rec_addr
*/
	 @Column	public String user_rec_addr;
/**
* 9:user_rec_mobile
*/
	 @Column	public String user_rec_mobile;
/**
* 10:rec_name
*/
	 @Column	public String rec_name;
/**
* 11:rec_province
*/
	 @Column	public String rec_province;
/**
* 12:rec_city
*/
	 @Column	public String rec_city;
/**
* 13:rec_area
*/
	 @Column	public String rec_area;
/**
* 14:rec_addr
*/
	 @Column	public String rec_addr;
/**
* 15:rec_mobile
*/
	 @Column	public String rec_mobile;
/**
* 16:logistics_fee
*/
	 @Column	public BigDecimal logistics_fee;
/**
* 17:weight
*/
	 @Column	public Float weight;
/**
* 18:begin_time
*/
	 @Column	public Date begin_time;
/**
* 19:receive_time
*/
	 @Column	public Date receive_time;
/**
* 20:insure_fee
*/
	 @Column	public BigDecimal insure_fee;
/**
* 21:logistics_staff
*/
	 @Column	public String logistics_staff;
/**
* 22:logistics_url
*/
	 @Column	public String logistics_url;
/**
* 23:status
*/
	 @Column	public Integer status;
/**
* 24:created_at
*/
	 @Column	public Date created_at;
/**
* 25:updated_at
*/
	 @Column	public Date updated_at;
/**
* 26:enabled
*/
	 @Column	public Boolean enabled;
	public Long getLogistics_id(){
		return logistics_id;
	}
	public String getCompany(){
		return company;
	}
	public Long getOrder_logistics_id(){
		return order_logistics_id;
	}
	public String getUser_rec_name(){
		return user_rec_name;
	}
	public String getUser_rec_province(){
		return user_rec_province;
	}
	public String getUser_rec_city(){
		return user_rec_city;
	}
	public String getUser_rec_area(){
		return user_rec_area;
	}
	public String getUser_rec_addr(){
		return user_rec_addr;
	}
	public String getUser_rec_mobile(){
		return user_rec_mobile;
	}
	public String getRec_name(){
		return rec_name;
	}
	public String getRec_province(){
		return rec_province;
	}
	public String getRec_city(){
		return rec_city;
	}
	public String getRec_area(){
		return rec_area;
	}
	public String getRec_addr(){
		return rec_addr;
	}
	public String getRec_mobile(){
		return rec_mobile;
	}
	public BigDecimal getLogistics_fee(){
		return logistics_fee;
	}
	public Float getWeight(){
		return weight;
	}
	public Date getBegin_time(){
		return begin_time;
	}
	public Date getReceive_time(){
		return receive_time;
	}
	public BigDecimal getInsure_fee(){
		return insure_fee;
	}
	public String getLogistics_staff(){
		return logistics_staff;
	}
	public String getLogistics_url(){
		return logistics_url;
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

