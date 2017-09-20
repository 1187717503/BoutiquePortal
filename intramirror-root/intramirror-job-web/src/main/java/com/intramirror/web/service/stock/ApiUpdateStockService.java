package com.intramirror.web.service.stock;

import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.web.mapping.vo.StockOption;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.error_price_out_of_range;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.updateStock_params_is_error;

/**
 * Created by dingyifan on 2017/9/14.
 *
 */
public class ApiUpdateStockService {

    // logger
    private static final Logger logger = Logger.getLogger(ApiUpdateStockService.class);

    private static IStoreService storeService = new StoreServiceImpl();

    private static ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

    public Map<String, Object> updateStock(StockOption stockOption) {
        logger.info("ApiUpdateStockServiceUpdateStock,inputParams,stockOption:"+new Gson().toJson(stockOption));
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {

            // 校验参数
            logger.info("ApiUpdateStockServiceUpdateStock,checkedParams,start,stockOption:"+new Gson().toJson(stockOption));
            String checkMsg = this.checkedParams(stockOption);
            logger.info("ApiUpdateStockServiceUpdateStock,checkedParams,end,stockOption:"+new Gson().toJson(stockOption)+",checkMsg:"+checkMsg);

            if(StringUtils.isNotBlank(checkMsg)) {
                return mapUtils.putData("status", StatusType.FAILURE)
                        .putData("info","update stock - " + updateStock_params_is_error.getDesc())
                        .putData("key","info")
                        .putData("value",checkMsg)
                        .putData("error_enum", updateStock_params_is_error).getMap();
            }

            // 处理库存规则
            int qtyType = Integer.parseInt(stockOption.getType());
            int quantity = Integer.parseInt(stockOption.getQuantity());
            logger.info("ApiUpdateStockServiceUpdateStock,handleApiStockRule,start,stockOption:"+new Gson().toJson(stockOption));
            ResultMessage resultMessage = storeService.handleApiStockRule(qtyType,quantity,stockOption.getSizeValue(),stockOption.getProductCode(),stockOption.getVendor_id());
            logger.info("ApiUpdateStockServiceUpdateStock,handleApiStockRule,end,resultMessage:"+new Gson().toJson(resultMessage)+",stockOption:"+new Gson().toJson(stockOption));

            if(!resultMessage.getStatus()) {
                return mapUtils.putData("status", StatusType.FAILURE)
                        .putData("info","update stock - " + updateStock_params_is_error.getDesc())
                        .putData("key","info")
                        .putData("value",resultMessage.getMsg())
                        .putData("error_enum", updateStock_params_is_error).getMap();
            }

            // 修改库存
            ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
            stockOptions.setProductCode(stockOption.getProductCode());
            stockOptions.setQuantity(stockOption.getQuantity());
            stockOptions.setSizeValue(stockOption.getSizeValue());
            stockOptions.setVendor_id(stockOption.getVendor_id());
            stockOptions.setPrice(stockOption.getPrice());

            SkuStore skuStore = (SkuStore) resultMessage.getData();
            stockOptions.setQuantity(skuStore.getStore().toString());
            stockOptions.setReserved(skuStore.getReserved().toString());
            stockOptions.setConfirmed(skuStore.getConfirmed().toString());
            logger.info("ApiUpdateStockServiceUpdateStock,updateStock,start,stockOptions:"+new Gson().toJson(stockOptions));
            Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
            logger.info("ApiUpdateStockServiceUpdateStock,updateStock,start,serviceResultMap:"+new Gson().toJson(serviceResultMap)+",stockOptions:"+new Gson().toJson(stockOptions));

            return serviceResultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockServiceUpdateStock,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+new Gson().toJson(stockOption));
            mapUtils.putData("status", pk.shoplus.parameter.StatusType.FAILURE)
                    .putData("info","update stock - " + Runtime_exception.getDesc()+"error message : " + ExceptionUtils.getExceptionDetail(e))
                    .putData("key","exception")
                    .putData("value",ExceptionUtils.getExceptionDetail(e))
                    .putData("error_enum", Runtime_exception);
        }
        return mapUtils.getMap();
    }

    public String checkedParams(StockOption stockOption){
        String productCode = stockOption.getProductCode();
        String quantity =  stockOption.getQuantity();
        String vendor_id = stockOption.getVendor_id();
        String sizeValue = stockOption.getSizeValue();
        String price = stockOption.getPrice();
        String type = stockOption.getType();

        // check productCode
        if(StringUtils.isBlank(productCode)) {
            return "product code is null";
        }

        // check quantity
        if(StringUtils.isBlank(quantity)) {
            return "quantity is null";
        }

        try {
            BigDecimal bigDecimalQuantity = new BigDecimal(quantity);
            stockOption.setQuantity(bigDecimalQuantity.intValue()+"");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockServiceCheckedParams,stockErrorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+new Gson().toJson(stockOption));
            return "quantuty转换异常";
        }

        // check vendor_id
        if(StringUtils.isBlank(vendor_id)) {
            return "vendor_id is null";
        }

        // check sizeValue
        if(StringUtils.isBlank(sizeValue)) {
            return "sizeValue is null";
        }

        // check price
        if(StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bigDecimalPrice = new BigDecimal(price);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockServiceCheckedParams,priceErrorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+new Gson().toJson(stockOption));
                return "price转换异常";
            }
        }

        // check type
        if(StringUtils.isBlank(type)) {
            return "type is null";
        }
        return "";
    }
}
