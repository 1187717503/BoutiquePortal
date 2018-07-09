/**
 * 
 */
package com.intramirror.web.controller.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.main.api.service.ApiEndPointService;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.product.api.service.IApiMqService;
import com.intramirror.web.util.QueueNameEnumUtils;
import com.intramirror.web.util.QueueUtils;

import pk.shoplus.model.OrderManagement;
import pk.shoplus.model.OrderManagement.OrderMager;

/**
 * @author yml
 *
 */
@Controller
@RequestMapping("/job/order")
public class OrderController {
	
	private static Logger logger = Logger.getLogger(OrderController.class);
	
	@Autowired
    private ApiEndPointService apiEndPointService;
	
	@Autowired
    private ApiParameterService apiParameterService;
	
	@Autowired
	private IApiMqService apiMqService;
	
 	@RequestMapping(value="/setOrder", method=RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> setOrder(String mqName){
    	//返回参数MAP
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
    		logger.info("job setOrder Start ");
	    	logger.info("job setOrder 获取apiEndpointList 入参:" + mqName);
	    	
	    	List<Map<String, Object>> apiEndpointList = apiMqService.selectMqByName(mqName);
//	    	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
	    	// 获取相关接口数据
	    	Map<String, Object> urlMap = null;
	    	Map<String, Object> apiEndpointMap = null;
	    	int apiEndPointId = 0;
	    	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
	    		apiEndpointMap = apiEndpointList.get(0);
				urlMap = this.getUrl(apiEndpointMap);
				apiEndPointId = Integer.parseInt(urlMap.get("apiEndPointId").toString());
	    	}
	    	//获取数据
	    	OrderManagement orderManagement = new OrderManagement();
	    	Long vendorId = Long.parseLong("0");
	    	Long pageTokenId = Long.parseLong("0");
	    	int limit = 0;
	    	if (null != urlMap && urlMap.size() > 0){
	    		vendorId = Long.parseLong(apiEndpointMap.get("vendor_id").toString());
	    		pageTokenId =  Long.parseLong(urlMap.get("offset").toString());
	    	}
	    	logger.info("OrderList param : vendorId" + vendorId + "  pageTokenId :" +pageTokenId +" limit :" +limit);
	    	//推送订单数据
	    	String orderId = "";
	    	List<OrderMager> list = orderManagement.getOrderList(vendorId, pageTokenId, limit);
	    	if (null != list && 0 < list.size()){
	    		orderId = list.get(list.size() - 1).getOrderId();
	    		int index = 1;
    			for(OrderMager order :list ){
                    Map<String,Object> mqDataMap = new HashMap<String,Object>();
                    mqDataMap.put("orderMager", new Gson().toJson(order));
                    mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
                    mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
                    mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
                    mqDataMap.put("mqName", mqName); 
                    mqDataMap.put("orderId", order.getOrderId());
                    logger.info("setOrder Push Index" + index);
                    String src = new Gson().toJson(mqDataMap);
                    logger.info("setOrder Push data" + src);
                    index++;
                    QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(),QueueNameEnumUtils.searchQueue(mqName));
        		}
    			Map<String, Object> kvMap = new HashMap<>();
    			kvMap.put("paramValue", orderId);
    			kvMap.put("paramKey", "offset");
    			kvMap.put("apiEndPointId", apiEndPointId);
    			logger.info("updateApiParameterByKey param " +new Gson().toJson(kvMap));
    			apiParameterService.updateApiParameterByKey(kvMap);
	    	}
	    	logger.info("job setOrder END " + list.size());
    	} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error message : " + e.getMessage());
			resultMap.put("error", e.getMessage());
		}
		return resultMap;
 	}
 	
 	public Map<String, Object> getUrl (Map<String, Object> apiEndpoint) throws Exception {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        String limit = "";
        String offset = "";
        String StartIndex = "";
        String EndIndex = "";
        if(null != apiParameterList && apiParameterList.size() > 0) {
            String paramKey = "";
            String paramValue = "";
            urlBuffer.append("?");

            for (Map<String, Object> apiParameter : apiParameterList) {
                paramKey = apiParameter.get("param_key").toString();
                paramValue = apiParameter.get("param_value").toString();

                if ("limit".equals(paramKey)) {
                    limit = paramValue;
                }
                if ("offset".equals(paramKey)) {
                    offset = paramValue;
                }
                if ("StartIndex".equals(paramKey)){
                	StartIndex = paramValue;
                }
                if ("EndIndex".equals(paramKey)){
                	EndIndex = paramValue;
                }
                if ("SeasonCode".equals(paramKey)){
                	continue;
                }
                urlBuffer.append(paramKey+"="+paramValue+"&");
            }
        }
        String url =  urlBuffer.toString();
        map.put("url", url);
        map.put("storeCode", apiEndpoint.get("store_code"));
        map.put("limit", limit);
        map.put("offset", offset);
        map.put("StartIndex", StartIndex);
        map.put("EndIndex", EndIndex);
        map.put("apiEndPointId", apiEndPointId);
        return map;
    }

}
