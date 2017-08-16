package com.intramirror.web.controller.order;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.MessageFormat;
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
import com.google.gson.JsonParser;
import com.intramirror.common.Helper;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.common.OrderStatusType;
import com.intramirror.order.api.model.Container;
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.api.service.ISubShipmentService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.user.api.model.User;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.VendorService;
import com.intramirror.web.common.BarcodeUtil;
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
	private VendorService vendorService;
	
	@Autowired
	private OrderRefund orderRefund;
	
	@Autowired
	private ISkuStoreService skuStoreService;
	
	@Autowired
	private ILogisticsProductService iLogisticsProductService;
	
	@Autowired
	private IShipmentService iShipmentService;
	
	@Autowired
	private IContainerService containerService;
	
	@Autowired
	private ISubShipmentService subShipmentService;
	
	
	
	/**
	 * 获取订单列表
	 * @param status 
	 * @param httpRequest
	 * @return
	 */
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
		
		String sortByName = null;
		if(map.get("sortByName") != null){
			sortByName = map.get("sortByName").toString();
		}
		
		
		Long vendorId = vendor.getVendorId();
		//根据订单状态查询订单
		List<Map<String,Object>> orderList = orderService.getOrderListByStatus(Integer.parseInt(map.get("status").toString()),vendorId,sortByName);
		
		if(orderList != null && orderList.size() > 0 ){
			
//			/**------------------------------------优化----------------------------------------*/
//			//遍历获取所有商品ID
//			String productIds = "";
//			for(Map<String, Object> info : orderList){
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
				info.put("sale_price_discount",(100 -sale_price_discount.intValue()) +" %");
				
				BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
				info.put("supply_price_discount", (100 - supply_price_discount.intValue())+" %");
				
				
//				//添加商品对应的属性
//				if(productPropertyResult.size() > 0 ){
//					if(productPropertyResult.get(info.get("product_id").toString()) != null){
//						info.put("brandID", productPropertyResult.get(info.get("product_id").toString()).get("BrandID"));
//						info.put("colorCode", productPropertyResult.get(info.get("product_id").toString()).get("ColorCode"));
//					}
//				}
				
			}
			
		}
		
		result.successStatus();
		result.setData(orderList);
		return result;
	}
    
    
    
    /**
     * 获取订单详情
     * @param status  orderNumber 
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage getOrderDetail(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("status") == null || StringUtils.isBlank(map.get("status").toString())|| map.get("orderNumber") == null){
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
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("status", Integer.parseInt(map.get("status").toString()));
		conditionMap.put("vendorId", vendorId);
		conditionMap.put("orderNumber", map.get("orderNumber").toString());
		
		//根据订单orderLineNumber 查询订单详情
		Map<String,Object> orderInfo = orderService.getOrderInfoByCondition(conditionMap);
		
		if(orderInfo != null ){
			//汇率
			Double rate =  Double.parseDouble(orderInfo.get("current_rate").toString());
			
			//按汇率计算人民币价钱
			Double sale_price2 = Double.parseDouble(orderInfo.get("sale_price").toString()) * rate;
			orderInfo.put("sale_price2", sale_price2);
			//计算利润
			Double profit = Double.parseDouble(orderInfo.get("sale_price").toString()) - Double.parseDouble(orderInfo.get("in_price").toString());
			BigDecimal b = new BigDecimal(profit);  
			profit = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			orderInfo.put("profit", profit * rate);
			
			//计算折扣 
			Double salePrice = Double.parseDouble(orderInfo.get("sale_price").toString());
			Double price = Double.parseDouble(orderInfo.get("price").toString());
			Double inPrice = Double.parseDouble(orderInfo.get("in_price").toString());
			
			BigDecimal sale_price_discount = new BigDecimal((salePrice / price)*100);  
//				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
			orderInfo.put("sale_price_discount",(100 - sale_price_discount.intValue()) +" %");
			
			BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
			orderInfo.put("supply_price_discount", (100 -supply_price_discount.intValue())+" %");
			
//			//根据ID列表获取商品属性
//			List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(orderInfo.get("product_id").toString());
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
//			
//			
//
//			//汇率
//			Double rate =  Double.parseDouble(orderInfo.get("current_rate").toString());
//			
//			//按汇率计算人民币价钱
//			Double sale_price2 = Double.parseDouble(orderInfo.get("sale_price").toString()) * rate;
//			orderInfo.put("sale_price2", sale_price2);
//			//计算利润
//			Double profit = Double.parseDouble(orderInfo.get("sale_price").toString()) - Double.parseDouble(orderInfo.get("in_price").toString());
//			BigDecimal b = new BigDecimal(profit);  
//			profit = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
//			orderInfo.put("profit", profit * rate);
//			
//			//计算折扣 
//			Double salePrice = Double.parseDouble(orderInfo.get("sale_price").toString());
//			Double price = Double.parseDouble(orderInfo.get("price").toString());
//			Double inPrice = Double.parseDouble(orderInfo.get("in_price").toString());
//			
//			BigDecimal sale_price_discount = new BigDecimal((salePrice / price)*100);  
////				info.put("sale_price_discount",sale_price_discount.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +" %");
//			orderInfo.put("sale_price_discount",(100 - sale_price_discount.intValue()) +" %");
//			
//			BigDecimal supply_price_discount = new BigDecimal((inPrice*(1+0.22)/price)*100);
//			orderInfo.put("supply_price_discount", (100 -supply_price_discount.intValue())+" %");
//			
//			
//			//添加商品对应的属性
//			if(productPropertyResult.size() > 0 ){
//				if(productPropertyResult.get(orderInfo.get("product_id").toString()) != null){
//					orderInfo.put("brandID", productPropertyResult.get(orderInfo.get("product_id").toString()).get("BrandID"));
//					orderInfo.put("colorCode", productPropertyResult.get(orderInfo.get("product_id").toString()).get("ColorCode"));
//				}
//			}
			
		}
		
		


		String orderNumberUrl = "Barcode-"+orderInfo.get("order_line_num").toString()+".png";
		orderNumberUrl = BarcodeUtil.generateFile(orderInfo.get("order_line_num").toString(), orderNumberUrl,false);
		orderInfo.put("orderNumberUrl", orderNumberUrl);
		
		//如果包含#号  则不生成条形码
		String skuBarcodeUrl = "Barcode-"+orderInfo.get("sku_code").toString()+".png";
		if(!skuBarcodeUrl.contains("#")){
			skuBarcodeUrl = BarcodeUtil.generateFile(orderInfo.get("sku_code").toString(), skuBarcodeUrl,true);
			orderInfo.put("skuBarcodeUrl", skuBarcodeUrl);
		}else{
			orderInfo.put("skuBarcodeUrl", null);
		}

		
		result.successStatus();
		result.setData(orderInfo);
		return result;
	}
	
	
	/**
	 * 状态机借口
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/updateOrderStatus", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage updateOrderStatus(@RequestBody Map<String, Object> map){
		logger.info("updateOrderStatus param:{}",new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", StatusType.FAILURE);
		try {
			//参数不能为空
			if(map == null || map.size() == 0){
				message.successStatus().putMsg("info", "Parameter cannot be null");
				return message;
			}
			
			if(map.get("logisProductId") == null || StringUtils.isBlank(map.get("logisProductId").toString())){
				message.successStatus().putMsg("info", "logisProductId cannot be null");
				return message;
			}
			
			if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
				message.successStatus().putMsg("info", "status cannot be null");
				return message;
			}
			
			
			Long logisProductId =Long.parseLong(map.get("logisProductId").toString());
			int status =Integer.parseInt(map.get("status").toString());
			
			//调用修改订单状态
			resultMap = logisticsProductService.updateOrderLogisticsStatusById(logisProductId, status);
			if (StatusType.SUCCESS == Integer.parseInt(resultMap.get("status").toString())){
				message.successStatus().putMsg("Info", "SUCCESS").setData(resultMap);
				return message;
			}
			message.successStatus().putMsg("Info", ""+StatusType.FAILURE).setData(resultMap);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("errorMsg : " +e.getMessage());
			message.errorStatus().putMsg("errorMsg", e.getMessage());
		}
		
		return message;
	}
	
	/**
	 * 根据不同订单状态统计数量
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/getOrderCount", method=RequestMethod.GET)
	@ResponseBody
	public ResultMessage getOrderCount(HttpServletRequest httpRequest){
		ResultMessage resultMessage = ResultMessage.getInstance();
		Map<String, Object> map;
        try {
        	User user = this.getUser(httpRequest);
    		if(user == null){
    			resultMessage.setMsg("Please log in again");
    			return resultMessage;
    		}
    		
    		Vendor vendor= null;
    		try {
    			vendor= vendorService.getVendorByUserId(user.getUserId());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		if(vendor == null){
    			resultMessage.setMsg("Please log in again");
    			return resultMessage;
    		}
    		Long vendorId = vendor.getVendorId();
    		int [] item = {OrderStatusType.PENDING,OrderStatusType.COMFIRMED};
    		Map<String, Object> resultMap = new HashMap<>();
    		for (int i = 0; i < item.length; i++) {
    			map = new HashMap<>();
    			map.put("status",item[i]);
    			map.put("vendorId", vendorId);
    			int result = orderService.getOrderByIsvalidCount(map);
    			if (OrderStatusType.PENDING == item[i])
    				resultMap.put("comfirmed", result);
    			if (OrderStatusType.COMFIRMED == item[i])
    				resultMap.put("pack", result);
			}
    		
    		//readytoship数量
    		map = new HashMap<>();
    		map.put("vendorId", vendorId);
    		int result = containerService.getContainerCount(map);
    		resultMap.put("readyToship", result);
    		
    		//shippedCount
    		int shippedCount = orderService.getShippedCount(resultMap);
    		resultMap.put("shipped", shippedCount);
    		
    		resultMessage.successStatus().putMsg("info","SUCCESS").setData(resultMap);
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
	@RequestMapping( value ="/orderRefund", method=RequestMethod.POST)
	@ResponseBody
	public ResultMessage orderRefund(@RequestBody Map<String, Object> map, HttpServletRequest request){
		//打印退款参数
		logger.info("info parameter" + new Gson().toJson(map));
		ResultMessage message = ResultMessage.getInstance();		
		User user = this.getUser(request);
		if (null == user){
			message.successStatus().putMsg("status", ""+StatusType.SESSION_USER_NULL);
			message.successStatus().putMsg("errorMsg", "session失效,请重新登陆!!!");
			return message;
		}
		//入参处理
		Map<String, String> params;
		try {
			params = getParamsFromRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			message.errorStatus().putMsg("errorMsg", e.getMessage());
			message.errorStatus().putMsg("status", ""+StatusType.STRING_CONVERT_UNSUPPORTED_ENCODING_EXCEPTION);
            return message;
		}
        //判断status
		int checkResult = checkParams(params);
        if (StatusType.SUCCESS != checkResult){
        	message.errorStatus().putMsg("errorMsg", "参数校验失败！！！");
        	message.errorStatus().putMsg("status", checkResult+"");
        	return message;
        }
        //获取订单状态
  		Integer orderStatus = Integer.valueOf(params.get("status"));
  		//该接口只针对退款，如果不是走状态机接口
  		if (-4 != orderStatus){
  			message.errorStatus().putMsg("errorMsg", "status not in -4");
  			message.errorStatus().putMsg("status", checkResult+"");
  			return message;
  		}
  		//单个或多个logistics_product_id编辑改变状态
  		JsonArray logisticsProductArray = null;
  		//传过来order_num和vendor_id进行编辑改变状态
  		JsonArray orderVendorArray = null;
  		// 响应状态(默认为失败)
        int status = StatusType.FAILURE;
        try {
        	//退款处理
	  		if (null != params.get("logistics_product_list") || StringUtils.isNoneBlank(params.get("logistics_product_list").toString())){
	  			logisticsProductArray = new JsonParser().parse(params.get("logistics_product_list").toString()).getAsJsonArray();
	  			status = orderRefund.updateStausByJson(logisticsProductArray, null, orderStatus);
	  			
	  		}
	  		if (null != params.get("order_vendor_list") || StringUtils.isNoneBlank(params.get("order_vendor_list").toString())){
	  			orderVendorArray = new JsonParser().parse(params.get("order_vendor_list").toString()).getAsJsonArray();
	  			status = orderRefund.updateStausByJson(null, orderVendorArray, orderStatus);
	  		}
	  		status = StatusType.SUCCESS;
	  		message.successStatus().putMsg("status", ""+status).setData(status);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error"+ e.getMessage());
			message.errorStatus().putMsg("errorMsg", e.getMessage());
		}
		return message;
	}
	
	
	
	/***
	 * 获取箱子里面的订单信息
	 * @param status containerId
	 * @param httpRequest
	 * @return
	 */
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
			

