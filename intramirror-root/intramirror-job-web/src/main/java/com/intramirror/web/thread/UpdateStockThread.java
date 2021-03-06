package com.intramirror.web.thread;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.controller.api.service.ApiUpdateStockSerivce;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.service.stock.ApiUpdateStockService;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/13.
 */
public class UpdateStockThread implements Runnable{

    private static final Logger logger = Logger.getLogger(UpdateStockThread.class);

    private StockOption stockOption;

    private static final ApiUpdateStockService apiUpdateStockService = new ApiUpdateStockService();

    private ApiDataFileUtils apiDataFileUtils;

    private Object originData;

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            System.out.println(JSONObject.toJSON(apiDataFileUtils));
            logger.info("UpdateStockThreadRun,updateStock,start,stockOption:"+JSONObject.toJSONString(stockOption));
//            Map<String, Object> resultMap = apiUpdateStockService.updateStock(stockOption);
            ApiUpdateStockSerivce apiUpdateStockSerivce = new ApiUpdateStockSerivce();
            Map<String,Object> resultMap = apiUpdateStockSerivce.updateStock(stockOption);
            logger.info("UpdateStockThreadRun,updateStock,end,resultMap:"+JSONObject.toJSONString(resultMap)+",stockOption:"+JSONObject.toJSONString(stockOption));

            if( !resultMap.get("status").toString().equals("1")) {
                logger.info("UpdateStockThreadRun,FAILUREMessage:"+JSONObject.toJSONString(resultMap)+",stockOption:"+JSONObject.toJSONString(stockOption));

                Map<String,Object> map = new HashMap<>();
                map.put("stockOption",stockOption);
                map.put("resultMap",resultMap);

                Map<String,Object> fileData = new HashMap<>();
                fileData.put("originData",originData);
                fileData.put("stockOption",stockOption);
                fileData.put("apiDataFileUtils",apiDataFileUtils);
                fileData.put("result",resultMap);

                resultMap.put("product_code",stockOption.getProductCode());
                resultMap.put("sku_size",stockOption.getSizeValue());

                String fileDataContent = JSONObject.toJSONString(fileData);
                logger.info("UpdateStockThreadRun,bakErrorFile,start,fileDataContent:"+fileDataContent);
                String path = apiDataFileUtils.bakErrorFile("error",fileDataContent);
                logger.info("UpdateStockThreadRun,bakErrorFile,end,path:"+path+",fileDataContent:"+fileDataContent);

                UpdateProductThread.saveErrorMsg(fileDataContent,stockOption.getVendor_id().toString(),apiDataFileUtils,resultMap,path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateStockThreadRun,updateStock,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+new Gson().toJson(stockOption));
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,UpdateStockThread_run,start:"+start+",end:"+end+",time:"+(end-start));
    }

    public UpdateStockThread(StockOption stockOption,ApiDataFileUtils apiDataFileUtils,Object originData) {
        this.stockOption = stockOption;
        this.apiDataFileUtils = apiDataFileUtils;
        this.originData = originData;
    }
}
