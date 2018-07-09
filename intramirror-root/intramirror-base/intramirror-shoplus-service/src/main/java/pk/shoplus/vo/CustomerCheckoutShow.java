package pk.shoplus.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CustomerCheckoutShow {

	/**
	 * 接收姓名
	 */
	public String rec_name;
	
	/**
	 * 接收省份
	 */
	public String rec_province;
	
	/**
	 * 接收城市
	 */
	public String rec_city;
	
	/**
	 * 接收地区
	 */
	public String rec_area;
	
	/**
	 * 接收地址
	 */
	public String rec_addr;
	
	/**
	 * 接收电话
	 */
	public String rec_mobile;
	
	/**
	 * 商店id
	 */
	public Long shop_id;
	
	/**
	 * 商店图片
	 */
	public String shop_pic;
	
	/**
	 * 商店名
	 */
	public String shop_name;
	
	/**
	 * 商品总价钱
	 */
	public BigDecimal subTotalPrice;
	
	/**
	 * 优惠券价格
	 */
	public BigDecimal couponsPrice;
	
	/**
	 * 实际价格
	 */
	public BigDecimal actualPrice;
	
	/**
	 * 运费
	 */
	public BigDecimal shippingCost;
	
	/**
	 * 订单总价格
	 */
	public BigDecimal orderAmount;
	
	/**
	 * sku列表
	 */
	public List<Map<String, Object>> skuList;

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

	public String getShop_pic() {
		return shop_pic;
	}

	public String getShop_name() {
		return shop_name;
	}

	public BigDecimal getSubTotalPrice() {
		return subTotalPrice;
	}

	public BigDecimal getCouponsPrice() {
		return couponsPrice;
	}

	public BigDecimal getActualPrice() {
		return actualPrice;
	}

	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public List<Map<String, Object>> getSkuList() {
		return skuList;
	}

	public Long getShop_id() {
		return shop_id;
	}
	
	
}
