package com.intramirror.web.controller.api.cloudstore;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IDataMapping;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.enums.ApiParamterEnum;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.ApiEndpointService;
import pk.shoplus.service.ApiParameterService;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Controller
@RequestMapping("/cloudStore")
public class CloudStoreSynSkuProducerController {

    private final Logger logger = Logger.getLogger(CloudStoreSynSkuProducerController.class);
    Integer index = 0;
    Integer step = 0;

    @Resource(name = "cloudStoreProductMapping")
    private IDataMapping iDataMapping;


    @RequestMapping("/syn_sku_producer")
    @ResponseBody
    public Map<String,Object> execute(){
        index = 0;step=1;
        MapUtils mapUtils = new MapUtils(new HashMap<String,Object>());
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();

            ApiParameterService apiParameterService = new ApiParameterService(conn);
            ApiEndpointService apiEndpointService = new ApiEndpointService(conn);
            IGetPostRequest getPostRequestService = new GetPostRequestService();

            List<Map<String, Object>> apiEndpointList = apiEndpointService.getapiEndpointInfoByCondition("cloudstore", "CloudStoreSynProduct");

            if(apiEndpointList != null && apiEndpointList.size() != 0) {
                Map<String, Object> apiEndPointMap = apiEndpointList.get(0);
                String apiEndPointId = apiEndPointMap.get("api_end_point_id").toString();
                String url = apiEndPointMap.get("url").toString();

                // get parameter
                List<Map<String,Object>> apiParamMap =  apiParameterService.getapiParameterByCondition(apiEndPointId);
                Map<String,Object> params = new HashMap<>();
                for(Map map : apiParamMap) {
                    String param_key = map.get("param_key").toString();
                    String param_value = map.get("param_value").toString();

                    if(ApiParamterEnum.MERCHANT_ID.getCode().equalsIgnoreCase(param_key)) {
                        params.put(ApiParamterEnum.MERCHANT_ID.getCode(),param_value);
                    } else if(ApiParamterEnum.TOKEN.getCode().equalsIgnoreCase(param_key)){
                        params.put(ApiParamterEnum.TOKEN.getCode(),param_value);
                    }
                }

                ProductEDSManagement.VendorOptions vendorOption = new ProductEDSManagement().getVendorOptions();
                vendorOption.setVendorId(Long.parseLong(apiEndPointMap.get("vendor_id").toString()));

                if(conn != null) {conn.close();}

                while (true) {
                    logger.info("CloudStoreAllUpdateProductControllerExecute,StartRequestMethod,url:"+url+",params:"+new Gson().toJson(params)+",step:"+step);
                    String postResponse = getPostRequestService.requestMethod(GetPostRequestService.HTTP_POST,url,new Gson().toJson(params));
                    logger.info("CloudStoreAllUpdateProductControllerExecute,EndRequestMethod,url:"+url+",params:"+new Gson().toJson(params)+",postResponse"+postResponse);
                    if(StringUtils.isNotBlank(postResponse)) {
                        JSONObject jsonObject = JSONObject.parseObject(postResponse);
                        this.handleBody(jsonObject,vendorOption);
                        if(jsonObject.get("next_step") != null) {
                            params.put("step",jsonObject.get("next_step"));
                            step++;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                logger.info("CloudStoreAllUpdateProductControllerExecute,共请求数据"+index+"条;共请求"+step+"批;");
                mapUtils.putData("status", StatusType.SUCCESS).putData("info","共请求数据"+index+"条;共请求"+step+"批;");
            } else {
                mapUtils.putData("status",StatusType.FAILURE).putData("info","apiEndpointList is null !!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("CloudStoreAllUpdateProductControllerExecute,errorMessage : "+ ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status",StatusType.FAILURE).putData("info","error message : " + e.getMessage());
            if(conn != null) {conn.close();}
        } finally {
            if(conn != null) {conn.close();}
        }
        return mapUtils.getMap();
    }

    public void handleBody(JSONObject jsonObject,ProductEDSManagement.VendorOptions vendorOption){
        logger.info("CloudStoreAllUpdateProductControllerHandleBody,inputParams,jsonObject:"+jsonObject.toJSONString()
        +",vendorOption:"+new Gson().toJson(vendorOption));

        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray inventorys = data.getJSONArray("inventory");
        Iterator<Object> it = inventorys.iterator();
        while(it.hasNext()) {
            JSONObject inventory = (JSONObject) it.next();
            Map<String,Object> mqMap = new HashMap<>();
            mqMap.put("vendorOption",vendorOption);
            mqMap.put("responseBody",inventory);
            mqMap.put("full_update_product","1");
            logger.info("CloudStoreAllUpdateProductControllerHandleBody,mqMap:"+new Gson().toJson(mqMap));
            iDataMapping.mapping(mqMap);
            index ++;
        }
    }
}
