package pk.shoplus.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CustomerReturnApplicationListShow {

	/**
	 * 退换id
	 */
	public Long return_application_id;
	
	/**
	 * 显示的退换id
	 */
	public String return_application_num;
	
	/**
	 * 2:source
	 */
	public Integer source;
	
	/**
	 * 用户id
	 */
	public Long user_id;

	/**
	 * 订单号
	 */
	public Long order_logistics_id;

	/**
	 * 退换类型
	 */

	public Integer return_type;

	/**
	 * 金额
	 */
	public BigDecimal fee;

	/**
	 * 订单状态
	 */
	public Integer status;

	/**
	 * 商店名字
	 */
	public String shop_name;

	/**
	 * 商店图片
	 */
	public String shop_pic;
	
	/**
	 * 商店回复
	 */
	public String shop_reply;
	
	/**
	 * 商城回复
	 */
	public String mall_reply;
	
	public Integer appeal_status;
	/**
	 * 申请理由
	 */
	public String appeal_reason;

	/**
	 * 商品list
	 */
	public List<Map<String, Object>> skuList;

	public String getShop_name() {
		return shop_name;
	}

	public String getShop_pic() {
		return shop_pic;
	}

	public List<Map<String, Object>> getSkuList() {
		return skuList;
	}

	public Long getReturn_application_id() {
		return return_application_id;
	}

	public Integer getSource() {
		return source;
	}

	public Long getOrder_logistics_id() {
		return order_logistics_id;
	}

	public Integer getReturn_type() {
		return return_type;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public Integer getStatus() {
		return status;
	}

	public String getShop_reply() {
		return shop_reply;
	}

	public String getMall_reply() {
		return mall_reply;
	}
	
	public Integer getAppeal_status() {
		return appeal_status;
	}

	public String getAppeal_reason() {
		return appeal_reason;
	}

	public Long getUser_id() {
		return user_id;
	}

	public String getReturn_application_num() {
		return return_application_num;
	}

}
