package com.intramirror.web.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.ApiEndPointService;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.web.enums.QueueNameJobEnum;
import com.intramirror.web.util.QueueUtils;

@Controller
@RequestMapping("/job/product")
public class QuadraProductController {

    private static Logger logger = LoggerFactory.getLogger(QuadraProductController.class);
    
    @Autowired
    private ApiEndPointService apiEndPointService;
    
    @Autowired
    private ApiParameterService apiParameterService;
    
    @RequestMapping(value = "/updateProduct",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updateProduct(){

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	param.put("system", "quadra");
        	param.put("name", "QuadraSynAllProduct");
            logger.info("job updateProduct 获取apiEndpointList 入参:"+new Gson().toJson(param));
        	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
        	
            // 获取相关接口数据
        	Map<String, Object> urlMap = null;
        	 Map<String, Object> apiEndpointMap = null;
        	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
        		apiEndpointMap = apiEndpointList.get(0);
                urlMap = this.getUrl(apiEndpointMap);
        	}
        	
        	if(urlMap != null){
                IGetPostRequest requestGet = new GetPostRequestService();
                logger.info("job updateProduct  Call the interface to get the data    url:"+urlMap.get("url").toString());
                String json = requestGet.requestMethod(GetPostRequestService.HTTP_GET,urlMap.get("url").toString(),null);
                if(StringUtils.isNotBlank(json)) {
                	
                    // Quadra 拆分接口返回的product放入mq
                    logger.info("job updateProduct 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);
                	
                	if(List !=null && List.size() > 0 ){
                		
                        logger.info("job updateProduct  遍历解析商品列表   存入MQ队列    productList Size:"+List.size());
                        int i = 0;
                		for(Map<String, Object> productInfo :List ){
                			
                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
                            mqDataMap.put("product", productInfo);
                            mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
                            mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
                            mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
                            
                            // 放入MQ
                			System.out.println(i++);
                            QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(), QueueNameJobEnum.QuadraSynProduct);
                            
                            //跳出循环,测试用
                            if(i>855){
                            	break;
                            }
                		}
                	}
                    

                }else{
                	 logger.info("job updateProduct  请求接口获取的商品对象数据为空");
                }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
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

                urlBuffer.append(paramKey+"="+paramValue+"&");
            }
        }
        String url =  urlBuffer.toString();

        map.put("url", url);
        map.put("storeCode", apiEndpoint.get("store_code"));
        map.put("limit", limit);
        map.put("offset", offset);

        return map;
    }
    
        
}
