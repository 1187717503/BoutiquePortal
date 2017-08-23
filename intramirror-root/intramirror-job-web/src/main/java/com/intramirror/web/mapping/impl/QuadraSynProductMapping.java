package com.intramirror.web.mapping.impl;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;

import difflib.DiffRow;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.FileUtil;
import pk.shoplus.util.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wzh on 2017/8/23.
 */
public class QuadraSynProductMapping implements IMapping{

    private static Logger logger = Logger.getLogger(QuadraSynProductMapping.class);

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private IProductService iProductService = new ProductServiceImpl();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info(" start QuadraSynProductMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            ProductEDSManagement.VendorOptions vendorOptions = new Gson().fromJson(mqDataMap.get("vendorOptions").toString(), ProductEDSManagement.VendorOptions.class);
            String propertyValue = mqDataMap.get("product").toString();
            ProductEDSManagement.ProductOptions productOptions = this.handleMappingData(propertyValue,vendorOptions);

            logger.info("Quadra开始调用商品创建Service by Quadra,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
            Map<String,Object> serviceProductMap = productEDSManagement.createProduct(productOptions,vendorOptions);
            logger.info("Quadra结束调用商品创建Service by Quadra,serviceProductMap:" + new Gson().toJson(serviceProductMap));

            if(serviceProductMap != null && serviceProductMap.get("status").equals(StatusType.PRODUCT_ALREADY_EXISTS)) {

                logger.info("Quadra开始调用商品修改Service by Quadra,productOptions:" + new Gson().toJson(productOptions) + " , vendorOptions:" + new Gson().toJson(vendorOptions));
                serviceProductMap = iProductService.updateProduct(productOptions,vendorOptions);
                logger.info("Quadra结束调用商品修改Service by Quadra,serviceProductMap:" + new Gson().toJson(serviceProductMap));
            }

            return  serviceProductMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("status", StatusType.FAILURE).putData("info","FilippoSynProductMapping error message : " + e.getMessage());
        }
        logger.info(" end FilippoSynProductMapping.handleMappingAndExecute();");
        return mapUtils.getMap();
    }

    public ProductEDSManagement.ProductOptions handleMappingData(String propertyValue, ProductEDSManagement.VendorOptions vendorOptions) throws Exception{

        if(StringUtils.isNotBlank(propertyValue) && propertyValue.contains("newLine")) {
            DiffRow diffRow = new Gson().fromJson(propertyValue, DiffRow.class);
            propertyValue = diffRow.getNewLine();
        }

        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
        Connection conn = null;
        try {
            logger.info(" productOptions filippo : " + new Gson().toJson(productOptions));
        } catch (Exception e) {
            if(conn != null) {conn.close();}
            e.printStackTrace();
            throw e;
        } finally {
            if(conn != null) {conn.close();}
        }
        return productOptions;
    }

}
