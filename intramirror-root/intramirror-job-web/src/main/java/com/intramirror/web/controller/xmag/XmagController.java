/**
 * 
 */
package com.intramirror.web.controller.xmag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.main.api.service.ApiEndPointService;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.web.enums.QueueNameJobEnum;
import com.intramirror.web.util.QueueUtils;

import pk.shoplus.service.SeasonService;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;

/**
 * @author yuan
 *
 */
@Controller
@RequestMapping("/job/xmag")
public class XmagController {

	private static Logger logger = Logger.getLogger(XmagController.class);
	
	@Autowired
    private ApiEndPointService apiEndPointService;
	    
    @Autowired
    private ApiParameterService apiParameterService;
    
    @RequestMapping(value="/getProducts", method=RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> getProducts(){
    	//返回参数MAP
    	Map<String, Object> resultMap = new HashMap<>();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
	    	param.put("system", "xmag");
	    	param.put("name", "GetAllDbcontext");
	    	logger.info("job updateProduct 获取apiEndpointList 入参:"+new Gson().toJson(param));
	    	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
	    	//循环处理数据，知道处理结束
	    	while(true){
	    		//查询当前游标数
	    		int StartIndex = 0;
	    		int EndIndex = 0;
	    		int apiEndPointId =0;
		    	// 获取相关接口数据
		    	Map<String, Object> urlMap = null;
		    	Map<String, Object> apiEndpointMap = null;
		    	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
		    		apiEndpointMap = apiEndpointList.get(0);
					urlMap = this.getUrl(apiEndpointMap);
		    	}
		    	String json = "";
		    	//获取数据
		    	if (null != urlMap){
		    		IGetPostRequest requestGet = new GetPostRequestService();
		    		logger.info("job getProudcts  Call the interface to get the data    url:"+urlMap.get("url").toString());
		    		json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, urlMap.get("url").toString(), null);
		    		logger.info("job getProudcts result:"+json);
		    		StartIndex = Integer.parseInt(urlMap.get("StartIndex").toString());
		    		EndIndex = Integer.parseInt(urlMap.get("EndIndex").toString());
		    		apiEndPointId = Integer.parseInt(urlMap.get("apiEndPointId").toString());
		    	}
		    	//如果请求数据不为空，放入MQ队列
		    	JSONObject jsonOjbect = JSONObject.parseObject(json);
		    	if (StringUtils.isNotBlank(json)){
		    		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonOjbect.get("product");
		    		logger.info("product list size:"+list.size());
		    		if(list !=null && list.size() > 0 ){
		    			int index = 1;
		    			for(Map<String, Object> product :list ){
	                        Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                        mqDataMap.put("product", product);
	                        mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
	                        mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
	                        mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
	                        logger.info("Push Index" + index);
	                        String src = new Gson().toJson(mqDataMap);
	                        System.out.println(src);
	                        index++;
	                        QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(),QueueNameJobEnum.XmagSynProduct);
	            		}
		    		}
		    	}else{
		    		logger.info("job getProudcts result null ");
		    		break;
		    	}
		    	StartIndex += Integer.parseInt(jsonOjbect.get("number").toString());
		    	//每次处理修改游标
		    	Map<String, Object> kvMap = new HashMap<>();
		    	kvMap.put("paramValue", StartIndex);
		    	kvMap.put("paramKey", "StartIndex");
		    	kvMap.put("apiEndPointId", apiEndPointId);
		    	apiParameterService.updateApiParameterByKey(kvMap);
		    	EndIndex = StartIndex+50;
		    	kvMap = new HashMap<>();
		    	kvMap.put("paramValue", EndIndex);
		    	kvMap.put("paramKey", "EndIndex");
		    	kvMap.put("apiEndPointId", apiEndPointId);
		    	apiParameterService.updateApiParameterByKey(kvMap);
		    	logger.info("This StartIndex : "+StartIndex +" ---------This endIndex :"+EndIndex);
		    	if (50 != Integer.parseInt(jsonOjbect.get("number").toString()))
		    		break;
	    	
	    	}
	    	
    	} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error message : " + e.getMessage());
			resultMap.put("error", e.getMessage());
		}
		return resultMap;
	}
    
    @RequestMapping(value="/getProductByDate", method=RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> getProductByDate(){
    	//返回参数MAP
    	Map<String, Object> resultMap = new HashMap<>();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
	    	param.put("system", "xmag");
	    	param.put("name", "getProductByDate");
	    	logger.info("job getProductByDate 获取apiEndpointList 入参:"+new Gson().toJson(param));
	    	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
	    	//循环处理数据，知道处理结束
	    	while(true){
	    		//查询当前游标数
	    		int StartIndex = 0;
	    		int EndIndex = 0;
	    		int apiEndPointId =0;
		    	// 获取相关接口数据
		    	Map<String, Object> urlMap = null;
		    	Map<String, Object> apiEndpointMap = null;
		    	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
		    		apiEndpointMap = apiEndpointList.get(0);
					urlMap = this.getUrl(apiEndpointMap);
		    	}
		    	String json = "";
		    	//获取数据
		    	if (null != urlMap){
		    		IGetPostRequest requestGet = new GetPostRequestService();
		    		logger.info("job getProductByDate  Call the interface to get the data    url:"+urlMap.get("url").toString());
		    		json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, urlMap.get("url").toString(), null);
		    		logger.info("job getProductByDate result:"+json);
		    		StartIndex = Integer.parseInt(urlMap.get("StartIndex").toString());
		    		EndIndex = Integer.parseInt(urlMap.get("EndIndex").toString());
		    		apiEndPointId = Integer.parseInt(urlMap.get("apiEndPointId").toString());
		    	}
		    	//如果请求数据不为空，放入MQ队列
		    	JSONObject jsonOjbect = JSONObject.parseObject(json);
		    	if (StringUtils.isNotBlank(json)){
		    		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonOjbect.get("product");
		    		logger.info("getProductByDate list size:"+list.size());
		    		if(list !=null && list.size() > 0 ){
		    			int index = 1;
		    			for(Map<String, Object> product :list ){
	                        Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                        mqDataMap.put("product", product);
	                        mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
	                        mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
	                        mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
	                        logger.info("Push Index" + index);
	                        String src = new Gson().toJson(mqDataMap);
	                        logger.info("Push data" + src);
	                        index++;
	                        QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(),QueueNameJobEnum.XmagSynProductByDate);
	            		}
		    		}
		    	}else{
		    		logger.info("job getProductByDate result null ");
		    		break;
		    	}
		    	StartIndex += Integer.parseInt(jsonOjbect.get("number").toString());
		    	//每次处理修改游标
		    	Map<String, Object> kvMap = new HashMap<>();
		    	kvMap.put("paramValue", StartIndex);
		    	kvMap.put("paramKey", "StartIndex");
		    	kvMap.put("apiEndPointId", apiEndPointId);
		    	apiParameterService.updateApiParameterByKey(kvMap);
		    	EndIndex = StartIndex+50;
		    	kvMap = new HashMap<>();
		    	kvMap.put("paramValue", EndIndex);
		    	kvMap.put("paramKey", "EndIndex");
		    	kvMap.put("apiEndPointId", apiEndPointId);
		    	apiParameterService.updateApiParameterByKey(kvMap);
		    	logger.info("This StartIndex : "+StartIndex +" ---------This endIndex :"+EndIndex);
		    	if (50 != Integer.parseInt(jsonOjbect.get("number").toString()))
		    		break;
	    	
	    	}
	    	
    	} catch (Exception e) {
			e.printStackTrace();
			logger.error(" error message : " + e.getMessage());
			resultMap.put("error", e.getMessage());
		}
		return resultMap;
    }
    

    @RequestMapping(value="/getAllStock", method=RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> getAllStock(){
    	//返回参数MAP
    	Map<String, Object> resultMap = new HashMap<>();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
	    	param.put("system", "xmag");
	    	param.put("name", "getAllStock");
	    	logger.info("job XmagAllStock 获取apiEndpointList 入参:"+new Gson().toJson(param));
	    	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
		    	// 获取相关接口数据
		    	Map<String, Object> urlMap = null;
		    	Map<String, Object> apiEndpointMap = null;
		    	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
		    		apiEndpointMap = apiEndpointList.get(0);
					urlMap = this.getUrl(apiEndpointMap);
		    	}
		    	String json = "";
		    	//获取数据
		    	if (null != urlMap){
		    		IGetPostRequest requestGet = new GetPostRequestService();
		    		logger.info("job XmagAllStock  Call the interface to get the data    url:"+urlMap.get("url").toString());
		    		json = requestGet.requestMethod(GetPostRequestService.HTTP_GET, urlMap.get("url").toString(), null);
		    		logger.info("job XmagAllStock result:"+json);
		    	}
		    	//如果请求数据不为空，放入MQ队列
		    	JSONObject jsonOjbect = JSONObject.parseObject(json);
		    	if (StringUtils.isNotBlank(json)){
		    		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonOjbect.get("listStockData");
		    		logger.info("XmagAllStock list size:"+list.size());
		    		if(list !=null && list.size() > 0 ){
		    			int index = 1;
		    			for(Map<String, Object> product :list ){
	                        Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                        mqDataMap.put("product", product);
	                        mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
	                        mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
	                        mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
	                        logger.info("Push Index" + index);
	                        String src = new Gson().toJson(mqDataMap);
	                        logger.info("Push data" + src);
	                        index++;
//	                        QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(),QueueNameJobEnum.XmagAllStock);
	            		}
		    		}
		    	}else{
		    		logger.info("job XmagAllStock result null ");
		    	}
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
                if ("DateStart".equals(paramKey)){
                	urlBuffer.append(paramKey+"="+getTimeByHour(6)+"&");
                	continue;
                }
                if ("DateEnd".equals(paramKey)){
                	urlBuffer.append(paramKey+"="+getTimeByHour(6)+"&");
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
	
	/**
	 * 根据输入数值获取当前时间之前时间 hour == 6 意大利时间
	 * @param hour
	 * @return
	 */
	public static String getTimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

}
