package com.intramirror.web.controller.api.eds;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.EDSProduct;
import pk.shoplus.model.Result;
import pk.shoplus.service.ApiEndpointService;
import pk.shoplus.service.request.api.IGetPostRequest;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/11.
 */
@Controller
@RequestMapping("/eds")
public class EdsProductAllProducerController {

    private final Logger logger = Logger.getLogger(EdsProductAllProducerController.class);

    private final int limit = 100;

    private IGetPostRequest requestGet = new GetPostRequestService();

    @RequestMapping("/syn_all_product_producer")
    @ResponseBody
    public Map<String,Object> execute(){
        MapUtils mapUtils = new MapUtils(new HashMap<String,Object>());
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.open();
            ApiEndpointService apiEndpointService = new ApiEndpointService(conn);

            List<Map<String, Object>> apiEndpointList = apiEndpointService.getapiEndpointInfoByCondition("eds", "getProducts");
            logger.info("EdsProductAllProducerControllerExecute,getapiEndpointInfoByCondition,apiEndpointList:" + new Gson().toJson(apiEndpointList));

            if(apiEndpointList == null || apiEndpointList.size() == 0) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("info","select error !").getMap();
            }

            boolean flag = true;
            int offset = 0;
            while (flag) {
                String url = apiEndpointList.get(0).get("url").toString();
                String store_code = apiEndpointList.get(0).get("store_code").toString();
                String vendor_id = apiEndpointList.get(0).get("vendor_id").toString();
                String api_configuration_id = apiEndpointList.get(0).get("api_configuration_id").toString();
                if(StringUtils.isBlank(url)) {
                    break;
                }
                logger.info("EdsProductAllProducerControllerExecute,url:"+url);

                url = url + "?storeCode=" + store_code + "&limit=" + limit +"&offset=" + offset;

                String responseData = requestGet.requestMethod(GetPostRequestService.HTTP_GET,url,null);
                logger.info("SynEdsAllProductUrl:"+url);
                logger.info("SynEdsAllProductResponseData:"+responseData);

                if(StringUtils.isBlank(responseData)) {
                    logger.info(" responseData is null ...");
                    break;
                }

                Map<String, Result> map = (Map<String, Result>) JSONObject.parse(responseData);
                Map<String, Object> mapResult = (Map<String, Object>) map.get("results");
                List<EDSProduct> edsProductList = (List<EDSProduct>) mapResult.get("items");
                if(edsProductList == null || edsProductList.size() == 0) {
                    logger.info("SynEdsAllProduct end !!!");
                    break;
                }

                for(int i = 0,len = edsProductList.size();i<len;i++){
                    Map<String,Object> mqDataMap = new HashMap<String,Object>();
                    mqDataMap.put("reqCode", mapResult.get("reqCode") == null ? "" : mapResult.get("reqCode").toString());
                    mqDataMap.put("count", mapResult.get("count") == null ? "" : mapResult.get("count").toString());
                    mqDataMap.put("product", edsProductList.get(i));
                    mqDataMap.put("store_code", store_code);
                    mqDataMap.put("vendor_id", vendor_id);
                    mqDataMap.put("api_configuration_id", api_configuration_id);
                    mqDataMap.put("full_update_product","1");

                    logger.info("EdsProductAllProducerControllerExecute,mqDataMap:" + new Gson().toJson(mqDataMap));
                }
                offset = offset + limit;
            }
        } catch (Exception e) {
            if(conn != null) {conn.rollback();conn.close();}
        } finally {
            if(conn != null) {conn.close();}
        }
        return mapUtils.getMap();
    }

}