//			if(packList != null && packList.size() > 0){
//				//遍历获取所有商品ID
//				String productIds = "";
//				for(Map<String, Object> info : packList){
//					productIds +=info.get("product_id").toString()+",";
//				}
//				
//				if(StringUtils.isNoneBlank(productIds)){
//					productIds = productIds.substring(0,productIds.length() -1);
//				}
//				
//				//根据ID列表获取商品属性
//				List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(productIds);
//				Map<String, Map<String, String>> productPropertyResult= new HashMap<String, Map<String, String>>();
//				
//				for(Map<String, Object> productProperty : productPropertyList){
//					
//					//如果存在
//					if(productPropertyResult.containsKey(productProperty.get("product_id").toString())){
//						Map<String, String> info = productPropertyResult.get(productProperty.get("product_id").toString());
//					    info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
//					}else{
//						Map<String, String> info = new HashMap<String, String>();
//						info.put(productProperty.get("key_name").toString(), productProperty.get("value").toString());
//						productPropertyResult.put(productProperty.get("product_id").toString(), info);
//					}
//					
//				}
//			}


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
	 * 装箱验证
	 * @param map
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value="/packingCheckOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage packingCheckOrder(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		Map<String,Object> infoMap = new HashMap<String, Object>();
		//0 校验   2返回shipment列表  3箱子为空时  4不为空时  
		infoMap.put("statusType", StatusType.ORDER_CHECK_ORDER);
		infoMap.put("code", StatusType.ORDER_ERROR_CODE);
		
		
		if(checkParamsBypackingCheckOrder(map)){
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
		
        String orderLineNum = map.get("orderLineNum").toString();
        Map<String,Object> currentOrder = null;
        
        
		
		Long vendorId = vendor.getVendorId();
		//根据订单状态查询订单
		List<Map<String,Object>> orderList = orderService.getOrderListByStatus(Integer.parseInt(map.get("status").toString()),vendorId,null);
		if(orderList ==null || orderList.size() == 0){
			result.setMsg("The current order list is empty");
			return result;
		}
		
		//校验order_line_num  是否在CONFIRMED中存在
		for(Map<String,Object> info :orderList){
			if(orderLineNum.equals(info.get("order_line_num").toString())){
				currentOrder = info;
				result.successStatus();
				break;
			}
		}
		if(currentOrder == null){
			result.setMsg("Order does not exist");
			return result;
		}
		
		
		
		try{
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("container_id", Long.parseLong(map.get("containerId").toString()));
			conditionMap.put("status", OrderStatusType.READYTOSHIP);
			currentOrder.put("containerId", Long.parseLong(map.get("containerId").toString())); 
			
			//检查判断该箱子是否存在订单
			List<LogisticsProduct> list = iLogisticsProductService.selectByCondition(conditionMap);
			String shipment_id = map.get("shipment_id").toString();
			
			//获取箱子信息
			Map<String, Object> selectContainer = new HashMap<String, Object>(); 
			selectContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
			Container container =  containerService.selectContainerById(selectContainer);
			
			//如果为空箱子，并且已经选择过shipMent 则直接关联，并加入箱子
			if(StringUtils.isNoneBlank(shipment_id) && (list == null || list.size() == 0)){
				logger.info("已经选择过shipMent 直接关联，并加入箱子 start");
				infoMap.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
				
				//判断箱子的geography 跟订单的大区是否一致 
				if(container != null && !container.getShipToGeography().equals(currentOrder.get("geography_name").toString())){
					result.setMsg("The delivery area is inconsistent");
					return result;
				}
				
				//箱子关联Shipment
				Map<String, Object> updateContainer = new HashMap<String, Object>(); 
				updateContainer.put("shipment_id",shipment_id);
				updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
				logger.info(MessageFormat.format("packOrder updateContainerByCondition shipment_id:{0},container_id:{1}",shipment_id,Long.parseLong(map.get("containerId").toString())));
				int row = containerService.updateContainerByCondition(updateContainer);
				
				//关联成功，则往箱子里存入订单
				if(row > 0 ){
					
					Map<String, Object> shipMentMap = new HashMap<String, Object>();
					//根据订单大区选择的Shipment   所以只需要用订单的大区即可(只有箱子为空时)
					shipMentMap.put("ship_to_geography", currentOrder.get("geography_name").toString());
					shipMentMap.put("shipment_id", Long.parseLong(shipment_id));
//					//获取当前ShipMent 第一段的物流类型(不需要  空箱子不比较shipmentType 直接放入)
					
					//订单加入箱子
					logger.info("start updateLogisticsProduct");
					result =  updateLogisticsProduct(currentOrder,shipMentMap,false);

					
				}else{
					result.setMsg("The modification failed. Check that the parameters are correct ");
				}
				logger.info("已经选择过shipMent 直接关联，并加入箱子 end");
				result.setInfoMap(infoMap);
				return result;
			}
			
			
			
			//如果是新箱子，则需要关联Shipment,如果存在符合条件的Shipment有多个则返回列表供选择,如果只有一个则默认存入，没有则需要新建Shipment
			if(list == null || list.size() == 0){
				logger.info("箱子内订单列表为空    ");
				//判断箱子的geography 跟订单的大区是否一致 
				if(container != null && !container.getShipToGeography().equals(currentOrder.get("geography_name").toString())){
					result.setMsg("The delivery area is inconsistent");
					return result;
				}
				

				Map<String, Object> selectShipmentParam = new HashMap<String, Object>();
				selectShipmentParam.put("shipToGeography", currentOrder.get("geography_name").toString());
				
				//shipment 状态
				selectShipmentParam.put("status", ContainerType.OPEN);
				selectShipmentParam.put("vendorId", vendorId);
				
				//查询shipment
				List<Map<String, Object>> shipmentMapList = iShipmentService.getShipmentsByVendor(selectShipmentParam);
				
				//如果为空  新建Shipment
				if(shipmentMapList == null || shipmentMapList.size() == 0  ){
					logger.info("shipmentMapList is null  start  ");
					Map<String, Object> saveShipmentParam = new HashMap<String, Object>();
					saveShipmentParam.put("orderNumber", orderLineNum);
					saveShipmentParam.put("shipment_id", 0);

					//接口需要返回shipmentId
					Map<String, Object> orderResult = orderService.getShipmentDetails(saveShipmentParam);
					orderResult.put("shipmentId", 0);
					String shipmentId = iShipmentService.saveShipmentByOrderId(orderResult);
					if (shipmentId != null && StringUtils.isNotBlank(shipmentId)){
						
						infoMap.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
						
						//箱子关联Shipment
						Map<String, Object> updateContainer = new HashMap<String, Object>(); 
						updateContainer.put("shipment_id", Long.parseLong(shipmentId));
						updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
						logger.info(MessageFormat.format("packOrder updateContainerByCondition shipment_id:{0},container_id:{1}",shipmentId,Long.parseLong(map.get("containerId").toString())));
						int updateContainerRow = containerService.updateContainerByCondition(updateContainer);
						
						//关联成功，则往箱子里存入订单
						if(updateContainerRow > 0 ){
							
							Map<String, Object> shipMentMap = new HashMap<String, Object>();
							//根据订单大区创建的Shipment   所以只需要用订单的大区即可(只有箱子为空时)
							shipMentMap.put("ship_to_geography", currentOrder.get("geography_name").toString());
							shipMentMap.put("shipment_id", shipmentId);
//								//获取当前ShipMent 第一段的物流类型(不需要  空箱子不比较shipmentType 直接放入)
							
							//订单加入箱子
							result =  updateLogisticsProduct(currentOrder,shipMentMap,false);
						}
						result.setInfoMap(infoMap);
					}
						
					//如果匹配的Shipment 只存在一个,就直接关联箱子   并把订单存入箱子
					}else if(shipmentMapList.size() == 1){
						logger.info("shipmentMapList size 1  start  ");
						infoMap.put("statusType", StatusType.ORDER_CONTAINER_EMPTY);
						
						//箱子关联Shipment
						Map<String, Object> updateContainer = new HashMap<String, Object>(); 
						updateContainer.put("shipment_id", Long.parseLong(shipmentMapList.get(0).get("shipment_id").toString()));
						updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
						logger.info(MessageFormat.format("packOrder updateContainerByCondition shipment_id:{0},container_id:{1}",shipmentMapList.get(0).get("shipment_id").toString(),Long.parseLong(map.get("containerId").toString())));
						int row = containerService.updateContainerByCondition(updateContainer);
						
						//关联成功，则往箱子里存入订单
						if(row > 0 ){
							
							Map<String, Object> shipMentMap = shipmentMapList.get(0);
							//获取当前ShipMent 第一段的物流类型(不需要  空箱子不比较shipmentType 直接放入)
							
							//订单加入箱子
							result =  updateLogisticsProduct(currentOrder,shipMentMap,false);
						}
						result.setInfoMap(infoMap);
					
					//如果匹配的Shipment 存在多个，则返回列表供选择
					}else if(shipmentMapList.size() > 1){
						logger.info("shipmentMapList size >1  start  ");
						result.setData(shipmentMapList);
						infoMap.put("statusType", StatusType.ORDER_QUERY_LIST);
						result.setInfoMap(infoMap);
					}
				
			
			//如果箱子中存在订单，则直接加入箱子
			}else{
				logger.info("箱子内已经存在订单   start  ");
				infoMap.put("statusType", StatusType.ORDER_CONTAINER_NOT_EMPTY);
				
				Map<String, Object> getShipment = new HashMap<String, Object>();
				getShipment.put("shipmentId", Long.parseLong(shipment_id));
				
				//根据shipmentId 获取shipment 相关信息及物流第一段类型
				Map<String, Object> shipmentMap = iShipmentService.getShipmentTypeById(getShipment);
				
				if(shipmentMap != null ){
					//订单加入箱子
					result =  updateLogisticsProduct(currentOrder,shipmentMap,true);
				}else{
					result.setMsg("shipment Query is empty ");
				}
				
				result.setInfoMap(infoMap);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		
		return result;
	}
	
	
	
	
	/**
	 * 删除箱子中的订单
	 * @param map
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value="/deletePackingCheckOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResultMessage deletePackingCheckOrder(@RequestBody Map<String,Object> map,HttpServletRequest httpRequest){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		if(map == null || map.size() == 0 || map.get("logistics_product_id") == null || map.get("container_id") == null){
			result.setMsg("Parameter cannot be empty");
			return result;
		}
		
		try{
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("logistics_product_id", Long.parseLong(map.get("logistics_product_id").toString()));
			conditionMap.put("container_id", Long.parseLong(map.get("container_id").toString()));
			conditionMap.put("status", OrderStatusType.READYTOSHIP);
			
			//检查判断该箱子是否存在订单
			List<LogisticsProduct> list = iLogisticsProductService.selectByCondition(conditionMap);
			if(list == null || list.size() == 0){
				result.setMsg("Order does not exist ");
				return result;
			}
			
			
			//取消订单跟箱子的关联,并修改状态为CONFIRMED
			LogisticsProduct logisticsProduct = new LogisticsProduct();
			logisticsProduct.setLogistics_product_id(Long.parseLong(map.get("logistics_product_id").toString()));
			logisticsProduct.setContainer_id(0l);
			logisticsProduct.setStatus(OrderStatusType.COMFIRMED);
			int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
			
			if(row > 0){
				//查询相关的logisProShipmentInfo 表获取sub_shipment_id
				Map<String,Object> logisProShipmentInfo = iLogisticsProductService.selectLogisProShipmentById(Long.parseLong(map.get("logistics_product_id").toString()));
				
				//根据sub_shipment_id 删除sub_shipment
				if(logisProShipmentInfo != null && logisProShipmentInfo.get("sub_shipment_id") != null ){
					subShipmentService.deleteByPrimaryKey(Long.parseLong(logisProShipmentInfo.get("sub_shipment_id").toString()));
//					iLogisticsProductService
				}
				result.successStatus();
			}else{
				result.setMsg("Order does not exist ");
			}
			
		}catch(Exception e){
			e.printStackTrace();
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
    
    
    
    /**
     * packingCheckOrder 接口的参数校验
     * @return
     */
    public boolean checkParamsBypackingCheckOrder(Map<String,Object> map){
    	if(map == null || map.size() == 0){
    		return true;
    	}
    	
    	if(map.get("containerId") == null || StringUtils.isBlank(map.get("containerId").toString())){
    		return true;
    	}
    	
    	if(map.get("status") == null || StringUtils.isBlank(map.get("status").toString())){
    		return true;
    	}
    	
    	if(map.get("orderLineNum") == null || StringUtils.isBlank(map.get("orderLineNum").toString())){
    		return true;
    	}
    	
    	return false;
    }
    
    
    
    /**
     * 回调接口
     * @return
     */
    @RequestMapping(value="/orderRefundCallback")
	@ResponseBody
    public ResultMessage orderRefundCallback(@RequestBody Map<String,Object> map){
		logger.info("info parameter :"+ new Gson().toJson(map));
		ResultMessage message= ResultMessage.getInstance();
		try {
			//校验入参
	    	if (null == map || 0 == map.size()){
	    		message.successStatus().putMsg("info", "Parameter cannot be null");
	    		logger.info("info parameter cannot be null");
	    		return message;
	    	}
	    	if (null == map.get("logisProductId") || StringUtils.isBlank(map.get("logisProductId").toString())){
	    		message.successStatus().putMsg("info", "logisProductId cannot be null");
	    		logger.info("info logisProductId  cannot be null");
	    		return message;
	    	}
	    	if (null == map.get("status") || StringUtils.isBlank(map.get("status").toString())){
	    		message.successStatus().putMsg("info", "status cannot be null");
	    		logger.info("info status  cannot be null");
	    		return message;
	    	}
	    	Long logisProductId =Long.parseLong(map.get("logisProductId").toString());
			int status =Integer.parseInt(map.get("status").toString());
			
			//回调接口只支持取消
			if (OrderStatusType.CANCELED != status){
				message.successStatus().putMsg("info", "status not is 6");
	    		logger.info("info status not is 6");
	    		return message;
			}
	    	logger.info(logisProductId +"： 进入退款回调流程!!");
	    	Map<String, Object> resultMap = logisticsProductService.updateOrderLogisticsStatusById(logisProductId, status);
	    	//获取SKU
	    	LogisticsProduct oldLogisticsProduct = iLogisticsProductService.selectById(logisProductId);
	    	//开始修改库存
			if (StatusType.SUCCESS == Integer.parseInt(resultMap.get("status").toString())){
				//如果成功，释放库存
				Long skuId = skuStoreService.selectSkuIdByShopProductSkuId(oldLogisticsProduct.getShop_product_sku_id());
				int result = skuStoreService.updateBySkuId(OrderStatusType.REFUND, skuId);
				message.successStatus().putMsg("Info", "SUCCESS").setData(result);
				logger.info(logisProductId + "===========>>>>>释放库存");
				return message;
			}
			message.successStatus().putMsg("Info", "当前状态不符合状态机扭转请检查");
			logger.info("退款回调结束!!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("errorMsg : "+e.getMessage());
			message.errorStatus().putMsg("errorMsg", e.getMessage());
		}
    	return message;
    }
    
    
	/**
	 * 订单装箱
	 * @param map
	 * @return
	 */
	public ResultMessage updateLogisticsProduct(Map<String,Object> orderMap,Map<String,Object> shipMentMap,boolean ischeck){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		//校验该订单跟箱子所属的Shipment的目的地是否一致,一致则加入,是否分段运输，发货员自行判断
		if(!orderMap.get("geography_name").toString().equals(shipMentMap.get("ship_to_geography").toString())){
			
			//空箱子不需要判断,直接存入   shipment_type 用于判断该箱子是否能存放多个，状态为1 只能存放一个  所以不能在存入
			if(ischeck && shipMentMap.get("shipment_type_id").toString().equals("1")){
				result.setMsg("Only one can be deposited ");
				return result;
			}
				
//			//比较具体地址 省市区
//			}else if(shipMentMap.get("shipment_type_id").toString().equals("2")){
//				
//
//			}else if(shipMentMap.get("shipment_type_id").toString().equals("3")){
//				
//			}
			

		}
		
		
		//添加订单跟箱子的关联,并修改状态为READYTOSHIP
		LogisticsProduct logisticsProduct = new LogisticsProduct();
		logisticsProduct.setLogistics_product_id(Long.parseLong(orderMap.get("logistics_product_id").toString()));
		logisticsProduct.setContainer_id(Long.parseLong(orderMap.get("containerId").toString()));
		logisticsProduct.setStatus(OrderStatusType.READYTOSHIP);
		int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
		
		if(row > 0){
			result.successStatus();
			
			Map<String, Object> saveShipmentParam = new HashMap<String, Object>();
			saveShipmentParam.put("orderNumber", orderMap.get("order_line_num").toString());
			saveShipmentParam.put("shipmentId", Long.parseLong(shipMentMap.get("shipment_id").toString()));
			Map<String, Object> orderResult = orderService.getShipmentDetails(saveShipmentParam);
			orderResult.put("shipmentId", Long.parseLong(shipMentMap.get("shipment_id").toString()));
			
			//添加第三段物流
			iShipmentService.saveShipmentByOrderId(orderResult);
			
		}else{
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("code", StatusType.ORDER_ERROR_CODE);
			result.setInfoMap(info);
			result.setMsg("Package failure");
		}
		return result;
	}
}
