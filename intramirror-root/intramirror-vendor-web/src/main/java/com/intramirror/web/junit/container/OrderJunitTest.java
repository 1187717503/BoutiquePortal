/**
 * 
 */
package com.intramirror.web.junit.container;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.web.controller.order.OrderController;
import com.intramirror.web.junit.BaseJunit4Test;

/**
 * @author 123
 *
 */
public class OrderJunitTest extends BaseJunit4Test{

	@Autowired
	private OrderController orderController;
	
	@Before  
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@After  
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
	@Test
	public void Test(){
		
		Map<String, Object> map = new HashMap<>();
		map.put("logisProductId", "123");
		map.put("status", "1");
//		ResultMessage message = orderController.updateOrderStatus(map);
//		System.out.println("updateOrderStatus=============>> result  : " +new Gson().toJson(message));
		
		HttpServletRequest httpRequest = null;
		ResultMessage message1 = orderController.getOrderCount(httpRequest);
		System.out.println("getOrderCount=============>> result  : " +new Gson().toJson(message1));

		
		ResultMessage message2 = orderController.getPackOrderList(map, httpRequest);
		System.out.println("getPackOrderList=============>> result  : " +new Gson().toJson(message2));

		ResultMessage message3 = orderController.packingCheckOrder(map, httpRequest);
		System.out.println("packingCheckOrder=============>> result  : " +new Gson().toJson(message3));
		
		map = new HashMap<>();
		map.put("logistics_product_id", "123");
		map.put("container_id", "123");
		ResultMessage message4 = orderController.deletePackingCheckOrder(map, httpRequest);
		System.out.println("deletePackingCheckOrder=============>> result  : " +new Gson().toJson(message4));
		
		map = new HashMap<>();
		map.put("logisProductId", "123");
		map.put("status", "1");
		ResultMessage message5 = orderController.orderRefundCallback(map);
		System.out.println("orderRefundCallback=============>> result  : " +new Gson().toJson(message5));
		
		
		map = new HashMap<>();
		map.put("logisProductId", "123");
		map.put("comments", "1");
		map.put("reason", "1");
		ResultMessage message6 = orderController.saveUserComment(map, httpRequest);
		System.out.println("saveUserComment=============>> result  : " +new Gson().toJson(message6));
		
		ResultMessage message7 = orderController.getExceptionType();
		System.out.println("getExceptionType=============>> result  : " +new Gson().toJson(message7));
		
		map = new HashMap<>();
		map.put("logisProductId", "123");
		map.put("status", "1");
		ResultMessage message8 = orderController.updateException(map);
		System.out.println("updateException=============>> result  : " +new Gson().toJson(message8));
		
		
	}
}
