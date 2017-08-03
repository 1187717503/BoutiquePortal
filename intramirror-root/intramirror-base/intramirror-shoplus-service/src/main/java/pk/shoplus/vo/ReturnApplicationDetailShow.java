package pk.shoplus.vo;

import java.util.List;

import pk.shoplus.model.Logistics;
import pk.shoplus.model.Order;
import pk.shoplus.model.ReturnApplication;

/**
*@author 作者：zhangxq
*
*/
public class ReturnApplicationDetailShow {
	
	public ReturnApplication returnApplication;
	
	public Order order;
	
	public OrderMessageShow orderMessageShow;
	
	public String shop_name;
	
	public Logistics logistics;
	
	public List<OrderProductShow> productList;
	
	public List<ReturnProductShow> returnProductList;
	
	

	public ReturnApplication getReturnApplication() {
		return returnApplication;
	}

	public Order getOrder() {
		return order;
	}

	public String getShop_name() {
		return shop_name;
	}

	public Logistics getLogistics() {
		return logistics;
	}

	public List<OrderProductShow> getProductList() {
		return productList;
	}

	public List<ReturnProductShow> getReturnProductList() {
		return returnProductList;
	}

	public OrderMessageShow getOrderMessageShow() {
		return orderMessageShow;
	}
	
	
	
	
}

