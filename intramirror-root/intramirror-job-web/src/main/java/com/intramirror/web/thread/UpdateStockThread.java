package com.intramirror.web.thread;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.service.stock.ApiUpdateStockService;
import com.intramirror.web.util.ApiDataFileUtils;
import org.apache.log4j.Logger;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        try {
            logger.info("UpdateStockThreadRun,updateStock,start,stockOption:"+JSONObject.toJSONString(stockOption));
            Map<String, Object> resultMap = apiUpdateStockService.updateStock(stockOption);
            logger.info("UpdateStockThreadRun,updateStock,end,resultMap:"+JSONObject.toJSONString(resultMap)+",stockOption:"+JSONObject.toJSONString(stockOption));

            if( !resultMap.get("status").toString().equals("1")) {
                logger.info("UpdateStockThreadRun,FAILUREMessage:"+JSONObject.toJSONString(resultMap)+",stockOption:"+JSONObject.toJSONString(stockOption));

                Map<String,Object> map = new HashMap<>();
                map.put("stockOption",stockOption);
                map.put("resultMap",resultMap);
                apiDataFileUtils.bakErrorFile(resultMap.get("error_enum") ==null?"waraning":resultMap.get("error_enum").toString(),JSONObject.toJSONString(map));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateStockThreadRun,updateStock,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+new Gson().toJson(stockOption));
        }
    }

    public UpdateStockThread(StockOption stockOption,ApiDataFileUtils apiDataFileUtils,Object originData) {
        this.stockOption = stockOption;
        this.apiDataFileUtils = apiDataFileUtils;
        this.originData = originData;
    }
}
