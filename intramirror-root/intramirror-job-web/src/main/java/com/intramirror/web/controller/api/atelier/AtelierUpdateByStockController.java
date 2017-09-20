package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ResultHelper;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Controller
@RequestMapping("/")
public class AtelierUpdateByStockController  implements InitializingBean {

    // logger
    private static Logger logger = Logger.getLogger(AtelierUpdateByStockController.class);

    // init params
    private Map<String,Object> paramsMap;

    // thread num
    private static final int threadNum = 10;

    // mapping
    @Resource(name = "atelierUpdateByStockMapping")
    private IStockMapping iStockMapping;

    @RequestMapping("/updateSKUStock")
    @ResponseBody
    public Map<String,Object> execute(HttpServletRequest request){
        logger.info("AtelierUpdateByStockControllerExecute,start");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());

        try{
            // 获取入参
            InputStream is = request.getInputStream();
            String body = IOUtils.toString(is, "utf-8");
            JSONObject data = JSON.parseObject(body);
            String storeID = request.getParameter("StoreID");
            String version = request.getParameter("Version");
            logger.info("AtelierUpdateByStockControllerExecute,inputParams,storeID:"+storeID+",version:"+version+",body:"+body);

            // 校验入参
            if(StringUtils.isBlank(storeID)) {
                return ResultHelper.createErrorResult("1", "E001: Parameter StoreID is mandatory");
            }
            if (version == null || !version.equals("1.0")) {return ResultHelper.createErrorResult("1", "E001: Parameter Version is mandatory");}
            if (body == null) {
                return ResultHelper.createErrorResult("30", "E030: Empty Request");
            }
            JSONArray skus = data.getJSONArray("sku");
            if (skus == null || skus.size() == 0) {
                return ResultHelper.createErrorResult("5", "E005: Invalid Request Format");
            }

            // 获取基础数据
            if(paramsMap.get(storeID) == null) {
                return ResultHelper.createErrorResult("206", "E206: StoreID non existent.storeID:"+storeID);
            }
            Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(storeID);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramsMap.get("executor");
            logger.info("AtelierUpdateByStockControllerExecute,getBaseData,paramMap:"+JSONObject.toJSONString(paramMap));
            String vendor_id = paramMap.get("vendor_id").toString();
            String eventName = paramMap.get("eventName").toString();
            ApiDataFileUtils fileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");
            fileUtils.bakPendingFile("storeCode"+storeID+"vension"+version,body);

            // mapping
            for(int i =0,len=skus.size();i<len;i++) {
                JSONObject sku = skus.getJSONObject(i);
                logger.info("AtelierUpdateByStockControllerExecute,for,sku:"+sku.toJSONString());

                Map<String,Object> bodyDataMap = new HashMap<>();
                bodyDataMap.put("vendor_id",vendor_id);
                bodyDataMap.put("type", StockContants.absolute_qty);
                bodyDataMap.put("sku",sku);

                logger.info("AtelierUpdateByStockControllerExecute,mapping,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
                StockOption stockOption = iStockMapping.mapping(bodyDataMap);
                logger.info("AtelierUpdateByStockControllerExecute,mapping,stockOption:"+JSONObject.toJSONString(stockOption));

                logger.info("AtelierUpdateByStockControllerExecute,threadTool,stockOption:"+JSONObject.toJSONString(stockOption)+",fileUtils:"+JSONObject.toJSONString(fileUtils)+",eventName:"+eventName);
                CommonThreadPool.execute(eventName,executor,threadNum,new UpdateStockThread(stockOption,fileUtils,bodyDataMap));
            }
            logger.info("AtelierUpdateByStockControllerExecute,executeEnd,storeID:"+storeID+",version:"+version+",body:"+body);
            mapUtils.putData("ResponseStatus","1000");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByStockControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            return ResultHelper.createErrorResult("206", "E206: error exception msg:"+e.getMessage());
        }

        logger.info("AtelierUpdateByStockControllerExecute,outputParams:"+ JSONObject.toJSONString(mapUtils));
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
        X4ZMP.put("eventName","luciana更新库存");
        X4ZMP.put("fileUtils",new ApiDataFileUtils("luciana","luciana更新库存"));
        paramsMap.put("X4ZMP",X4ZMP);

        Map<String,Object> XIW2E = new HashMap<>();
        XIW2E.put("store_code","XIW2E");
        XIW2E.put("vendor_id","10");
        XIW2E.put("vendor_name","Dante 5");
        XIW2E.put("eventName","dante更新库存");
        XIW2E.put("fileUtils",new ApiDataFileUtils("dante","dante更新库存"));
        paramsMap.put("XIW2E",XIW2E);

        Map<String,Object> UIWK2 = new HashMap<>();
        UIWK2.put("store_code","UIWK2");
        UIWK2.put("vendor_id","11");
        UIWK2.put("vendor_name","I Cinque Fiori");
        UIWK2.put("eventName","iCinque更新库存");
        UIWK2.put("fileUtils",new ApiDataFileUtils("iCinque","iCinque更新库存"));
        paramsMap.put("UIWK2",UIWK2);

        Map<String,Object> ERS4S = new HashMap<>();
        ERS4S.put("store_code","ERS4S");
        ERS4S.put("vendor_id","12");
        ERS4S.put("vendor_name","Mimma Ninni");
        ERS4S.put("eventName","mimma更新库存");
        ERS4S.put("fileUtils",new ApiDataFileUtils("mimma","mimma更新库存"));
        paramsMap.put("ERS4S",ERS4S);

        Map<String,Object> UEYHD = new HashMap<>();
        UEYHD.put("store_code","UEYHD");
        UEYHD.put("vendor_id","13");
        UEYHD.put("vendor_name","Di Pierro");
        UEYHD.put("eventName","diPierro更新库存");
        UEYHD.put("fileUtils",new ApiDataFileUtils("diPierro","diPierro更新库存"));
        paramsMap.put("UEYHD",UEYHD);

        Map<String,Object> IEK7W = new HashMap<>();
        IEK7W.put("store_code","IEK7W");
        IEK7W.put("vendor_id","14");
        IEK7W.put("vendor_name","Gisa Boutique");
        IEK7W.put("eventName","gisa更新库存");
        IEK7W.put("fileUtils",new ApiDataFileUtils("gisa","gisa更新库存"));
        paramsMap.put("IEK7W",IEK7W);

        Map<String,Object> WISE = new HashMap<>();
        WISE.put("store_code","WISE");
        WISE.put("vendor_id","18");
        WISE.put("vendor_name","Wise Boutique");
        WISE.put("eventName","wise更新库存");
        WISE.put("fileUtils",new ApiDataFileUtils("wise","wise更新库存"));
        paramsMap.put("WISE",WISE);
    }
}

