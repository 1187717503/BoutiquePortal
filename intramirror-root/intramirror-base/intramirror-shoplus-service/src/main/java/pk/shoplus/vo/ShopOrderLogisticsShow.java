package pk.shoplus.vo;

import java.math.BigDecimal;

import pk.shoplus.model.OrderLogistics;

public class ShopOrderLogisticsShow {

	/**
	 * 订单 物流 实体
	 */
	public OrderLogistics orderLogistics;
	
	/**
	 * order显示的id
	 */
	public String order_num;
	
	/**
	 * 用户名
	 */
	public String username;
	
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
	 * 图片
	 */
	public String cover_img;
	
	/**
	 * 货品数量
	 */
	public Integer product_num;
	
	/**
	 * 付款总数
	 */
	public BigDecimal amount;

	
	/**
	 * 付款总数
	 */
	public BigDecimal subTotalPrice;
	
	
	/**
	 * 收益 revenue=Amount(Rs)-Shipping Cost(RS)-Subtotal Price(RS)-Coupons Price(RS)
	 */
	public BigDecimal revenue;

	public OrderLogistics getOrderLogistics() {
		return orderLogistics;
	}

	public String getUsername() {
		return username;
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

	public String getCover_img() {
		return cover_img;
	}

	public Integer getProduct_num() {
		return product_num;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public String getOrder_num() {
		return order_num;
	}

	public BigDecimal getSubTotalPrice() {
		return subTotalPrice;
	}
	
	
}
