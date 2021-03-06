package com.intramirror.web.thread;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.controller.api.service.ApiCreateProductService;
import com.intramirror.web.controller.api.service.ApiUpdateProductService;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.CategoryService;
import pk.shoplus.util.ExceptionUtils;

public class UpdateProductThread implements Runnable {

    private static final Logger logger = Logger.getLogger(UpdateProductThread.class);

    private ProductEDSManagement.ProductOptions productOptions;

    private ProductEDSManagement.VendorOptions vendorOptions;

    private Object originData;

    private ApiDataFileUtils apiDataFileUtils;

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            ApiCreateProductService apiCreateProductService = new ApiCreateProductService();
            Map<String, Object> resultMap = apiCreateProductService.createProduct(productOptions, vendorOptions);
            logger.info("UpdateProductThreadRun,createProduct,end,resultMap:" + JSONObject.toJSONString(resultMap) + ",productOptions:" + JSONObject
                    .toJSONString(productOptions) + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));

            if (resultMap != null && resultMap.get("info").equals(ApiErrorTypeEnum.errorType.error_boutique_id_already_exists.getDesc())) {
                ApiUpdateProductService apiUpdateProductService = new ApiUpdateProductService();
                resultMap = apiUpdateProductService.updateProduct(productOptions, vendorOptions);
                logger.info("UpdateProductThreadRun,updateProduct,end,resultMap:" + JSONObject.toJSONString(resultMap) + ",productOptions:" + JSONObject
                        .toJSONString(productOptions) + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));
            }

            if (!resultMap.get("status").toString().equals("1")) {

                Map<String, Object> fileData = new HashMap<>();
                fileData.put("originData", originData);
                fileData.put("productOptions", productOptions);
                fileData.put("vendorOptions", vendorOptions);
                fileData.put("apiDataFileUtils", apiDataFileUtils);
                fileData.put("result", resultMap);
                fileData.put("error_type", productOptions.getError_type());

                resultMap.put("product_code", productOptions.getCode());
                resultMap.put("color_code", productOptions.getColorCode());
                resultMap.put("brand_id", productOptions.getBrandCode());
                resultMap.put("sku_size", JSONObject.toJSONString(productOptions.getSkus()));

                String fileDataContent = JSONObject.toJSONString(fileData);
                String path = apiDataFileUtils.bakErrorFile("error", fileDataContent);

                this.saveErrorMsg(fileDataContent, vendorOptions.getVendorId().toString(), apiDataFileUtils, resultMap, path);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(
                    "UpdateProductThreadRun,errorMessage:" + ExceptionUtils.getExceptionDetail(e) + ",productOptions:" + JSONObject.toJSONString(productOptions)
                            + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,UpdateProductThread_run,start:" + start + ",end:" + end + ",time:" + (end - start));
    }

    public UpdateProductThread(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions,
            ApiDataFileUtils apiDataFileUtils, Object originData) {
        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;
        this.apiDataFileUtils = apiDataFileUtils;
        this.originData = originData;
    }

    // 错误消息存库
    public static boolean saveErrorMsg(String fileDataContent, String vendor_id, ApiDataFileUtils apiDataFileUtils, Map<String, Object> resultMap,
            String fileName) {
        long start = System.currentTimeMillis();

        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            CategoryService categoryService = new CategoryService(conn);

            String vendorName = apiDataFileUtils.getVendorName();
            String evnetName = apiDataFileUtils.getEventName();
            String file_location = ApiDataFileUtils.baseUrl + "/" + vendorName + "/" + ApiDataFileUtils.error_file + "/" + evnetName;
            String product_code = resultMap.get("product_code") == null ? "" : resultMap.get("product_code").toString();
            String color_code = resultMap.get("color_code") == null ? "" : resultMap.get("color_code").toString();
            String brand_id = resultMap.get("brand_id") == null ? "" : resultMap.get("brand_id").toString();
            String key = resultMap.get("key") == null ? "" : resultMap.get("key").toString();
            String sku_size = resultMap.get("sku_size") == null ? "" : resultMap.get("sku_size").toString();
            String value = resultMap.get("value") == null ? "" : resultMap.get("value").toString();
            ApiErrorTypeEnum.errorType errorType = resultMap.get("error_enum") == null ? null : (ApiErrorTypeEnum.errorType) resultMap.get("error_enum");

            if (errorType == null) {
                List<Map<String, Object>> warningMap = (List<Map<String, Object>>) resultMap.get("warningMaps");

                for (Map<String, Object> map : warningMap) {
                    key = map.get("key") == null ? "" : map.get("key").toString();
                    value = map.get("value") == null ? "" : map.get("value").toString();
                    errorType = map.get("error_enum") == null ? null : (ApiErrorTypeEnum.errorType) map.get("error_enum");
                    insertErrorMsg(conn, categoryService, vendor_id, evnetName, file_location, errorType, fileName, product_code, sku_size, brand_id,
                            color_code, key, value);
                }
            } else {
                insertErrorMsg(conn, categoryService, vendor_id, evnetName, file_location, errorType, fileName, product_code, sku_size, brand_id, color_code,
                        key, value);
            }
            if (conn != null) {
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            return false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,UpdateProductThread_saveErrorMsg,start:" + start + ",end:" + end + ",time:" + (end - start));
        return true;
    }

    public static boolean insertErrorMsg(Connection conn, CategoryService categoryService, String vendor_id, String evnetName, String file_location,
            ApiErrorTypeEnum.errorType errorType, String fileName, String product_code, String sku_size, String brand_id, String color_code, String key,
            String value) throws Exception {
        try {
            // 获取vendor_api
            String selectVendorApiSQL =
                    "select * from vendor_api vp where vp.vendor_id = '" + vendor_id + "' and vp.`name` = '" + evnetName + "' and enabled = 1";
            List<Map<String, Object>> selectVendorApiMap = categoryService.executeSQL(selectVendorApiSQL);
            if (selectVendorApiMap == null || selectVendorApiMap.size() == 0) {
                String insertVendorApiSQL =
                        "insert into vendor_api (`vendor_id`,`name`,`file_location`,enabled)values(" + vendor_id + ",'" + evnetName + "','" + file_location
                                + "',1)";
                categoryService.updateBySQL(insertVendorApiSQL);
                selectVendorApiMap = categoryService.executeSQL(selectVendorApiSQL);
                if (selectVendorApiMap.size() > 1) {
                    if (conn != null) {
                        conn.rollback();
                        conn.close();
                    }
                    return false;
                }
            }
            String vendor_api_id = selectVendorApiMap.get(0).get("vendor_api_id").toString();

            // 获取api_error_type
            String selectApiErrorTypeSQL = "select * from api_error_type where error_type_name = '" + errorType.getCode() + "' and enabled = 1";
            List<Map<String, Object>> selectApiErrorTypeMap = categoryService.executeSQL(selectApiErrorTypeSQL);
            if (selectApiErrorTypeMap == null || selectApiErrorTypeMap.size() == 0) {
                String insertApiErrorTypeSQL =
                        "insert into api_error_type(error_type_name,error_type_desc,enabled)values('" + errorType.getCode() + "','" + errorType.getDesc()
                                + "',1)";
                categoryService.updateBySQL(insertApiErrorTypeSQL);
                selectApiErrorTypeMap = categoryService.executeSQL(selectApiErrorTypeSQL);
                if (selectApiErrorTypeMap.size() > 1) {
                    if (conn != null) {
                        conn.rollback();
                        conn.close();
                    }
                    return false;
                }
            }
            String api_error_type_id = selectApiErrorTypeMap.get(0).get("api_error_type_id").toString();

            // insert api_error_processing
            String insertApiErrorProcessingSQL = "insert into api_error_processing"
                    + "(api_mq_id,api_error_type_id,error_id,boutique_id,sku_size,brand_id,color_code,data_field,boutique_data,create_time,no_process,enabled,vendor_api_id)\n"
                    + "values('0','" + api_error_type_id + "','" + fileName + "','" + product_code + "','" + sku_size + "','" + brand_id + "','" + color_code
                    + "','" + key + "','" + pk.shoplus.util.StringUtils.escapeStr(value) + "',now(),1,1,'" + vendor_api_id + "')";

            categoryService.updateBySQL(insertApiErrorProcessingSQL);
        } catch (Exception e) {
            throw e;
        }
        return true;
    }
}
