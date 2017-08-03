package pk.shoplus.vo;

import java.util.List;
import java.util.Map;

/**
 * @author 作者：zhangxq
 *
 */
public class OrderProductShow {

	public Long product_id;

	public String product_name;

	public List<Map<String, Object>> propertyNames;

	public List<Map<String, Object>> logisticsProductList;

	public Long getProduct_id() {
		return product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public List<Map<String, Object>> getLogisticsProductList() {
		return logisticsProductList;
	}

	public List<Map<String, Object>> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<Map<String, Object>> propertyNames) {
		this.propertyNames = propertyNames;
	}

}
