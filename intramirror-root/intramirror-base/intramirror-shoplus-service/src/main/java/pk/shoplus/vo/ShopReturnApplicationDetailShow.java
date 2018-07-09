package pk.shoplus.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import pk.shoplus.model.ReturnApplication;

public class ShopReturnApplicationDetailShow {

	/**
	 * return application 实体
	 */
	public ReturnApplication returnApplication;

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
	 * 付款总数
	 */
	public BigDecimal amount;

	/**
	 * 收益 revenue=Amount(Rs)-Shipping Cost(RS)-Subtotal Price(RS)-Coupons
	 * Price(RS)
	 */
	public BigDecimal revenue;

	/**
	 * 商品list
	 */
	public List<Map<String, Object>> productList;

	public ReturnApplication getReturnApplication() {
		return returnApplication;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public List<Map<String, Object>> getProductList() {
		return productList;
	}

}
