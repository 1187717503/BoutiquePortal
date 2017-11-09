package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.vo.StockOption;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/11/9.
 */
public class ApiStockSerivce {

    private static final Logger logger = Logger.getLogger(ApiStockSerivce.class);

    private boolean updateBySkuCode;

    public Map<String,Object> executeUpdateStock(){
        Map<String,Object> resultMap = new HashMap<>();
        Connection conn = null;
        try {
            /** 1.检查参数 */
            Map<String,Object> checkParamMap = this.executeCheckParams();
            if(checkParamMap != null) {
                return checkParamMap;
            }

            /** 2.执行库存规则 */
            conn = DBConnector.sql2o.beginTransaction();
            resultMap = this.executeStockRule(conn);
            if(conn!=null){conn.commit();conn.close();}

        } catch (Exception e) {
            if(conn!=null){conn.rollback();conn.close();}
            e.printStackTrace();
            logger.info("ApiStockSerivce,executeUpdateStock,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
            return errorMap(ApiErrorTypeEnum.errorType.Runtime_exception,"errorMessage",ExceptionUtils.getExceptionDetail(e));
        }
        return resultMap;
    }

    private Map<String,Object> executeStockRule(Connection conn) throws Exception{
        SkuStoreService skuStoreService = new SkuStoreService(conn);

        // 查询sku_store信息
        List<Map<String,Object>> skuStoreMap = null;
        if(StringUtils.isNotBlank(stockOption.getProductCode())) {
            skuStoreMap =skuStoreService.api_select_store(stockOption.getProductCode(),stockOption.getSizeValue(),stockOption.getVendor_id());
        } else {
            skuStoreMap =skuStoreService.api_select_store(stockOption.getSku_code(),stockOption.getVendor_id());
        }
        logger.info("ApiStockSerivce,executeUpdateStock,updateStockStart,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));

        if(skuStoreMap == null || skuStoreMap.size() == 0) {
            if(StringUtils.isNotBlank(stockOption.getProductCode())) {
                return errorMap(ApiErrorTypeEnum.errorType.sku_code_not_exists,"sku_code",stockOption.getSku_code());
            } else {
                // TODO CreateSKU
            }
        } else { // 处理库存规则
            int qty = Integer.parseInt(stockOption.getQuantity());
            int type = Integer.parseInt(stockOption.getType());
            int rs;
            int store = 0;
            int reserved = 0;
            int confirmed = 0;
            int sku_store_id = 0;
            store = Integer.parseInt(skuStoreMap.get(0).get("store").toString());
            reserved = Integer.parseInt(skuStoreMap.get(0).get("store").toString());
            confirmed = Integer.parseInt(skuStoreMap.get(0).get("store").toString());
            sku_store_id = Integer.parseInt(skuStoreMap.get(0).get("store").toString());

            if(Contants.STOCK_QTY == type) {
                rs = qty - (store + reserved + confirmed);
            } else {
                rs = qty;
            }

            if(confirmed == 0 && Contants.STOCK_QTY == type) {
                store = qty - reserved;
            } else {
                if(rs >= 0) {
                    store = store + rs;
                } else {
                    if(confirmed > 0) {
                        confirmed = confirmed + rs;
                        if(confirmed < 0) {
                            store = store + confirmed;
                            confirmed = 0;
                        }
                    } else {
                        store = store + rs;
                    }
                }
            }

            SkuStore skuStore = new SkuStore();
            skuStore.setUpdated_at(new Date());
            skuStore.setLast_check(new Date());
            skuStore.setSku_store_id((long)sku_store_id);
            skuStore.setStore((long)store);
            skuStore.setReserved((long)reserved);
            skuStore.setConfirmed((long)confirmed);
            skuStoreService.updateSkuStore(skuStore);
            if(conn!=null) {conn.commit();conn.close();}
            logger.info("ApiStockSerivce,executeUpdateStock,updateStockEnd,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));
        }
        return successMap();
    }

    private Map<String,Object> executeCheckParams() throws Exception{
        logger.info("ApiStockSerivce,executeCheckParams,inputParams,stockOption:"+JSONObject.toJSONString(stockOption));
        String quantity =  StringUtils.trim(stockOption.getQuantity());
        String vendorId = StringUtils.trim(stockOption.getVendor_id());
        String type =StringUtils.trim( stockOption.getType());
        String price = StringUtils.trim(stockOption.getPrice());
        String productCode = StringUtils.trim(stockOption.getProductCode());
        String skuCode = StringUtils.trim(stockOption.getSku_code());
        String sizeValue = StringUtils.trim(stockOption.getSizeValue());

        stockOption.setQuantity(quantity);
        stockOption.setVendor_id(vendorId);
        stockOption.setType(type);
        stockOption.setPrice(price);
        stockOption.setProductCode(productCode);
        stockOption.setSku_code(skuCode);
        stockOption.setSizeValue(sizeValue);

        // check quantity
        if(StringUtils.isBlank(quantity)) {
            return errorMap(ApiErrorTypeEnum.errorType.stock_is_null,"store",quantity);
        }
        try {
            int stock = Integer.parseInt(quantity);
            if(stock<0 || stock > 100) {
                return errorMap(ApiErrorTypeEnum.errorType.Data_is_negative,"store",quantity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiStockSerivce,executeCheckParams,stock not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
            return errorMap(ApiErrorTypeEnum.errorType.Data_is_not_number,"store",quantity);
        }

        // check vendorId
        if(StringUtils.isBlank(vendorId)) {
            return errorMap(ApiErrorTypeEnum.errorType.vendor_is_null,"vendor_id",vendorId);
        }

        // check type
        if(StringUtils.isBlank(type)) {
            return errorMap(ApiErrorTypeEnum.errorType.stock_type_is_null,"stock_type",type);
        }

        // check price
        if(StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bPrice = new BigDecimal(price);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiStockSerivce,executeCheckParams,price not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
                return errorMap(ApiErrorTypeEnum.errorType.Data_is_not_number,"price",price);
            }
        }

        if((StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) || (StringUtils.isNotBlank(skuCode))) {
            return null;
        } else {
            return errorMap(ApiErrorTypeEnum.errorType.updateStock_params_is_error,"stockOption",JSONObject.toJSONString(stockOption));
        }
    }

    /** 返回错误消息 */
    private Map<String,Object> errorMap(ApiErrorTypeEnum.errorType errorType,String key,String value){
        logger.info("ApiStockSerivce,errorMap,inputParams,errorType:"+errorType.getCode()+",key:"+key+",value:"+value);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", StatusType.FAILURE);
        resultMap.put("info","update stock - " + errorType.getDesc());
        resultMap.put("key",key);
        resultMap.put("value",value);
        resultMap.put("error_enum",errorType);
        logger.info("ApiStockSerivce,errorMap,outputParams,resultMap:" + JSONObject.toJSONString(resultMap));
        return resultMap;
    }

    private Map<String,Object> successMap(){
        Map<String,Object> successMap = new HashMap<>();
        successMap.put("status",StatusType.SUCCESS);
        successMap.put("info","success");
        return successMap;
    }

    private StockOption stockOption;

    public StockOption getStockOption() {
        return stockOption;
    }

    public void setStockOption(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public ApiStockSerivce(StockOption stockOption) {
        this.stockOption = stockOption;
    }
}