/*
{"data":[{"store_code":"X4ZMP","api_end_point_id":4,"system":"intramirror","vendor_id":8,"vendor_name":"Luciana Bari","api_configuration_id":13,"enabled":true},{"store_code":"XIW2E","api_end_point_id":4,"system":"intramirror","vendor_id":10,"vendor_name":"Dante 5","api_configuration_id":15,"enabled":true},{"store_code":"UIWK2","api_end_point_id":4,"system":"intramirror","vendor_id":11,"vendor_name":"I Cinque Fiori","api_configuration_id":17,"enabled":true},{"store_code":"ERS4S","api_end_point_id":4,"system":"intramirror","vendor_id":12,"vendor_name":"Mimma Ninni","api_configuration_id":19,"enabled":true},{"store_code":"UEYHD","api_end_point_id":4,"system":"intramirror","vendor_id":13,"vendor_name":"Di Pierro","api_configuration_id":21,"enabled":true},{"store_code":"IEK7W","api_end_point_id":4,"system":"intramirror","vendor_id":14,"vendor_name":"Gisa Boutique","api_configuration_id":23,"enabled":true},{"store_code":"WISE","api_end_point_id":29,"system":"intramirror","vendor_id":18,"vendor_name":"Wise Boutique","api_configuration_id":38,"enabled":true}]}
select ac.api_configuration_id,v.vendor_name,ac.vendor_id,ac.store_code,ac.api_end_point_id,ac.system,ac.enabled from api_configuration ac
left join api_end_point aep on(ac.api_end_point_id = aep.api_end_point_id and aep.enabled = 1)
left join vendor v on(v.vendor_id = ac.vendor_id and v.enabled = 1)
where ac.enabled = 1  and ac.system = 'intramirror' and aep.`name` = 'updateSKUStock';
*/
