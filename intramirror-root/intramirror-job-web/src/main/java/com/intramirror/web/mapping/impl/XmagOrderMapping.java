package com.intramirror.web.mapping.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.product.api.service.IApiMqService;

import pk.shoplus.model.OrderManagement;
import pk.shoplus.model.OrderRefund;
import pk.shoplus.model.OrderManagement.OrderMager;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.MapUtils;

/**
 * @author yml
 * @date 2017/8/30
 */
@Service
public class XmagOrderMapping implements IMapping{

    private static Logger logger = Logger.getLogger(XmagOrderMapping.class);
    
    @Autowired
    private ApiParameterService apiParameterService;
    
    @Autowired
    private IApiMqService apiMqService;
    
    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info(" start XmagOrderMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
        	logger.info("mqData : ===========>>>>>>" +mqData);
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            logger.info("DataMap :" + new Gson().toJson(mqDataMap));
         	List<Map<String, Object>> apiEndpointList = apiMqService.selectMqByName(mqDataMap.get("mqName").toString());
         	// 获取相关接口数据
         	Map<String, Object> urlMap = null;
         	Map<String, Object> apiEndpointMap = null;
         	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
         		apiEndpointMap = apiEndpointList.get(0);
     			urlMap = this.getUrl(apiEndpointMap, mqDataMap);
     			String json = "";
             	IGetPostRequest requestGet = new GetPostRequestService();
        		logger.info("job XmagOrderMapping  Call the interface to get the data    url:"+urlMap.get("url").toString());
        		json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, urlMap.get("url").toString(), null);
        		logger.info("job XmagOrderMapping result:"+json);
        		
        		if (StringUtils.isNotBlank(json)){
        			JSONObject jsonOjbect = JSONObject.parseObject(json);
        			String result = jsonOjbect.get("Status").toString();
        			logger.info("job XmagOrderMapping result:"+result);
        			if ("KO".equals(result)){
        				//没有库存执行退款
        				OrderRefund orderRefund = new OrderRefund();
        				String logisProductId = mqDataMap.get("logistics_product_id").toString();
        				if (orderRefund != null){
        					orderRefund.orderRefund(logisProductId);
        					mapUtils.putData("status", StatusType.SUCCESS).putData("info","XmagOrderMapping orderRefund SUCCESS :" + logisProductId);
        				}
        			}
        			mapUtils.putData("status", StatusType.SUCCESS).putData("info","XmagOrderMapping SUCCESS :" +mqDataMap.get("logistics_product_id").toString());
        		}
         	}else{
         		logger.info(" apiEndpointList is null");
         	}
         	mapUtils.putData("status", StatusType.SUCCESS).putData("info","XmagOrderMapping SUCCESS :" +mqDataMap.get("logistics_product_id").toString());
		} catch (Exception e) {
			e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("status", StatusType.FAILURE).putData("info","XmagOrderMapping error message : " + e.getMessage());
		}
        logger.info(" end XmagOrderMapping.handleMappingAndExecute()");
        return mapUtils.getMap();
    }

    public Map<String, Object> getUrl (Map<String, Object> apiEndpoint, Map<String,Object> mqDataMap) throws Exception {
    	logger.info("job getUrl 入参:"+new Gson().toJson(apiEndpoint));
    	
        StringBuffer urlBuffer = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> apiParameterList = null;
        String apiEndPointId = "";

        if (null != apiEndpoint) {
            urlBuffer.append(apiEndpoint.get("url"));
            apiEndPointId = apiEndpoint.get("api_end_point_id").toString();
        }

        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	param.put("api_end_point_id", apiEndPointId);
            apiParameterList = apiParameterService.getapiParameterByCondition(param);
            //获取数据
	    	OrderManagement orderManagement = new OrderManagement();
	    	Long vendorId = Long.parseLong(mqDataMap.get("vendorId").toString());
	    	int logisticsProductId = (Integer.parseInt(mqDataMap.get("logistics_product_id").toString())-1);
	    	Long pageTokenId = (long) logisticsProductId;
	    	int limit = 0;
	    	//查询当前的订单信息
	    	logger.info("OrderList param : vendorId" + vendorId + "  pageTokenId :" +pageTokenId +" limit :" +limit);
     		List<OrderMager> list = orderManagement.getOrderList(vendorId, pageTokenId, limit);
     		logger.info("OrderList result :" + new Gson().toJson(list));
     		if (null != list && 0<list.size()){
     			for (OrderMager orderMager : list) {
     				if (mqDataMap.get("logistics_product_id").toString().equals(orderMager.getOrderId())){
     					mqDataMap.put("orderMager", new Gson().toJson(orderMager));
     					break;
     				}
				}
     		}
     		if(mqDataMap.get("orderMager") == null || StringUtils.isBlank(mqDataMap.get("orderMager").toString())){
     			mqDataMap.put("orderMager", null);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(null != apiParameterList && apiParameterList.size() > 0) {
            String paramKey = "";
            String paramValue = "";
            urlBuffer.append("?");
            JSONObject jsonOjbect = JSONObject.parseObject(new Gson().toJson(mqDataMap));
            if(mqDataMap.get("orderMager") == null || StringUtils.isBlank(mqDataMap.get("orderMager").toString())){
            	return null;
            }
			Map<String, Object> orderMap = (Map<String, Object>) JSONObject.parseObject(jsonOjbect.get("orderMager").toString());
	        for (Map<String, Object> apiParameter : apiParameterList) {
	            paramKey = apiParameter.get("param_key").toString();
	            paramValue = apiParameter.get("param_value").toString();
	            if ("DBContext".equals(paramKey)) {
	                urlBuffer.append(paramKey+"="+paramValue+"&");
	            }
	            if ("Key".equals(paramKey)) {
	            	urlBuffer.append(paramKey+"="+paramValue+"&");
	            }
	            if ("order_no".equals(paramKey)){
	            	urlBuffer.append(paramKey+"="+paramValue+"&");
	            }
	            if ("purchase_no".equals(paramKey)) {
	            	urlBuffer.append(paramKey+"="+orderMap.get("orderNumber").toString()+"&");
	            }
	            if ("barcode".equals(paramKey)) {
	            	String src = orderMap.get("barcode").toString().equals("#")?"":orderMap.get("barcode").toString();
	            	urlBuffer.append(paramKey+"="+src+"&");
	            }
	            if ("ordQty".equals(paramKey)) {
	            	urlBuffer.append(paramKey+"="+orderMap.get("itemsCount").toString()+"&");
	            }
	            if ("sellPrice".equals(paramKey)) {
	            	urlBuffer.append(paramKey+"="+orderMap.get("price").toString()+"&");
	            }
	            
	        }
        }
        String url =  urlBuffer.toString();
        map.put("url", url);
        map.put("storeCode", apiEndpoint.get("store_code"));
        map.put("apiEndPointId", apiEndPointId);
        return map;
    }
    
  
}
