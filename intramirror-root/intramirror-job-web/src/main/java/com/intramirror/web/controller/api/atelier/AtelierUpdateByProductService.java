package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.enums.QueueNameJobEnum;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.QueueNameEnumUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ResultHelper;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2017/9/19.
 *
 */
@Service(value = "atelierUpdateByProductService")
public class AtelierUpdateByProductService implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(AtelierUpdateByProductService.class);

    // init params
    private Map<String,Object> paramsMap;

    @Resource(name = "atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;

    public String all_update_product = "全量更新商品";

    public String update_product = "更新商品";

    public String create_product = "创建商品";

    // thread num
    private static final int threadNum = 10;

    public Map<String,Object> updateProduct(String body,String storeID,String version,String type){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            // 校验入参
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
            logger.info("AtelierUpdateByProductServiceUpdateProduct,jsonObjectBody:"+jsonObjectBody.toJSONString());
            String boutique_id = jsonObjectBody.getString("boutique_id");
            if(StringUtils.isBlank(boutique_id)) {
                return mapUtils.putData("ResponseStatus","2000")
                        .putData("ErrorCode","205")
                        .putData("ErrorMsg","E205: Invalid boutique_id")
                        .putData("TimeStamp",new Date()).getMap();
            }

            // 获取入参
            if(paramsMap.get(storeID) == null) {
                return ResultHelper.createErrorResult("206", "E206: StoreID non existent.storeID:"+storeID);
            }
            Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(storeID);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramsMap.get("executor");
            logger.info("AtelierUpdateByStockControllerExecute,getBaseData,paramMap:"+JSONObject.toJSONString(paramMap));
            String vendor_id = paramMap.get("vendor_id").toString();
            String eventName = paramMap.get("eventName").toString();
            ApiDataFileUtils fileUtils = new ApiDataFileUtils(eventName,eventName+type);
            fileUtils.bakPendingFile(boutique_id+"storeCode"+storeID+"vension"+version,body);

            // mapping
            Map<String,Object> bodyDataMap = new HashMap<>();
            bodyDataMap.put("Data",jsonObjectBody);
            bodyDataMap.put("full_update_product","1");
            logger.info("AtelierUpdateByProductServiceUpdateProduct,mapping,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",eventName:"+eventName);
            ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(bodyDataMap);
            logger.info("AtelierUpdateByProductServiceUpdateProduct,mapping,productOptions:"+JSONObject.toJSONString(productOptions)+",bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",eventName:"+eventName);

            ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
            vendorOptions.setVendorId(Long.parseLong(vendor_id));

            logger.info("AtelierUpdateByProductServiceUpdateProduct,executeThreadTool,eventName:"+eventName+",productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",fileUtils:"+JSONObject.toJSONString(fileUtils));
            CommonThreadPool.execute(eventName,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils));
            logger.info("AtelierUpdateByProductServiceUpdateProduct,executeThreadTool,eventName:"+eventName+",productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",fileUtils:"+JSONObject.toJSONString(fileUtils));
            mapUtils.putData("ResponseStatus","1000");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductServiceUpdateProduct,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return mapUtils.putData("ResponseStatus","2000")
                    .putData("ErrorCode","1")
                    .putData("ErrorMsg","500: E500: failed:"+ExceptionUtils.getExceptionDetail(e))
                    .putData("TimeStamp",new Date()).getMap();
        }
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        paramsMap = new HashMap<>();
        ThreadPoolExecutor executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        paramsMap.put("executor",executor);

        Map<String,Object> X4ZMP = new HashMap<>();
        X4ZMP.put("store_code","X4ZMP");
        X4ZMP.put("vendor_id","8");
        X4ZMP.put("vendor_name","Luciana Bari");
        X4ZMP.put("eventName","luciana");
        paramsMap.put("X4ZMP",X4ZMP);

        Map<String,Object> XIW2E = new HashMap<>();
        XIW2E.put("store_code","XIW2E");
        XIW2E.put("vendor_id","10");
        XIW2E.put("vendor_name","Dante 5");
        XIW2E.put("eventName","dante");
        paramsMap.put("XIW2E",XIW2E);

        Map<String,Object> UIWK2 = new HashMap<>();
        UIWK2.put("store_code","UIWK2");
        UIWK2.put("vendor_id","11");
        UIWK2.put("vendor_name","I Cinque Fiori");
        UIWK2.put("eventName","iCinque");
        paramsMap.put("UIWK2",UIWK2);

        Map<String,Object> ERS4S = new HashMap<>();
        ERS4S.put("store_code","ERS4S");
        ERS4S.put("vendor_id","12");
        ERS4S.put("vendor_name","Mimma Ninni");
        ERS4S.put("eventName","mimma");
        paramsMap.put("ERS4S",ERS4S);

        Map<String,Object> UEYHD = new HashMap<>();
        UEYHD.put("store_code","UEYHD");
        UEYHD.put("vendor_id","13");
        UEYHD.put("vendor_name","Di Pierro");
        UEYHD.put("eventName","diPierro");
        paramsMap.put("UEYHD",UEYHD);

        Map<String,Object> IEK7W = new HashMap<>();
        IEK7W.put("store_code","IEK7W");
        IEK7W.put("vendor_id","14");
        IEK7W.put("vendor_name","Gisa Boutique");
        IEK7W.put("eventName","gisa");
        paramsMap.put("IEK7W",IEK7W);

        Map<String,Object> WISE = new HashMap<>();
        WISE.put("store_code","WISE");
        WISE.put("vendor_id","18");
        WISE.put("vendor_name","Wise Boutique");
        WISE.put("eventName","wise");
        paramsMap.put("WISE",WISE);
    }
}