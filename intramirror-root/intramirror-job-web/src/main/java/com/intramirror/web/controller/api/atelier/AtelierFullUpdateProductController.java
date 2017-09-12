package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.IApiMqService;
import com.intramirror.web.enums.QueueNameJobEnum;
import com.intramirror.web.util.QueueNameEnumUtils;
import com.intramirror.web.util.QueueUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/3.
 */
@Controller
@RequestMapping("/api")
public class AtelierFullUpdateProductController {

    private static Logger logger = Logger.getLogger(AtelierFullUpdateProductController.class);

    @Autowired
    private IApiMqService iApiMqService;

    @RequestMapping(value = "/fullUpdateProduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> execute(HttpServletRequest request){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierFullUpdateProductControllerExecute,params,storeID:"+storeID+",version:"+version+",body:"+body);

            if(StringUtils.isBlank(storeID)) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","1")
                        .putData("ErrorMsg","E001: Parameter StoreID is mandatory")
                        .putData("TimeStamp",new Date()).getMap();
            }

            if(StringUtils.isBlank(version) || !"1.0".equals(version)) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","1")
                        .putData("ErrorMsg","E001: Parameter Version is mandatory")
                        .putData("TimeStamp",new Date()).getMap();
            }

            if(StringUtils.isBlank(body)) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","30")
                        .putData("ErrorMsg","E030: Empty Request")
                        .putData("TimeStamp",new Date()).getMap();
            }

            JSONObject jsonObjectBody = JSON.parseObject(body);
            logger.info("AtelierFullUpdateProductControllerExecute,jsonObjectBody:"+jsonObjectBody.toJSONString());

            String boutique_id = jsonObjectBody.getString("boutique_id");
            if(StringUtils.isBlank(boutique_id)) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","205")
                        .putData("ErrorMsg","E205: Invalid boutique_id")
                        .putData("TimeStamp",new Date()).getMap();
            }

            Map<String,Object> params = new HashMap<>();
            params.put("name","fullUpdateProduct");
            params.put("system","intramirror");
            params.put("store_code",storeID);
            logger.info("AtelierFullUpdateProductControllerExecute,selectApiCfgParams:"+new Gson().toJson(params));
            List<Map<String,Object>> apiCfgMaps = iApiMqService.selectApiInfoByAtelier(params);
            logger.info("AtelierFullUpdateProductControllerExecute,apiCfgMaps:"+new Gson().toJson(apiCfgMaps));
            if(apiCfgMaps == null || apiCfgMaps.size() == 0 || apiCfgMaps.size() > 1) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","1")
                        .putData("ErrorMsg","E001: Parameter StoreID is error")
                        .putData("storeID",storeID)
                        .putData("TimeStamp",new Date()).getMap();
            }

            String mqName = apiCfgMaps.get(0).get("name").toString();
            String api_configuration_id = apiCfgMaps.get(0).get("api_configuration_id").toString();
            String vendor_id = apiCfgMaps.get(0).get("vendor_id").toString();
            logger.info("AtelierFullUpdateProductControllerExecute,mqName:"+mqName);
            QueueNameJobEnum queueNameJobEnum =  QueueNameEnumUtils.searchQueue(mqName);
            logger.info("AtelierFullUpdateProductControllerExecute,queueNameJobEnum:"+new Gson().toJson(queueNameJobEnum));

            if(queueNameJobEnum == null) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","1")
                        .putData("ErrorMsg","E001: Parameter StoreID is error")
                        .putData("queueNameJobEnum",new Gson().toJson(queueNameJobEnum))
                        .putData("TimeStamp",new Date()).getMap();
            }


            ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
            vendorOptions.setVendorId(Long.parseLong(vendor_id));

            Map<String,Object> mqDataMap = new HashMap<String,Object>();
            mqDataMap.put("Data", jsonObjectBody);
            mqDataMap.put("StoreID", storeID);
            mqDataMap.put("version", version);
            mqDataMap.put("full_update_product","1");
            mqDataMap.put("vendorOptions", vendorOptions);

            logger.info("AtelierFullUpdateProductControllerExecute,putMessage:"+new Gson().toJson(mqDataMap));
            QueueUtils.putMessage(mqDataMap,queueNameJobEnum, QueueTypeEnum.PENDING);

            mapUtils.putData("ResponseStatus","1000");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierFullUpdateProductControllerExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            return mapUtils.putData("ResponseStatus","2000")
                    .putData("ErrorCode","1")
                    .putData("ErrorMsg","500: E500: Create product failed:"+ExceptionUtils.getExceptionDetail(e))
                    .putData("TimeStamp",new Date()).getMap();

        }
        logger.info("AtelierFullUpdateProductControllerExecute,mapUtils:"+new Gson().toJson(mapUtils));
        return mapUtils.getMap();
    }

}
