package pk.shoplus.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerReturnApplicationDetailShow {

	/**
	 * 退换id
	 */
	public Long return_application_id;
	
	/**
	 * 显示退换的id
	 */
	public String return_application_num;
	
	/**
	 * 2:source
	 */
	public Integer source;

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
	 * 退货理由
	 */
	public String return_reason;
	
	/**
	 * 9:appeal_status
	 */
	public Integer appeal_status;
	/**
	 * 10:appeal_reason
	 */
	public String appeal_reason;
	/**
	 * 11:appeal_at
	 */
	public Date appeal_at;
	
	/**
	 * 20:shop_reply
	 */
	public String shop_reply;
	/**
	 * 21:shop_reply_at
	 */
	public Date shop_reply_at;
	
	public String mall_reply;
	/**
	 * 24:mall_reply_at
	 */
	public Date mall_reply_at;
	
	/**
	 * 上传图片
	 */
	public String pics;
	
	/**
	 * 描述
	 */
	public String description;
	

	/**
	 * 商店名字
	 */
	public String shop_name;

	/**
	 * 商店图片
	 */
	public String shop_pic;

	/**
	 * 商品list
	 */
	public List<Map<String, Object>> skuList;
	
	/**
	 * 创建时间
	 */
	public Date created_at;
	
	/**
	 * 更新时间
	 */
	public Date updated_at;

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

	public String getReturn_reason() {
		return return_reason;
	}

	public String getPics() {
		return pics;
	}

	public String getDescription() {
		return description;
	}

	public String getShop_name() {
		return shop_name;
	}

	public String getShop_pic() {
		return shop_pic;
	}

	public List<Map<String, Object>> getSkuList() {
		return skuList;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public String getReturn_application_num() {
		return return_application_num;
	}

	public Integer getAppeal_status() {
		return appeal_status;
	}

	public String getAppeal_reason() {
		return appeal_reason;
	}

	public Date getAppeal_at() {
		return appeal_at;
	}

	public String getShop_reply() {
		return shop_reply;
	}

	public Date getShop_reply_at() {
		return shop_reply_at;
	}

	public String getMall_reply() {
		return mall_reply;
	}

	public Date getMall_reply_at() {
		return mall_reply_at;
	}
	
	
}
