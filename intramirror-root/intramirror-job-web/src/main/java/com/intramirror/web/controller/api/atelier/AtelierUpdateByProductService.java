package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ResultHelper;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

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

    public String all_update_product = "product_all_update";

    public String update_product = "product_delta_update";

    public String create_product = "product_delta_create";

    // thread num
    private static final int threadNum = 60;

    public Map<String,Object> updateProduct(String body,String storeID,String version,String type){
        long start = System.currentTimeMillis();
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
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
            logger.info("AtelierUpdateByStockControllerExecute,getBaseData,paramMap:"+JSONObject.toJSONString(paramMap));
            String vendor_id = paramMap.get("vendor_id").toString();
            String eventName = paramMap.get("eventName").toString();
            ApiDataFileUtils fileUtils = new ApiDataFileUtils(eventName,type);
            fileUtils.bakPendingFile(boutique_id+"storeCode"+storeID+"vension"+version,body);

            // mapping
            Map<String,Object> bodyDataMap = new HashMap<>();
            bodyDataMap.put("Data",jsonObjectBody);
            bodyDataMap.put("vendor_id",vendor_id);
            logger.info("AtelierUpdateByProductServiceUpdateProduct,mapping,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",eventName:"+eventName);
            ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(bodyDataMap);
            logger.info("AtelierUpdateByProductServiceUpdateProduct,mapping,productOptions:"+JSONObject.toJSONString(productOptions)+",bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",eventName:"+eventName);

            ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
            vendorOptions.setVendorId(Long.parseLong(vendor_id));

            long start_execute = System.currentTimeMillis();
            logger.info("AtelierUpdateByProductServiceUpdateProduct,executeThreadTool,eventName:"+eventName+",productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",fileUtils:"+JSONObject.toJSONString(fileUtils)+",storeCode:"+storeID);
            CommonThreadPool.execute(eventName,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,bodyDataMap));
            logger.info("AtelierUpdateByProductServiceUpdateProduct,executeThreadTool,eventName:"+eventName+",productOptions:"+JSONObject.toJSONString(productOptions)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions)+",fileUtils:"+JSONObject.toJSONString(fileUtils)+",storeCode:"+storeID);
            long end_execute = System.currentTimeMillis();
            logger.info("Job_Run_Time,AtelierUpdateByProductService_UpdateProductThread,start:"+start_execute+",end:"+end_execute+",time:"+(end_execute-start_execute));

            mapUtils.putData("ResponseStatus","1000");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByProductServiceUpdateProduct,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return mapUtils.putData("ResponseStatus","2000")
                    .putData("ErrorCode","1")
                    .putData("ErrorMsg","500: E500: failed:"+ExceptionUtils.getExceptionDetail(e))
                    .putData("TimeStamp",new Date()).getMap();
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,AtelierUpdateByProductService_updateProduct,start:"+start+",end:"+end+",time:"+(end-start));
        return mapUtils.getMap();
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        paramsMap = new HashMap<>();
        ThreadPoolExecutor executor_8 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_10 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_11 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_12 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_13 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_14 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_18 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_26 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_28 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_31 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_25 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_23 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_29 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_27 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor executor_33 =(ThreadPoolExecutor) Executors.newCachedThreadPool();

        Map<String,Object> X4ZMP = new HashMap<>();
        X4ZMP.put("store_code","X4ZMP");
        X4ZMP.put("vendor_id","8");
        X4ZMP.put("vendor_name","Luciana Bari");
        X4ZMP.put("eventName","luciana");
        X4ZMP.put("executor",executor_8);
        paramsMap.put("X4ZMP",X4ZMP);

        Map<String,Object> XIW2E = new HashMap<>();
        XIW2E.put("store_code","XIW2E");
        XIW2E.put("vendor_id","10");
        XIW2E.put("vendor_name","Dante 5");
        XIW2E.put("eventName","dante");
        XIW2E.put("executor",executor_10);
        paramsMap.put("XIW2E",XIW2E);

        Map<String,Object> UIWK2 = new HashMap<>();
        UIWK2.put("store_code","UIWK2");
        UIWK2.put("vendor_id","11");
        UIWK2.put("vendor_name","I Cinque Fiori");
        UIWK2.put("eventName","iCinque");
        UIWK2.put("executor",executor_11);
        paramsMap.put("UIWK2",UIWK2);

        Map<String,Object> ERS4S = new HashMap<>();
        ERS4S.put("store_code","ERS4S");
        ERS4S.put("vendor_id","12");
        ERS4S.put("vendor_name","Mimma Ninni");
        ERS4S.put("eventName","mimma");
        ERS4S.put("executor",executor_12);
        paramsMap.put("ERS4S",ERS4S);

        Map<String,Object> UEYHD = new HashMap<>();
        UEYHD.put("store_code","UEYHD");
        UEYHD.put("vendor_id","13");
        UEYHD.put("vendor_name","Di Pierro");
        UEYHD.put("eventName","diPierro");
        UEYHD.put("executor",executor_13);
        paramsMap.put("UEYHD",UEYHD);

        Map<String,Object> IEK7W = new HashMap<>();
        IEK7W.put("store_code","IEK7W");
        IEK7W.put("vendor_id","14");
        IEK7W.put("vendor_name","Gisa Boutique");
        IEK7W.put("eventName","gisa");
        IEK7W.put("executor",executor_14);
        paramsMap.put("IEK7W",IEK7W);

        Map<String,Object> WISE = new HashMap<>();
        WISE.put("store_code","WISE");
        WISE.put("vendor_id","18");
        WISE.put("vendor_name","Wise Boutique");
        WISE.put("eventName","wise");
        WISE.put("executor",executor_18);
        paramsMap.put("WISE",WISE);

        Map<String,Object> JUL = new HashMap<>();
        JUL.put("store_code","JULIAN");
        JUL.put("vendor_id","26");
        JUL.put("vendor_name","Julian");
        JUL.put("eventName","julian");
        JUL.put("executor",executor_26);
        paramsMap.put("JULIAN",JUL);

        Map<String,Object> ANDD = new HashMap<>();
        ANDD.put("store_code","ANDD");
        ANDD.put("vendor_id","29");
        ANDD.put("vendor_name","and");
        ANDD.put("eventName","and");
        ANDD.put("executor",executor_29);
        paramsMap.put("ANDD",ANDD);

        Map<String,Object> DIVO = new HashMap<>();
        DIVO.put("store_code","DIVO");
        DIVO.put("vendor_id","28");
        DIVO.put("vendor_name","divo");
        DIVO.put("eventName","divo");
        DIVO.put("executor",executor_28);
        paramsMap.put("DIVO",DIVO);

        Map<String,Object> BAG = new HashMap<>();
        BAG.put("store_code","BAG");
        BAG.put("vendor_id","31");
        BAG.put("vendor_name","BagheeraBoutique");
        BAG.put("eventName","bagheera");
        BAG.put("executor",executor_31);
        paramsMap.put("BAG",BAG);

        Map<String,Object> GAD = new HashMap<>();
        GAD.put("store_code","GAD");
        GAD.put("vendor_id","25");
        GAD.put("vendor_name","Gaudenzi");
        GAD.put("eventName","gaudenzi");
        GAD.put("executor",executor_25);
        paramsMap.put("GAD",GAD);

        Map<String,Object> VLT = new HashMap<>();
        VLT.put("store_code","VLT");
        VLT.put("vendor_id","27");
        VLT.put("vendor_name","Valenti");
        VLT.put("eventName","valenti");
        VLT.put("executor",executor_27);
        paramsMap.put("VLT",VLT);

        Map<String,Object> SUG = new HashMap<>();
        SUG.put("store_code","SUG");
        SUG.put("vendor_id","23");
        SUG.put("vendor_name","SugarBoutique");
        SUG.put("eventName","sugar");
        SUG.put("executor",executor_23);
        paramsMap.put("SUG",SUG);

        Map<String,Object> LUG = new HashMap<>();
        LUG.put("store_code","LUG");
        LUG.put("vendor_id","33");
        LUG.put("vendor_name","Lungoliviglio");
        LUG.put("eventName","lungoliviglio");
        LUG.put("executor",executor_33);
        paramsMap.put("LUG",LUG);
    }
}
