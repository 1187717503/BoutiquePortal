package com.intramirror.web.controller.order;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.order.api.service.IOrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private IOrderService orderService;
	
	@RequestMapping("/getOrderList")
	@ResponseBody
	public String getOrderList(){
		List<Map<String,Object>> result = orderService.getOrderList();
		
		String orderNumbers = "";
		
		//遍历获取所有的orderNumber
		if(result != null && result.size() > 0){
			for(Map<String, Object> orderInfo : result){
				orderNumbers += orderInfo.get("order_num").toString()+",";
			}
		}else{
			return new Gson().toJson(result);
		}
		
		if(StringUtils.isNoneBlank(orderNumbers)){
			orderNumbers = orderNumbers.substring(0,orderNumbers.length() -1);
		}
		
		List<Map<String,Object>> orderLineResult = orderService.getOrderListByOrderNumber(orderNumbers, 2);
		return new Gson().toJson(orderLineResult);
	}
	
}
