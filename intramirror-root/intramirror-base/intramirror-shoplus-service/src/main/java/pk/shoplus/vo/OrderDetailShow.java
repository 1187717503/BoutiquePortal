package pk.shoplus.vo;

import java.util.List;

import pk.shoplus.model.Logistics;
import pk.shoplus.model.Order;
import pk.shoplus.model.OrderLogistics;

/**
*@author 作者：zhangxq
*
*/
public class OrderDetailShow {
	
	public Order order;
	
	public String shop_name;
	
	public OrderLogistics orderLogistics;
	
	public Logistics logistics;
	
	public List<OrderProductShow> productList;
	
	public OrderMessageShow orderMessageShow;

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

	public OrderLogistics getOrderLogistics() {
		return orderLogistics;
	}

	public OrderMessageShow getOrderMessageShow() {
		return orderMessageShow;
	}


	
	
	
	
}

