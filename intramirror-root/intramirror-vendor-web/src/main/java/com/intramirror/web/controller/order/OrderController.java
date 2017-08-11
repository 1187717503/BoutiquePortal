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
import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.service.IContainerService;
import com.intramirror.order.api.service.ILogisticsProductService;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.service.IShipmentService;
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
		
		//根据订单状态查询订单
		Map<String,Object> orderInfo = orderService.getOrderInfoByCondition(conditionMap);
		
		if(orderInfo != null ){
			
			
			//根据ID列表获取商品属性
			List<Map<String, Object>> productPropertyList = productPropertyService.getProductPropertyListByProductId(orderInfo.get("product_id").toString());
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
			
			
			//添加商品对应的属性
			if(productPropertyResult.size() > 0 ){
				if(productPropertyResult.get(orderInfo.get("product_id").toString()) != null){
					orderInfo.put("brandID", productPropertyResult.get(orderInfo.get("product_id").toString()).get("BrandID"));
					orderInfo.put("colorCode", productPropertyResult.get(orderInfo.get("product_id").toString()).get("ColorCode"));
				}
			}
			
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
		logger.info("updateOrderStatus param:"+new Gson().toJson(map));
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
		
		
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("container_id", Long.parseLong(map.get("containerId").toString()));
		conditionMap.put("status", OrderStatusType.READYTOSHIP);
		
		//检查判断该箱子是否存在订单
		List<LogisticsProduct> list = iLogisticsProductService.selectByCondition(conditionMap);
		
		//如果是新箱子，则需要关联Shipment,如果存在符合条件的Shipment有多个则返回列表供选择,如果只有一个则默认存入，没有则需要新建Shipment
		if(list == null || list.size() == 0){
			
			//
			if(StringUtils.isNotBlank(currentOrder.get("address_country_id").toString())){
				Map<String, Object> selectShipmentParam = new HashMap<String, Object>();
				selectShipmentParam.put("shipToCountry", currentOrder.get("address_country_id").toString());
				
				//shipment 状态
				selectShipmentParam.put("status", ContainerType.OPEN);
				//查询shipment(不需要根据物流类型？)
				List<Map<String, Object>> resultMap = iShipmentService.getShipmentByStatus(selectShipmentParam);
				
				//如果为空  新建Shipment
				if(resultMap == null || resultMap.size() == 0  ){
					
					Map<String, Object> saveShipmentParam = new HashMap<String, Object>();
					saveShipmentParam.put("shipToGeography", currentOrder.get("geography_name").toString());
					saveShipmentParam.put("consignee", currentOrder.get("user_rec_name").toString());
					saveShipmentParam.put("shipToAddr", currentOrder.get("user_rec_addr").toString());
					saveShipmentParam.put("shipToDistrict", currentOrder.get("user_rec_area").toString());
					saveShipmentParam.put("shipToCity", currentOrder.get("user_rec_city").toString());
					saveShipmentParam.put("shipToProvince", currentOrder.get("user_rec_province").toString());
					saveShipmentParam.put("shipToCountry", currentOrder.get("user_rec_country").toString());
					
					//发货国家
					saveShipmentParam.put("consigner_country_id", currentOrder.get("vendor_address_country_id").toString());
					//收货国家
					saveShipmentParam.put("consignee_country_id", currentOrder.get("address_country_id").toString());

					
					int row = iShipmentService.saveShipmentByOrderId(map);
					if (row != 0){
						
					}
					
				//如果匹配的Shipment 只存在一个,就直接关联箱子   并把订单存入箱子
				}else if(resultMap.size() == 1){
					
					//箱子关联Shipment
					Map<String, Object> updateContainer = new HashMap<String, Object>(); 
					updateContainer.put("shipment_id", resultMap.get(0).get("shipment_id").toString());
					updateContainer.put("container_id", Long.parseLong(map.get("containerId").toString()));
					logger.info(MessageFormat.format("packOrder updateContainerByCondition shipment_id:{0},container_id:{1}",resultMap.get(0).get("shipment_id").toString(),Long.parseLong(map.get("containerId").toString())));
					int row = containerService.updateContainerByCondition(conditionMap);
					if(row > 0 ){
						//订单加入箱子
					}
				
				//如果匹配的Shipment 存在多个，则返回列表供选择
				}else if(resultMap.size() > 1){
					
				}
			}
		
		//如果箱子中存在订单，则直接加入箱子
		}else{
			

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
	public ResultMessage updateLogisticsProduct(Map<String,Object> orderMap,Map<String,Object> shipMentMap){
		ResultMessage result= new ResultMessage();
		result.errorStatus();
		
		//校验该订单跟箱子所属的Shipment的目的地是否一致,一致则加入,是否分段运输，发货员自行判断
		if(!orderMap.get("geography_id").toString().equals(shipMentMap.get("geography_id").toString())){
			
			if(shipMentMap.get("shipment_type_id").toString().equals("1")){
				return result;
			}else if(shipMentMap.get("shipment_type_id").toString().equals("2")){
				//比较具体地址 省市区
			}
			

		}
		
		
		//添加订单跟箱子的关联,并修改状态为READYTOSHIP
		LogisticsProduct logisticsProduct = new LogisticsProduct();
		logisticsProduct.setLogistics_product_id(Long.parseLong(orderMap.get("logistics_product_id").toString()));
		logisticsProduct.setContainer_id(Long.parseLong(orderMap.get("containerId").toString()));
		logisticsProduct.setStatus(OrderStatusType.READYTOSHIP);
		int row = iLogisticsProductService.updateByLogisticsProduct(logisticsProduct);
		
		if(row > 0){
			result.successStatus();
		}else{
			result.setMsg("Boxed success");
		}
		return result;
	}
}
