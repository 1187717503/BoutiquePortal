package com.intramirror.web.thread;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
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
public class UpdateProductThread implements Runnable{

    private static final Logger logger = Logger.getLogger(UpdateProductThread.class);

    private static final IProductService productServie = new ProductServiceImpl();

    private static final ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private ProductEDSManagement.ProductOptions productOptions;

    private ProductEDSManagement.VendorOptions vendorOptions;

    private ApiDataFileUtils apiDataFileUtils;

    @Override
    public void run() {
        try {
            logger.info("UpdateProductThreadRun,createProduct,start,productOptions:"+new Gson().toJson(productOptions)
                    +",vendorOptions:"+JSONObject.toJSONString(vendorOptions));
            Map<String,Object> resultMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("UpdateProductThreadRun,createProduct,end,resultMap:"+ JSONObject.toJSONString(resultMap)+",productOptions:"+JSONObject.toJSONString(productOptions)
                    +",vendorOptions:"+JSONObject.toJSONString(vendorOptions));

            if(resultMap != null && resultMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {
                logger.info("UpdateProductThreadRun,updateProduct,start,productOptions:" + JSONObject.toJSONString(productOptions)
                        + ",vendorOptions:" + new Gson().toJson(vendorOptions));
                resultMap = productServie.updateProduct(productOptions, vendorOptions);
                logger.info("UpdateProductThreadRun,updateProduct,end,resultMap:" + JSONObject.toJSONString(resultMap) + ",productOptions:" + JSONObject.toJSONString(productOptions)
                        + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));
            }

            if( !resultMap.get("status").toString().equals("1")) {
                logger.info("UpdateProductThreadRun,FAILUREMessage:"+JSONObject.toJSONString(resultMap)+ ",productOptions:" + JSONObject.toJSONString(productOptions)
                        + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));

                Map<String,Object> map = new HashMap<>();
                map.put("productOptions",productOptions);
                map.put("vendorOptions",vendorOptions);
                map.put("resultMap",resultMap);
                apiDataFileUtils.bakErrorFile(resultMap.get("error_enum").toString(),JSONObject.toJSONString(map));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateProductThreadRun,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",productOptions:"+JSONObject.toJSONString(productOptions)
                    +",vendorOptions:"+JSONObject.toJSONString(vendorOptions));
        }
    }

    public UpdateProductThread(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions,ApiDataFileUtils apiDataFileUtils) {
        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;
        this.apiDataFileUtils = apiDataFileUtils;
    }
}
