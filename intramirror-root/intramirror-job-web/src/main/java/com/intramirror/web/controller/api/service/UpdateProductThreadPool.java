package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.controller.execute.TimerJobHandlePendingQueueController;
import com.intramirror.web.thread.ThreadPoolTimer;
import com.intramirror.web.vo.ApiProductVo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/12.
 */
@Service(value = "updateProductThreadPool")
public class UpdateProductThreadPool {

    private static final Logger logger = Logger.getLogger(UpdateProductThreadPool.class);

    public static ThreadPoolTimer threadPoolTimer;

    public static void startThreadPool(List<ApiProductVo> productVoList) throws InterruptedException {
        logger.info("UpdateProductThreadPoolStartThreadPool,inputParams,productVoList:"+new Gson().toJson(productVoList));
        threadPoolTimer = ThreadPoolTimer.getInstance();

        for(int i = 0,len = productVoList.size();i<len;i++) {
            ProductEDSManagement.ProductOptions productOptions = productVoList.get(i).getProductOptions();
            ProductEDSManagement.VendorOptions vendorOptions = productVoList.get(i).getVendorOptions();

            logger.info("UpdateProductThreadPoolStartThreadPool,startExecuteTask,threadNum:"+len+",productOptions:"+new Gson().toJson(productOptions)
                    +",vendorOptions:"+new Gson().toJson(vendorOptions));
            threadPoolTimer.executeTask(new Consume(productOptions,vendorOptions));
        }

        logger.info("UpdateProductThreadPoolStartThreadPool,startSleep:"+ DateUtils.getStrDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        Thread.sleep(10000);
        logger.info("UpdateProductThreadPoolStartThreadPool,endSleep:"+ DateUtils.getStrDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        logger.info("UpdateProductThreadPoolStartThreadPool,outParams,productVoList:"+new Gson().toJson(productVoList));
    }
}

class Consume implements Runnable {

    private static final Logger logger = Logger.getLogger(Consume.class);

    private static final IProductService productServie = new ProductServiceImpl();

    private static final ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private ProductEDSManagement.ProductOptions productOptions;

    private ProductEDSManagement.VendorOptions vendorOptions;

    @Override
    public void run() {
        try {
            logger.info("UpdateProductThreadPoolStartThreadPoolConsumeRun,createProduct,productOptions:"+new Gson().toJson(productOptions)
                    +",vendorOptions:"+new Gson().toJson(vendorOptions));
            Map<String,Object> resultMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("UpdateProductThreadPoolStartThreadPoolConsumeRun,createProduct,resultMap:"+ JSONObject.toJSONString(resultMap)+",productOptions:"+new Gson().toJson(productOptions)
                    +",vendorOptions:"+new Gson().toJson(vendorOptions));

            if(resultMap != null && resultMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
                logger.info("UpdateProductThreadPoolStartThreadPoolConsumeRun,updateProduct,productOptions:" + new Gson().toJson(productOptions)
                        + ",vendorOptions:" + new Gson().toJson(vendorOptions));
                resultMap = productServie.updateProduct(productOptions, vendorOptions);
                logger.info("UpdateProductThreadPoolStartThreadPoolConsumeRun,updateProduct,resultMap:" + JSONObject.toJSONString(resultMap) + ",productOptions:" + new Gson().toJson(productOptions)
                        + ",vendorOptions:" + new Gson().toJson(vendorOptions));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateProductThreadPoolStartThreadPoolConsumeRun,productOptions:"+new Gson().toJson(productOptions)
                    +",vendorOptions:"+new Gson().toJson(vendorOptions));
        }
    }

    public Consume(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions) {
        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;
    }
}
