package com.intramirror.web.controller.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.web.service.LogisticsProductService;

@Controller
@RequestMapping("/order")
public class OrderController {

	private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	 
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private LogisticsProductService logisticsProductService;
	
	@Autowired
	private ProductPropertyService productPropertyService;
	
	
	@RequestMapping("/getOrderList")
	@ResponseBody
	public List<Map<String,Object>> getOrderList(){
		
		int status = 2;
		//获取order列表
		List<Map<String,Object>> result = orderService.getOrderList(status);
		
		String orderNumbers = "";
		
		//遍历获取所有的orderNumber
		if(result != null && result.size() > 0){
			for(Map<String, Object> orderInfo : result){
				orderNumbers += orderInfo.get("order_num").toString()+",";
			}
		}else{
			return result;
		}
		
		if(StringUtils.isNoneBlank(orderNumbers)){
			orderNumbers = orderNumbers.substring(0,orderNumbers.length() -1);
		}
		
		
		//根据orderNumber 获取orderLine信息
		List<Map<String,Object>> orderLineResult = orderService.getOrderListByOrderNumber(orderNumbers, status);
		
		if(orderLineResult != null && orderLineResult.size() > 0){
			
			/**------------------------------------优化----------------------------------------*/
			//遍历获取所有商品ID
			String productIds = "";
			for(Map<String, Object> info : orderLineResult){
				productIds +=info.get("product_id").toString()+",";
			}
			
			if(StringUtils.isNoneBlank(productIds)){
				productIds = productIds.substring(0,productIds.length() -1);
			}
			
			//根据ID列表获取商品属性
			List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(productIds);
			Map<String, Map<String, String>> productPropertyResult= new HashMap<String, Map<String, String>>();
			
			for(Map<String, Object> productProperty : productPropertyList){
				
				//如果存在
				if(productPropertyResult.containsKey(productProperty.get("product_id").toString())){
					Map<String, String> info = productPropertyResult.get(productProperty.get("product_id").toString());
				    info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
				}else{
					Map<String, String> info = new HashMap<String, String>();
					info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
					productPropertyResult.put(productProperty.get("product_id").toString(), info);
				}
				
			}
			/**------------------------------------优化end----------------------------------------*/
			
			
			
			for(Map<String, Object> orderInfo : result){
				List<Map<String, Object>> orderLineList = new ArrayList<Map<String,Object>>();
				int amount = 0;
				for(Map<String, Object> info : orderLineResult){
					if(orderInfo.get("order_num").toString().equals(info.get("order_num").toString())){
						//累加数量
						amount +=Integer.parseInt(info.get("amount").toString());
						//汇率
						Double rate =  Double.parseDouble(orderInfo.get("current_rate").toString());
						
						//按汇率计算人民币价钱
						Double sale_price2 = Double.parseDouble(info.get("sale_price").toString()) * rate;
						info.put("sale_price2", sale_price2);
						//计算利润
						Double profit = Double.parseDouble(info.get("sale_price").toString()) - Double.parseDouble(info.get("in_price").toString());
						BigDecimal b = new BigDecimal(profit);  
						profit = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
						info.put("profit", profit * rate);
						
						//计算折扣 
						Double salePrice = Double.parseDouble(info.get("sale_price").toString());
						Double price = Double.parseDouble(info.get("price").toString());
						Double inPrice = Double.parseDouble(info.get("in_price").toString());
						
						BigDecimal sale_price_discount = new BigDecimal((salePrice / price)*100);  
//						info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
						info.put("sale_price_discount",sale_price_discount.intValue() +" %");
						
						BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
						info.put("supply_price_discount", supply_price_discount.intValue()+" %");
						
						//如果一条订单下面有多个子订单  判断子订单状态是否一致 不一致则修改父订单状态为Multiple
						if(orderLineList != null && orderLineList.size() > 0){
							for(Map<String, Object> orderMap : orderLineList){
								if(!orderMap.get("status").toString().equals(info.get("status").toString())){
									orderInfo.put("status", "Multiple");
								}
							}
							
						}
						
						
						//添加商品对应的属性
						if(productPropertyResult.size() > 0 ){
							if(productPropertyResult.get(info.get("product_id").toString()) != null){
								info.put("brandID", productPropertyResult.get(info.get("product_id").toString()).get("BrandID"));
								info.put("colorCode", productPropertyResult.get(info.get("product_id").toString()).get("ColorCode"));
							}
						}
							
						orderLineList.add(info);
						

					}
				}
				orderInfo.put("total_qty", amount);
				orderInfo.put("orderLineList", orderLineList);
			}
		}
		return orderLineResult;
	}
	
	
	
	@RequestMapping("/updateOrderStatus")
	@ResponseBody
	public Map<String, Object> updateOrderStatus(@RequestBody Map<String, Object> map){
		logger.info("updateOrderStatus param:"+new Gson().toJson(map));
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", StatusType.FAILURE);
		
		//参数不能为空
		if(map == null || map.size() == 0){
			resultMap.put("info", "Parameter cannot be null");
			return resultMap;
		}
		
		if(map.get("logisProductId") == null || StringUtils.isBlank(map.get("logisProductId").toString())){
			resultMap.put("info", "logisProductId cannot be null");
			return resultMap;
		}
		
		if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
			resultMap.put("info", "status cannot be null");
			return resultMap;
		}
		
		
		Long logisProductId =Long.parseLong(map.get("logisProductId").toString());
		int status =Integer.parseInt(map.get("status").toString());
		
		//调用修改订单状态
		resultMap = logisticsProductService.updateOrderLogisticsStatusById(logisProductId, status);
		
		return resultMap;
	}
	
	
}
