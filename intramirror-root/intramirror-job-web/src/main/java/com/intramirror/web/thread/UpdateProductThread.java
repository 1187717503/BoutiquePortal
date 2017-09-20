package com.intramirror.web.thread;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.model.ApiErrorProcessing;
import com.intramirror.product.api.model.ApiErrorType;
import com.intramirror.web.util.ApiDataFileUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    private Object originData;

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

                Map<String,Object> fileData = new HashMap<>();
                fileData.put("originData",originData);
                fileData.put("productOptions",productOptions);
                fileData.put("vendorOptions",vendorOptions);
                fileData.put("apiDataFileUtils",apiDataFileUtils);
                fileData.put("result",resultMap);

                resultMap.put("product_code",productOptions.getCode());
                resultMap.put("color_code",productOptions.getColorCode());
                resultMap.put("brand_id",productOptions.getBrandCode());
                resultMap.put("sku_size",new Gson().toJson(productOptions.getSkus()));

                String fileDataContent = JSONObject.toJSONString(fileData);
                logger.info("UpdateProductThreadRun,bakErrorFile,start,fileDataContent:"+fileDataContent);
                String path = apiDataFileUtils.bakErrorFile("error",fileDataContent);
                logger.info("UpdateProductThreadRun,bakErrorFile,end,path:"+path+",fileDataContent:"+fileDataContent);

                this.saveErrorMsg(fileDataContent,vendorOptions.getVendorId().toString(),apiDataFileUtils,resultMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("UpdateProductThreadRun,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",productOptions:"+JSONObject.toJSONString(productOptions)
                    +",vendorOptions:"+JSONObject.toJSONString(vendorOptions));
        }
    }

    public UpdateProductThread(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions,ApiDataFileUtils apiDataFileUtils,Object originData) {
        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;
        this.apiDataFileUtils = apiDataFileUtils;
        this.originData = originData;
    }

    // 错误消息存库
    public static boolean saveErrorMsg(String fileDataContent,String vendor_id,ApiDataFileUtils apiDataFileUtils,Map<String,Object> resultMap){
        logger.info("UpdateProductThreadSaveErrorMsg,inputParams,fileDataContent:"+fileDataContent+",vendor_id:"+vendor_id+",apiDataFileUtils:"+JSONObject.toJSONString(apiDataFileUtils)+",resultMap:"+JSONObject.toJSONString(resultMap));
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);

            String vendorName = apiDataFileUtils.getVendorName();
            String evnetName = apiDataFileUtils.getEventName();
            String fileName = apiDataFileUtils.getFileName();
            String file_location = ApiDataFileUtils.baseUrl+"/"+vendorName+"/"+ApiDataFileUtils.error_file+"/"+evnetName;
            String product_code = resultMap.get("product_code") == null ? "" : resultMap.get("product_code").toString();
            String color_code = resultMap.get("color_code") == null ? "" : resultMap.get("color_code").toString();
            String brand_id = resultMap.get("brand_id") == null ? "" : resultMap.get("brand_id").toString();
            String key = resultMap.get("key") == null ? "" : resultMap.get("key").toString();
            String sku_size = resultMap.get("sku_size") == null ? "" : resultMap.get("sku_size").toString();
            String value = resultMap.get("value") == null ? "" : resultMap.get("value").toString();
            ApiErrorTypeEnum.errorType errorType = resultMap.get("error_enum") == null ? null : (ApiErrorTypeEnum.errorType)resultMap.get("error_enum");

            // 获取vendor_api
            String selectVendorApiSQL = "select * from vendor_api vp where vp.vendor_id = '"+vendor_id+"' and vp.`name` = '"+evnetName+"' and enabled = 1";
            logger.info("UpdateProductThreadSaveErrorMsg,selectVendorApi,selectVendorApiSQL:"+selectVendorApiSQL);
            List<Map<String,Object>> selectVendorApiMap = categoryService.executeSQL(selectVendorApiSQL);
            logger.info("UpdateProductThreadSaveErrorMsg,selectVendorApi,selectVendorApiMap:"+JSONObject.toJSONString(selectVendorApiMap));
            if(selectVendorApiMap == null || selectVendorApiMap.size() ==0){
                String insertVendorApiSQL = "insert into vendor_api (`vendor_id`,`name`,`file_location`,enabled)values("+vendor_id+",'"+evnetName+"','"+file_location+"',1)";
                categoryService.updateBySQL(insertVendorApiSQL);
                selectVendorApiMap = categoryService.executeSQL(selectVendorApiSQL);
                if(selectVendorApiMap.size() > 1){
                    if(conn != null) {conn.rollback();conn.close();}
                    logger.info("UpdateProductThreadSaveErrorMsg,vendor_api already exists.");
                    return false;
                }
            }
            String vendor_api_id = selectVendorApiMap.get(0).get("vendor_api_id").toString();

            // 获取api_error_type
            String selectApiErrorTypeSQL = "select * from api_error_type where error_type_name = '"+errorType.getCode()+"' and enabled = 1";
            logger.info("UpdateProductThreadSaveErrorMsg,selectApiErrorType,selectVendorApiSQL:"+selectVendorApiSQL);
            List<Map<String,Object>> selectApiErrorTypeMap = categoryService.executeSQL(selectApiErrorTypeSQL);
            logger.info("UpdateProductThreadSaveErrorMsg,selectApiErrorType,selectApiErrorTypeMap:"+JSONObject.toJSONString(selectApiErrorTypeMap));
            if(selectApiErrorTypeMap == null || selectApiErrorTypeMap.size() == 0) {
                String insertApiErrorTypeSQL = "insert into api_error_type(error_type_name,error_type_desc,enabled)values('"+errorType.getCode()+"','"+errorType.getDesc()+"',1)";
                logger.info("UpdateProductThreadSaveErrorMsg,insertApiErrorTypeSQL:"+insertApiErrorTypeSQL);
                categoryService.updateBySQL(insertApiErrorTypeSQL);
                selectApiErrorTypeMap = categoryService.executeSQL(selectApiErrorTypeSQL);
                if(selectApiErrorTypeMap.size() > 1){
                    if(conn != null) {conn.rollback();conn.close();}
                    logger.info("UpdateProductThreadSaveErrorMsg,api_error_type already exists.");
                    return false;
                }
            }
            String api_error_type_id = selectApiErrorTypeMap.get(0).get("api_error_type_id").toString();

            // insert api_error_processing
            String insertApiErrorProcessingSQL = "insert into api_error_processing" +
                    "(api_mq_id,api_error_type_id,error_id,boutique_id,sku_size,brand_id,color_code,data_field,boutique_data,create_time,no_process,enabled,vendor_api_id)\n" +
                    "values('0','"+api_error_type_id+"','"+fileName+"','"+product_code+"','"+sku_size+"','"+brand_id+"','"+color_code+"','"+key+"','"+value+"',now(),1,1,'"+vendor_api_id+"')";

            logger.info("UpdateProductThreadSaveErrorMsg,insertApiErrorProcessing,insertApiErrorProcessingSQL:"+insertApiErrorProcessingSQL);
            categoryService.updateBySQL(insertApiErrorProcessingSQL);
            conn.commit();
        } catch (Exception e) {
            logger.info("UpdateProductThreadSaveErrorMsg,insertApiErrorProcessing,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
            e.printStackTrace();
            if(conn!=null) {conn.rollback();conn.close();}
            return false;
        } finally {
            if(conn!=null){conn.close();}
        }
        return true;
    }
}
