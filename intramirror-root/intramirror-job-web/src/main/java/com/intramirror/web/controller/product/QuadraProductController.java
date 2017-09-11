package com.intramirror.web.controller.product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intramirror.common.help.ExceptionUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pk.shoplus.common.Contants;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.FileUtil;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.main.api.service.ApiEndPointService;
import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.web.enums.QueueNameJobEnum;
import com.intramirror.web.util.GetPostRequestUtil;
import com.intramirror.web.util.QueueUtils;

import difflib.DiffRow;

@Controller
@RequestMapping("/job/product")
public class QuadraProductController {

    private static Logger logger = Logger.getLogger(QuadraProductController.class);

    @Autowired
    private ApiEndPointService apiEndPointService;
    
    @Autowired
    private ApiParameterService apiParameterService;
    
    @RequestMapping(value = "/updateProductAll",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updateProductAll(){

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	param.put("system", "quadra");
        	param.put("name", "QuadraSynAllProduct");
            logger.info("job updateProductAll 获取apiEndpointList 入参:"+new Gson().toJson(param));
        	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
        	
            // 获取相关接口数据
        	Map<String, Object> urlMap = null;
        	 Map<String, Object> apiEndpointMap = null;
        	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
        		apiEndpointMap = apiEndpointList.get(0);
                urlMap = this.getUrl(apiEndpointMap);
        	}
        	
        	if(urlMap != null){
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job updateProductAll  Call the interface to get the data    url:"+urlMap.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,urlMap.get("url").toString(),null);
                if(StringUtils.isNotBlank(json)) {
                	
                    // Quadra 拆分接口返回的product放入mq
                    logger.info("job updateProductAll 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);
                	
                	if(List !=null && List.size() > 0 ){
                		
                        logger.info("job updateProduct  遍历解析商品列表   存入MQ队列    productList Size:"+List.size());
                        int i = 0;
                		for(Map<String, Object> productInfo :List ){
                			
                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
                            mqDataMap.put("product", productInfo.toString());
                            mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
                            mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
                            mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
                            mqDataMap.put("full_update_product","1");
                            // 放入MQ
                			System.out.println(i++);
                            logger.info("QuadraProductControllerUpdateProductAll,putMessage,mqDateMap:"+new Gson().toJson(mqDataMap)+",urlMap:"+new Gson().toJson(urlMap));
                            QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(), QueueNameJobEnum.QuadraSynAllProduct);
                            
                            //跳出循环,测试用
//                            if(i>120){
//                            	break;
//                            }
                		}
                	}
                    

                }else{
                	 logger.info("job updateProductAll  请求接口获取的商品对象数据为空");
                }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("QuadraProductControllerUpdateProductAll,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            resultMessage.errorStatus().addMsg("error message : " + e.getMessage());
        }
        return resultMessage;
    }
    
    
    
    
    @RequestMapping(value = "/updateProductDay",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage updateProductDay(){

        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	param.put("system", "quadra");
        	param.put("name", "QuadraSynDayProduct");
            logger.info("job updateProductDay 获取apiEndpointList 入参:"+new Gson().toJson(param));
        	List<Map<String, Object>> apiEndpointList = apiEndPointService.getapiEndpointInfoByCondition(param);
        	
        	
            Date date = new Date();
            DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = format1.format(date);
            
            // 获取相关接口数据
        	Map<String, Object> urlMap = null;
        	 Map<String, Object> apiEndpointMap = null;
        	if(apiEndpointList != null && apiEndpointList.size() > 0 ){
        		apiEndpointMap = apiEndpointList.get(0);
                urlMap = this.getUrl(apiEndpointMap);
        	}
        	
        	if(urlMap != null){
        		GetPostRequestUtil requestGet = new GetPostRequestUtil();
                logger.info("job updateProductDay  Call the interface to get the data    url:"+urlMap.get("url").toString());
                String json = requestGet.requestMethodType(GetPostRequestUtil.HTTP_GET,urlMap.get("url").toString(),null);
                
                if(StringUtils.isNotBlank(json)) {
                	
                    // Quadra 拆分接口返回的product放入mq
                    logger.info("job updateProduct 返回的json 数据转换成对象");
                	List<Map<String, Object>> List = (List<Map<String, Object>>) JSONObject.parse(json);

	                String originProductPath = Contants.quadra_file_path + Contants.quadra_origin_product_day + Contants.quadra_file_type;
	                String revisedProductPath = Contants.quadra_file_path + Contants.quadra_revised_product_day + Contants.quadra_file_type;
	                
	                //如果第一次更新,全部添加到MQ 不需要比较
	                if(!FileUtil.fileExists(originProductPath)) {
	                	logger.info("job updateProductDay  第一次更新,全部添加到MQ 不需要比较  ");
	                	FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_origin_product_day + Contants.quadra_file_type,converString(List));
	                	
	                	if(List !=null && List.size() > 0 ){
	                		
	                        logger.info("job updateProductDay  遍历解析商品列表   存入MQ队列    productList Size:"+List.size());
	                        int i = 0;
	                		for(Map<String, Object> productInfo :List ){
	                			
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            mqDataMap.put("product", productInfo.toString());
	                            mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
	                            mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
	                            mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
	                            
	                            // 放入MQ
	                			System.out.println(i++);
	                			logger.info("QuadraProductControllerUpdateProductDay,putMessage,mqDateMap:"+new Gson().toJson(mqDataMap)+",urlMap:"+new Gson().toJson(urlMap));
	                            QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(), QueueNameJobEnum.QuadraSynDayProduct);
	                            
//	                            //跳出循环,测试用
//	                            if(i>200){
//	                            	break;
//	                            }
	                		}
	                	}
	                }else{
	                	
	                	logger.info("job updateProductDay  比较文件差异,获取修改的增量加入MQ  ");
		            	// 1.创建
		                FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_revised_product_day+ Contants.quadra_file_type,converString(List));
		
		                // 2.文件进行对比
		                List<DiffRow> diffRows = FileUtil.CompareTxtByType(originProductPath,revisedProductPath);
		
		                // 3.消息筛入MQ
		                StringBuffer stringBuffer = new StringBuffer();
		                for(DiffRow diffRow : diffRows) {
		                    DiffRow.Tag tag = diffRow.getTag();
		                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
		                        logger.info("change -------" + new Gson().toJson(diffRow));
		                        stringBuffer.append(diffRow.getNewLine()+"\n");
		                        
	                            Map<String,Object> mqDataMap = new HashMap<String,Object>();
	                            //读取出来会自动带换行符,需要转换
	                            mqDataMap.put("product", diffRow.getNewLine().replace("<br>", ""));
	                            mqDataMap.put("store_code", apiEndpointMap.get("store_code").toString());
	                            mqDataMap.put("vendor_id", apiEndpointMap.get("vendor_id").toString());
	                            mqDataMap.put("api_configuration_id", apiEndpointMap.get("api_configuration_id").toString());
	                            
	                            // 放入MQ
	                			logger.info("QuadraProductControllerUpdateProductDay,putMessage,mqDateMap:"+new Gson().toJson(mqDataMap)+",urlMap:"+new Gson().toJson(urlMap));
	                            QueueUtils.putMessage(mqDataMap, "",urlMap.get("url").toString(), QueueNameJobEnum.QuadraSynDayProduct);
	                            
//	                            //跳出循环,测试用
//	                            if(i>200){
//	                            	break;
//	                            }
		                    } 
		                }
		                
		                // 4.处理结束后备份origin,并重置origin
		                String originContentAll = FileUtil.readFile(originProductPath);
		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_origin_product_day + Contants.quadra_file_type,originContentAll);
		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_revised_product_day  + Contants.quadra_file_type,converString(List));
		                FileUtil.createFileByType(Contants.quadra_file_path,"bak_"+Contants.quadra_change_product_day +strDate+ Contants.quadra_file_type,stringBuffer.toString());
		                FileUtil.createFileByType(Contants.quadra_file_path,Contants.quadra_origin_product_day + Contants.quadra_file_type,converString(List));
	                }
	                
	                
                }else{
               	 logger.info("job updateProductDay  请求接口获取的商品对象数据为空");
               }
            	
                resultMessage.successStatus().addMsg("SUCCESS");
        	}

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("QuadraProductControllerUpdateProductDay,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
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
    
    
    /**
     * 转换类型
     * @param list
     * @return
     */
    public String converString(List<Map<String, Object>> list){
    	 StringBuffer stringBuffer = new StringBuffer();
    	 
    	 if(list != null && list.size() > 0 ){
    		 for(Map<String, Object> info : list){
    			 stringBuffer.append(info.toString()).append("\n");
    		 }
    	 }
    	 
    	return stringBuffer.toString();
    }
    
        
}
