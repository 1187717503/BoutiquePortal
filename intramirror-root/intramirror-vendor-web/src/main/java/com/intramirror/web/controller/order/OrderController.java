package com.intramirror.web.controller.order;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.Contants;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.payment.api.model.ResultMsg;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.LogisticsProductService;
import com.intramirror.web.service.OrderRefund;





@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	 
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private LogisticsProductService logisticsProductService;
	
	@Autowired
	private ProductPropertyService productPropertyService;
	
	@Autowired
	private ILogisticsProductService iLogisticsProductService;
	
	@Autowired
	private VendorService vendorService;
	
	
	
	
    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage getOrderList(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
			result.setMsg("Parameter cannot be empty");
			return result;
		}
		
		User user = this.getUser(httpRequest);
		if(user == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		Vendor vendor= null;
		try {
			vendor= vendorService.getVendorByUserId(user.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(vendor == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		Long vendorId = vendor.getVendorId();
		//根据订单状态查询订单
		List<Map<String,Object>> orderList = orderService.getOrderListByStatus(Integer.parseInt(map.get("status").toString()),vendorId);
		
		if(orderList != null && orderList.size() > 0 ){
			
			/**------------------------------------优化----------------------------------------*/
			//遍历获取所有商品ID
			String productIds = "";
			for(Map<String, Object> info : orderList){
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
			
			
			for(Map<String, Object> info : orderList){

				//汇率
				Double rate =  Double.parseDouble(info.get("current_rate").toString());
				
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
//				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
				info.put("sale_price_discount",sale_price_discount.intValue() +" %");
				
				BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
				info.put("supply_price_discount", supply_price_discount.intValue()+" %");
				
				
				//添加商品对应的属性
				if(productPropertyResult.size() > 0 ){
					if(productPropertyResult.get(info.get("product_id").toString()) != null){
						info.put("brandID", productPropertyResult.get(info.get("product_id").toString()).get("BrandID"));
						info.put("colorCode", productPropertyResult.get(info.get("product_id").toString()).get("ColorCode"));
					}
				}
				
			}
			
		}
		
		
//		//获取order列表
//		List<Map<String,Object>> orderList = orderService.getOrderList(status);
//		
//		String orderNumbers = "";
//		
//		//遍历获取所有的orderNumber
//		if(orderList != null && orderList.size() > 0){
//			for(Map<String, Object> orderInfo : orderList){
//				orderNumbers += orderInfo.get("order_num").toString()+",";
//			}
//		}else{
//			result.setData(orderList);
//			return result;
//		}
//		
//		if(StringUtils.isNoneBlank(orderNumbers)){
//			orderNumbers = orderNumbers.substring(0,orderNumbers.length() -1);
//		}
//		
//		
//		//根据orderNumber 获取orderLine信息
//		List<Map<String,Object>> orderLineResult = orderService.getOrderListByOrderNumber(orderNumbers, status);
//		
//		if(orderLineResult != null && orderLineResult.size() > 0){
//			
//			/**------------------------------------优化----------------------------------------*/
//			//遍历获取所有商品ID
//			String productIds = "";
//			for(Map<String, Object> info : orderLineResult){
//				productIds +=info.get("product_id").toString()+",";
//			}
//			
//			if(StringUtils.isNoneBlank(productIds)){
//				productIds = productIds.substring(0,productIds.length() -1);
//			}
//			
//			//根据ID列表获取商品属性
//			List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(productIds);
//			Map<String, Map<String, String>> productPropertyResult= new HashMap<String, Map<String, String>>();
//			
//			for(Map<String, Object> productProperty : productPropertyList){
//				
//				//如果存在
//				if(productPropertyResult.containsKey(productProperty.get("product_id").toString())){
//					Map<String, String> info = productPropertyResult.get(productProperty.get("product_id").toString());
//				    info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
//				}else{
//					Map<String, String> info = new HashMap<String, String>();
//					info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
//					productPropertyResult.put(productProperty.get("product_id").toString(), info);
//				}
//				
//			}
//			/**------------------------------------优化end----------------------------------------*/
//			
//			
//			
//			for(Map<String, Object> orderInfo : orderList){
//				List<Map<String, Object>> orderLineList = new ArrayList<Map<String,Object>>();
//				int amount = 0;
//				for(Map<String, Object> info : orderLineResult){
//					if(orderInfo.get("order_num").toString().equals(info.get("order_num").toString())){
//						//累加数量
//						amount +=Integer.parseInt(info.get("amount").toString());
//						//汇率
//						Double rate =  Double.parseDouble(orderInfo.get("current_rate").toString());
//						
//						//按汇率计算人民币价钱
//						Double sale_price2 = Double.parseDouble(info.get("sale_price").toString()) * rate;
//						info.put("sale_price2", sale_price2);
//						//计算利润
//						Double profit = Double.parseDouble(info.get("sale_price").toString()) - Double.parseDouble(info.get("in_price").toString());
//						BigDecimal b = new BigDecimal(profit);  
//						profit = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
//						info.put("profit", profit * rate);
//						
//						//计算折扣 
//						Double salePrice = Double.parseDouble(info.get("sale_price").toString());
//						Double price = Double.parseDouble(info.get("price").toString());
//						Double inPrice = Double.parseDouble(info.get("in_price").toString());
//						
//						BigDecimal sale_price_discount = new BigDecimal((salePrice / price)*100);  
////						info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
//						info.put("sale_price_discount",sale_price_discount.intValue() +" %");
//						
//						BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
//						info.put("supply_price_discount", supply_price_discount.intValue()+" %");
//						
//						//如果一条订单下面有多个子订单  判断子订单状态是否一致 不一致则修改父订单状态为Multiple
//						if(orderLineList != null && orderLineList.size() > 0){
//							for(Map<String, Object> orderMap : orderLineList){
//								if(!orderMap.get("status").toString().equals(info.get("status").toString())){
//									orderInfo.put("status", "Multiple");
//								}
//							}
//							
//						}
//						
//						
//						//添加商品对应的属性
//						if(productPropertyResult.size() > 0 ){
//							if(productPropertyResult.get(info.get("product_id").toString()) != null){
//								info.put("brandID", productPropertyResult.get(info.get("product_id").toString()).get("BrandID"));
//								info.put("colorCode", productPropertyResult.get(info.get("product_id").toString()).get("ColorCode"));
//							}
//						}
//							
//						orderLineList.add(info);
//						
//
//					}
//				}
//				orderInfo.put("total_qty", amount);
//				orderInfo.put("orderLineList", orderLineList);
//			}
//		}
		result.successStatus();
		result.setData(orderList);
		return result;
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
	
	/**
	 * 根据不同订单状态统计数量
	 * @param map
	 * @return
	 */
	@RequestMapping("/getOrderCount")
	@ResponseBody
	public ResultMessage getOrderCount(@RequestBody Map<String, Object> map){
		logger.info("getOrderCount param " + new Gson().toJson(map));
		ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	if(map == null || map.size() == 0){
        		resultMessage.successStatus().putMsg("info", "Parameter cannot be null");
        		return resultMessage;
        	}
        	if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
        		resultMessage.successStatus().putMsg("info", "status cannot be null");
        		return resultMessage;
        	}
        	int status =Integer.parseInt(map.get("status").toString());
            int result = orderService.getOrderByIsvalidCount(status);
            resultMessage.successStatus().putMsg("info","SUCCESS").setData(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
		
	}
	
	/**
	 * 退款接口
	 * @param map
	 * @return
	 */
	@RequestMapping("/orderRefund")
	@ResponseBody
	public Map<String, Object> orderRefund(@RequestBody Map<String, Object> map, HttpServletRequest request){
		// 响应数据
    	Map<String, Object> returnMap = new HashMap<String, Object>();
    	
    	// 响应状态(默认为失败)
        int status = StatusType.FAILURE;
//        User user = (User) request.getSession().getAttribute("sessionUser");
//        if (user == null) {
//            returnMap.put("status", StatusType.SESSION_USER_NULL);
//            returnMap.put("errorMsg", "session失效,请重新登陆!!!");
//            return returnMap;
//        }

        // 处理入参
        Map<String, String> params;
        try {
            params = getParamsFromRequest(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            returnMap.put("status", StatusType.STRING_CONVERT_UNSUPPORTED_ENCODING_EXCEPTION);
            return returnMap;
        }
        
        //订单状态
        Integer orderStatus = Integer.valueOf(params.get("status"));
//        String trackingNum = params.get("tracking_num").toString();
//        String vatNum = params.get("vat_num").toString();
System.out.println("*******************************");
        int checkResult = checkParams(params);

        // check params
        if (checkResult != StatusType.SUCCESS) {
            returnMap.put("status", checkResult);
            returnMap.put("errorMsg", "参数校验失败!!!");
            return returnMap;
        }
        StringBuffer stringBuffer = new StringBuffer();
        String errorMsg = "";
		
//		Long logisProductId =Long.parseLong(map.get("logisProductId").toString());
		
		
		//单个或多个logistics_product_id编辑改变状态
        JsonArray logisticsProductArray = new JsonParser().parse(params.get("logistics_product_list").toString()).getAsJsonArray();
        try{
			if (logisticsProductArray.size() != 0) {
				 for (int i = 0; i < logisticsProductArray.size(); i++) {
					 boolean isUpdateflag = false;
					 JsonObject logisticsProductObj = logisticsProductArray.get(i).getAsJsonObject();
					 String logisticsid = logisticsProductObj.get("logistics_product_id").getAsString();
					 OrderRefund orderRefund = new OrderRefund();
					 	if(orderStatus.intValue() == -4) {
							 //根据商品ID查询订单
							List<Map<String, Object>> porudctList =  orderService.getOrderPaymentByLogisProductId(Long.valueOf(logisticsid));
								 if (null != porudctList){
									 Map<String, Object> refundMap = porudctList.get(0);
									 if (Contants.PAY_OFFLINE.equals(refundMap.get("pay_way").toString())){ // 线下支付
										 isUpdateflag = true;
					                 }else if(refundMap.get("serial_number") != null && StringUtils.isNotBlank(refundMap.get("serial_number").toString())){
						            		ResultMsg rsMsg = orderRefund.refund(refundMap);
						                    if (!rsMsg.getStatus()) {
						                       status = StatusType.FAILURE;
						                       logger.info(rsMsg.getMsg());
						                    } else {
						                   	 	isUpdateflag = true;
						                    }
					                        // 记录退款日志
					                        orderRefund.recordPaymentLog(refundMap);
					                 }else{
					                	 status = StatusType.FAILURE;
					                	 stringBuffer.append(","+logisticsid+" 子订单数据异常,找不到有效支付记录!!!");
					                	 logger.info(logisticsid+" 子订单数据异常,找不到有效支付记录!!!");
					                 }
									 
								 } else {
					                 status = StatusType.FAILURE;
					                 stringBuffer.append(","+logisticsid+" 子订单数据异常,找不到有效支付记录!!!");
					                 logger.info(logisticsid+" 子订单数据异常,找不到有效支付记录!!!");
								 }
								 
							 if (porudctList != null && ((orderStatus.intValue() == -4 && isUpdateflag) || orderStatus.intValue() != -4)) {
								 //调用修改订单状态
								 Map<String, Object> result = logisticsProductService.updateOrderLogisticsStatusById(Long.valueOf(logisticsid), orderStatus);
								 if(Integer.parseInt(result.get("status").toString()) == StatusType.FAILURE){
				                 	status = StatusType.FAILURE;
				                 	stringBuffer.append(","+result.get("info").toString());
				                 	logger.info(result.get("info").toString());
				                 }
				             }
							 
				         }
				 	}
			}
			
			
			//传过来order_num和vendor_id进行编辑改变状态
	        JsonArray orderVendorArray = new JsonParser().parse(params.get("order_vendor_list").toString()).getAsJsonArray();
			if (orderVendorArray.size() != 0) {
				 for (int i = 0; i < orderVendorArray.size(); i++) {
					 JsonObject orderVendorObj = orderVendorArray.get(i).getAsJsonObject();
	                 String orderId = orderVendorObj.get("order_num").getAsString();
	                 Map<String, Object> orderMap = orderService.getOrderPaymentInfoByOrderId(Integer.parseInt(orderId));
	                 String order_logistics_id = orderMap.get("order_logistics_id").toString();
	                 boolean isUpdateflag = false;
	                 OrderRefund orderRefund = new OrderRefund();
	                 // 执行退款操作
	                 if (orderStatus.intValue() == -4) {
	                	 Map<String, Object> resultMap = orderService.getPaymentInfoByOrderId(Integer.parseInt(orderId));
	                	 if (null != resultMap) {
	                		 if (Contants.PAY_OFFLINE.equals(resultMap.get("pay_way").toString())){ // 线下支付
	 	                     	isUpdateflag = true;
	 	                    }else {
	 	                     	ResultMsg rsMsg = orderRefund.refund(resultMap);
	 	                         if (!rsMsg.getStatus()) {
	 	                            status = StatusType.FAILURE;
	 	                            stringBuffer.append("," + rsMsg.getMsg());
	 	                         } else {
	 	                        	 isUpdateflag = true;
	 	                         }
	 	                         // 记录退款日志
	 	                         orderRefund.recordPaymentLog(resultMap);
	 	                     }
	                	 }else {
		                     status = StatusType.FAILURE;
		                     stringBuffer.append(","+orderId+" 订单数据异常,找不到有效支付记录!!!");
		                     logger.info(orderId+" 订单数据异常,找不到有效支付记录!!!");
	                	 }
	                 }
	                 
	                 if((orderStatus.intValue() == -4 && isUpdateflag) || orderStatus.intValue() != -4) {
	                 	// 修改状态
	                     Map<String, Object> logisticsProductConditionMap = new HashMap<String, Object>();
	                     logisticsProductConditionMap.put("order_logistics_id", Integer.parseInt(order_logistics_id));
	                     logisticsProductConditionMap.put("vendor_id", Integer.parseInt(orderVendorObj.get("vendor_id").getAsString()));
	                     logisticsProductConditionMap.put("enabled", EnabledType.USED);
	
	                     List<LogisticsProduct> logisticsProductList = iLogisticsProductService.
	                    		 getLogisticsProductListByCondition(logisticsProductConditionMap);
	                     if (logisticsProductList.size() != 0) {
	                         for (int k = 0; k < logisticsProductList.size(); k++) {
	                        	 logger.info(logisticsProductList.get(0).getTracking_num());
	                        	 LogisticsProduct logisticsProduct = logisticsProductList.get(k);
	                             logisticsProduct.setStatus(Integer.valueOf(params.get("status")));
	                             Map<String, Object> result = logisticsProductService.updateOrderLogisticsStatusById(logisticsProduct.getLogistics_product_id(), orderStatus.intValue());
								 if(Integer.parseInt(result.get("status").toString()) == StatusType.FAILURE){
				                 	status = StatusType.FAILURE;
				                 	logger.info(result.get("info").toString());
				                 }
	                         }
	                     }
	                 }
				}
			}
		
		} catch (Exception e) {
	        status = StatusType.DATABASE_ERROR;
	        e.printStackTrace();
	        logger.info("error" + e.getMessage());
	        if(StringUtils.isBlank(errorMsg)) {
	        	errorMsg = e.getMessage();
	        }
	    }
		returnMap.put("status", status);
        returnMap.put("errorMsg", errorMsg);
		
		return returnMap;
	}
	
	/**
	 * 订单打包装箱
	 * @param map
	 * @return
	 */
	@RequestMapping("/PackOrder")
	@ResponseBody
	public ResultMessage PackOrder(@RequestBody Map<String, Object> map){
		logger.info("PackOrder param " + new Gson().toJson(map));
		ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
		
	}
	
	
	
	@RequestMapping(value="/getPackOrderList", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage getPackOrderList(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("status") == null || map.get("containerId") == null){
			result.setMsg("Parameter cannot be empty");
			return result;
		}
		
		User user = this.getUser(httpRequest);
		if(user == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		Vendor vendor= null;
		try {
			vendor= vendorService.getVendorByUserId(user.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(vendor == null){
			result.setMsg("Please log in again");
			return result;
		}
		
		
		
		try{
			//获取PackOrderorder列表
			List<Map<String,Object>> packList = orderService.getOrderListByStatusAndContainerId(Integer.parseInt(map.get("containerId").toString()),Integer.parseInt(map.get("status").toString()),vendor.getVendorId());
			

			if(packList != null && packList.size() > 0){
				//遍历获取所有商品ID
				String productIds = "";
				for(Map<String, Object> info : packList){
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
			}


			result.successStatus();
			result.setData(packList);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("Query order list fail,Check parameters, please ");
			return result;
		}

		
		
		return result;
	}
	
	/**
     * 获取请求参数
     * @param req
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> getParamsFromRequest(HttpServletRequest req) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        
        // 获取status(需执行的操作)
        String status = req.getParameter("status");
        if (StringUtils.isNotBlank(status)) {
            status = URLDecoder.decode(status, "UTF-8");
            if (status.equalsIgnoreCase("CONFIRM")) {
                status = "2";
            } else if (status.equalsIgnoreCase("ORDERED")) {//ship
                status = "3";
            }  else if (status.equalsIgnoreCase("DELIVERED")) {//delivered
                status = "4";
            } else if (status.equalsIgnoreCase("CLOSE")) {
                status = "5";
            }else if (status.equalsIgnoreCase("CANCELED")) {
                status = "-4";
            }
            result.put("status", status);
        }
        
        String tracking_num = req.getParameter("tracking_num");
        if (StringUtils.isNotBlank(tracking_num)) {
            result.put("tracking_num", tracking_num);
        } else {
            result.put("tracking_num", "");
        }

        String vat_num = req.getParameter("vat_num");
        if (StringUtils.isNotBlank(vat_num)) {
            result.put("vat_num", vat_num);
        } else {
            result.put("vat_num", "");
        }

        String logistics_product_list = req.getParameter("logistics_product_list");
        if (logistics_product_list != null) {
            logistics_product_list = URLDecoder.decode(logistics_product_list, "UTF-8");
            result.put("logistics_product_list", logistics_product_list);
        }

        String order_vendor_list = req.getParameter("order_vendor_list");
        if (order_vendor_list != null) {
            order_vendor_list = URLDecoder.decode(order_vendor_list, "UTF-8");
            result.put("order_vendor_list", order_vendor_list);
        }

        return result;
    }
    
    /**
     * 判断是否是合法
     *
     * @param params
     * @return
     */
    public int checkParams(Map<String, String> params) {
    	System.out.println("==========================????????????????");
        // 判断status是否合法
        if (Helper.isNullOrEmpty(params.get("status").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }

        if (params.get("status").toString().length() > 256) {
            return StatusType.STRING_LENGTH_ERROR;
        }

        // 判断logistics_product_list是否为空
        if (Helper.isNullOrEmpty(params.get("logistics_product_list").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }
        // 判断logistics_product_list是否json
        if (!Helper.isGoodJson(params.get("logistics_product_list").toString())) {
            return StatusType.IS_NOT_GOOD_JSON;
        }

        // 判断order_vendor_list是否为空
        if (Helper.isNullOrEmpty(params.get("order_vendor_list").toString())) {
            return StatusType.PARAM_EMPTY_OR_NULL;
        }
        // 判断order_vendor_list是否json
        if (!Helper.isGoodJson(params.get("order_vendor_list").toString())) {
            return StatusType.IS_NOT_GOOD_JSON;
        }
        return StatusType.SUCCESS;
    }
}
