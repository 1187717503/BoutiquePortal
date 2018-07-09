package pk.shoplus.vo;

import java.util.List;
import java.util.Map;

import pk.shoplus.model.OrderLogistics;

public class CustomerOrderLogisticsDetailShow {

	/**
	 * 订单 物流 实体
	 */
	public OrderLogistics orderLogistics;
	
	/**
	 * 显示的订单号
	 */
	public String order_num;
	
	/**
	 * 支付方式
	 */
	public Integer pay_way;

	/**
	 * 在线支付单号
	 */
	public Long online_payment_id;

	/**
	 * 订单状态
	 */
	public Integer order_status;

	/**
	 * 商店名字
	 */
	public String shop_name;

	/**
	 * 商店图片
	 */
	public String shop_pic;
	/**
	 * 商店店主 user id
	 */
	public Long shop_user_id;
	/**
	 * 收货人
	 */
	public String rec_name;
	/**
	 * 11:rec_province
	 */
	public String rec_province;
	/**
	 * 12:rec_city
	 */
	public String rec_city;
	/**
	 * 13:rec_area
	 */
	public String rec_area;
	/**
	 * 14:rec_addr
	 */
	public String rec_addr;
	/**
	 * 15:rec_mobile
	 */
	public String rec_mobile;
	
	/**
	 * 评价订单的id
	 */
	public Long rate_order_id;
	
	/**
	 * 评价订单中商店服务的评价id
	 */
	public Long rate_shop_id;
	
	/**
	 * 退换货申请id
	 */
	public Long return_application_id;
	
	public String customerMessage;
	
	public String shopMessage;
	
	
	/**
	 * 商品list
	 */
	public List<Map<String, Object>> skuList;
	

	public OrderLogistics getOrderLogistics() {
		return orderLogistics;
	}
	public Integer getPay_way() {
		return pay_way;
	}
	public Long getOnline_payment_id() {
		return online_payment_id;
	}
	public Integer getOrder_status() {
		return order_status;
	}
	public String getShop_name() {
		return shop_name;
	}
	public String getShop_pic() {
		return shop_pic;
	}
	public String getRec_name() {
		return rec_name;
	}
	public String getRec_province() {
		return rec_province;
	}
	public String getRec_city() {
		return rec_city;
	}
	public String getRec_area() {
		return rec_area;
	}
	public String getRec_addr() {
		return rec_addr;
	}
	public String getRec_mobile() {
		return rec_mobile;
	}
	public List<Map<String, Object>> getSkuList() {
		return skuList;
	}
	public Long getRate_order_id() {
		return rate_order_id;
	}
	public Long getReturn_application_id() {
		return return_application_id;
	}
	public String getCustomerMessage() {
		return customerMessage;
	}
	public String getShopMessage() {
		return shopMessage;
	}
	public Long getShop_user_id() {
		return shop_user_id;
	}
	public String getOrder_num() {
		return order_num;
	}
	public Long getRate_shop_id() {
		return rate_shop_id;
	}
	
	

}
