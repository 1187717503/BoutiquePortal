package pk.shoplus.model;
import java.util.*;
import java.math.BigDecimal;
import pk.shoplus.model.annotation.*;

   /**
    * report_shop_daily 实体类
    * Fri Dec 02 10:34:40 CST 2016
    */ 
    @Entity("report_shop_daily") 
public class ReportShopDaily{
/**
* 1:report_shop_daily_id
*/
	 @Id	public Long report_shop_daily_id;
/**
* 2:shop_id
*/
	 @Column	public Long shop_id;
/**
* 3:fee_yesterday
*/
	 @Column	public BigDecimal fee_yesterday;
/**
* 4:fee_7days
*/
	 @Column	public BigDecimal fee_7days;
/**
* 5:fee_15days
*/
	 @Column	public BigDecimal fee_15days;
/**
* 6:fee_30days
*/
	 @Column	public BigDecimal fee_30days;
/**
* 7:fee_total
*/
	 @Column	public BigDecimal fee_total;
/**
* 8:amount_yesterday
*/
	 @Column	public Integer amount_yesterday;
/**
* 9:amount_7days
*/
	 @Column	public Integer amount_7days;
/**
* 10:amount_15days
*/
	 @Column	public Integer amount_15days;
/**
* 11:amount_30days
*/
	 @Column	public Integer amount_30days;
/**
* 12:amount_total
*/
	 @Column	public Integer amount_total;
/**
* 13:flow_yesterday
*/
	 @Column	public Integer flow_yesterday;
/**
* 14:flow_7days
*/
	 @Column	public Integer flow_7days;
/**
* 15:flow_15days
*/
	 @Column	public Integer flow_15days;
/**
* 16:flow_30days
*/
	 @Column	public Integer flow_30days;
/**
* 17:flow_total
*/
	 @Column	public Integer flow_total;
/**
* 18:conversation_yesterday
*/
	 @Column	public BigDecimal conversation_yesterday;
/**
* 19:conversation_7days
*/
	 @Column	public BigDecimal conversation_7days;
/**
* 20:conversation_15days
*/
	 @Column	public BigDecimal conversation_15days;
/**
* 21:conversation_30days
*/
	 @Column	public BigDecimal conversation_30days;
/**
* 22:conversation_total
*/
	 @Column	public BigDecimal conversation_total;
/**
* 23:orders_yesterday
*/
	 @Column	public Integer orders_yesterday;
/**
* 24:orders_7days
*/
	 @Column	public Integer orders_7days;
/**
* 25:orders_15days
*/
	 @Column	public Integer orders_15days;
/**
* 26:orders_30days
*/
	 @Column	public Integer orders_30days;
/**
* 27:orders_total
*/
	 @Column	public Integer orders_total;
/**
* 28:status
*/
	 @Column	public Integer status;
/**
* 29:created_at
*/
	 @Column	public Date created_at;
/**
* 30:updated_at
*/
	 @Column	public Date updated_at;
/**
* 31:enabled
*/
	 @Column	public Boolean enabled;
	public Long getReport_shop_daily_id(){
		return report_shop_daily_id;
	}
	public Long getShop_id(){
		return shop_id;
	}
	public BigDecimal getFee_yesterday(){
		return fee_yesterday;
	}
	public BigDecimal getFee_7days(){
		return fee_7days;
	}
	public BigDecimal getFee_15days(){
		return fee_15days;
	}
	public BigDecimal getFee_30days(){
		return fee_30days;
	}
	public BigDecimal getFee_total(){
		return fee_total;
	}
	public Integer getAmount_yesterday(){
		return amount_yesterday;
	}
	public Integer getAmount_7days(){
		return amount_7days;
	}
	public Integer getAmount_15days(){
		return amount_15days;
	}
	public Integer getAmount_30days(){
		return amount_30days;
	}
	public Integer getAmount_total(){
		return amount_total;
	}
	public Integer getFlow_yesterday(){
		return flow_yesterday;
	}
	public Integer getFlow_7days(){
		return flow_7days;
	}
	public Integer getFlow_15days(){
		return flow_15days;
	}
	public Integer getFlow_30days(){
		return flow_30days;
	}
	public Integer getFlow_total(){
		return flow_total;
	}
	public BigDecimal getConversation_yesterday(){
		return conversation_yesterday;
	}
	public BigDecimal getConversation_7days(){
		return conversation_7days;
	}
	public BigDecimal getConversation_15days(){
		return conversation_15days;
	}
	public BigDecimal getConversation_30days(){
		return conversation_30days;
	}
	public BigDecimal getConversation_total(){
		return conversation_total;
	}
	public Integer getOrders_yesterday(){
		return orders_yesterday;
	}
	public Integer getOrders_7days(){
		return orders_7days;
	}
	public Integer getOrders_15days(){
		return orders_15days;
	}
	public Integer getOrders_30days(){
		return orders_30days;
	}
	public Integer getOrders_total(){
		return orders_total;
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

