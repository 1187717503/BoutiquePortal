package com.intramirror.web.controller.api.eds;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.Result;
import pk.shoplus.service.request.impl.GetPostRequestService;
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
 * Created by dingyifan on 2017/9/14.
 */
@Controller
@RequestMapping(value = "/eds_stock")
public class EdsUpdateByStockController implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(EdsUpdateByStockController.class);

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    // mapping
    @Resource(name = "edsUpdateByStockMapping")
    private IStockMapping iStockMapping;

    // init params
    private Map<String,Object> paramsMap;

    @RequestMapping("/syn_stock")
    @ResponseBody
    public Map<String,Object> execute(@Param(value = "name")String name){
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        logger.info("EdsAllUpdateByStockControllerExecute,startExecute,name:"+name);

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status", StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }

        // get params
        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        String url = param.get("url").toString();
        String store_code = param.get("store_code").toString();
        String vendor_id = param.get("vendor_id").toString();
        int limit = Integer.parseInt(param.get("limit").toString());
        int offset = Integer.parseInt(param.get("offset").toString());
        int threadNum = Integer.parseInt(param.get("threadNum").toString());
        String eventName = param.get("eventName").toString();
        ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("nugnesExecutor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

        try {
            int sum = 0;
            while (true) {
                // 拼接URL
                String appendUrl = url + "?storeCode=" + store_code + "&limit=" + limit +"&offset=" + offset;

                // 获取数据
                logger.info("EdsAllUpdateByStockControllerExecute,startRequestMethod,appendUrl:"+appendUrl);
                String responseData = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET,appendUrl,null);
                if(StringUtils.isBlank(responseData)) {
                    logger.info("EdsAllUpdateByStockControllerExecute,whileEnd,responseData is null,appendUrl:"+appendUrl);
                    break;
                }
                logger.info("EdsAllUpdateByStockControllerExecute,endRequestMethod,appendUrl:"+appendUrl+",responseData:"+responseData);

                // 解析数据
                Map<String, Result> map = (Map<String, Result>) JSONObject.parse(responseData);
                Map<String, Object> mapResult = (Map<String, Object>) map.get("results");
                List<Map> stockMapList = JSON.parseArray(mapResult.get("items").toString(), Map.class);
                if(stockMapList == null || stockMapList.size() == 0) {
                    logger.info("EdsAllUpdateByStockControllerExecute,whileEnd,edsProductList is null,appendUrl:"+appendUrl);
                    break;
                } else {
                    fileUtils.bakPendingFile("offset"+offset+"limit"+limit,responseData);
                }

                for(Map stockMap : stockMapList){
                    sum++;
                    logger.info("EdsAllUpdateByStockControllerExecute,stockMap:"+new Gson().toJson(stockMap)+",eventName:"+eventName);
                    stockMap.put("vendor_id",vendor_id);

                    // 映射数据 封装VO
                    logger.info("EdsAllUpdateByStockControllerExecute,mapping,start,stockMap:"+new Gson().toJson(stockMap)+",eventName:"+eventName);
                    StockOption stockOption = iStockMapping.mapping(stockMap);
                    logger.info("EdsAllUpdateByStockControllerExecute,mapping,end,stockMap:"+new Gson().toJson(stockMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);

                    // 线程池
                    logger.info("EdsAllUpdateByStockControllerExecute,execute,startDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(stockMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                    CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateStockThread(stockOption,fileUtils));
                    logger.info("EdsAllUpdateByStockControllerExecute,execute,endDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(stockMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                }
                offset = offset + limit;
            }
            logger.info("EdsAllUpdateByStockControllerExecute,executeEnd,offset:"+offset+",limit:"+limit+",url:"+url+",store_code:"+store_code+",sum:"+sum+",param:"+JSONObject.toJSONString(param)+",eventName:"+eventName);
            mapUtils.putData("status",StatusType.SUCCESS).putData("info","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("EdsAllUpdateByStockControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status",StatusType.FAILURE).putData("info",ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("EdsAllUpdateByStockControllerExecute,endExecute,mapUtils:"+new Gson().toJson(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nugnes
        ThreadPoolExecutor nugnesExecutor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> object = new HashMap<>();
        object.put("url","http://nugnes.edstema.it/api/v3.0/skus/stock");
        object.put("vendor_id","9");
        object.put("store_code","X3ZMV");
        object.put("limit","500");
        object.put("offset","0");
        object.put("threadNum","5");
        object.put("nugnesExecutor",nugnesExecutor);
        object.put("eventName","stock_all_update");
        object.put("fileUtils",new ApiDataFileUtils("nugnes","stock_all_update"));

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("nugnes_all_updatestock",object);
    }
}
