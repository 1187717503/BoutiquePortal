package com.intramirror.web.mapping.impl;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.handle_stock_rule_error;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.main.api.service.SkuPropertyService;

import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

/**
 * @author yml
 * @date 2017/8/30
 */
@Service
public class XmagAllStockMapping implements IMapping{

    private static Logger logger = Logger.getLogger(XmagAllStockMapping.class);
    
    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
    
    @Autowired
    private SkuPropertyService skuPropertyService;
    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info(" start XmagAllStockMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {
        	logger.info("mqData : ===========>>>>>>" +mqData);
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            Map<String,Object> productMap = JSONObject.parseObject(mqDataMap.get("product").toString());
            logger.info("sizeValue param :" + new Gson().toJson(productMap.get("Barcode").toString()));
            Map<String, Object> sizeValue = skuPropertyService.getSizeValue(productMap.get("Barcode").toString());
            logger.info("sizeValue result :" + new Gson().toJson(sizeValue));
            
            ProductStockEDSManagement.StockOptions stockOptions = this.handleMappingData1(productMap,sizeValue);

            if(StringUtils.isBlank(stockOptions.getQuantity())) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("key","stock").putData("value","null").putData("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null).putData("info","XmagAllStockMapping.handleMappingAndExecute() quantity is null !!!").getMap();
            } else {
                IStoreService storeService = new StoreServiceImpl();
                int qty = Integer.parseInt(stockOptions.getQuantity());
                ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qty,stockOptions.getSizeValue(),stockOptions.getProductCode());
                if(resultMessage.getStatus()) {
                    SkuStore skuStore = (SkuStore) resultMessage.getData();
                    stockOptions.setQuantity(skuStore.getStore().toString());
                    stockOptions.setReserved(skuStore.getReserved().toString());
                } else {
                    return mapUtils.putData("status",StatusType.FAILURE)
                            .putData("error_enum",handle_stock_rule_error)
                            .putData("product_code",stockOptions.getProductCode())
                            .putData("key","size")
                            .putData("value",stockOptions.getSizeValue())
                            .putData("size",stockOptions.getSizeValue())
                            .putData("info",resultMessage.getMsg()).getMap();
                }

                logger.info("Xmag开始调用filippo库存更新Service,stockOptions:"+new Gson().toJson(stockOptions));
                Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
                logger.info("Xmag结束调用filippo库存更新Service,serviceResultMap:"+new Gson().toJson(serviceResultMap)+",stockOptions:"+new Gson().toJson(stockOptions));
                serviceResultMap.put("product_code",stockOptions.getProductCode());
                serviceResultMap.put("size",sizeValue);
                return serviceResultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("key","exception");
            mapUtils.putData("value",ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("error_enum", Runtime_exception);
            mapUtils.putData("status", StatusType.FAILURE).putData("info","XmagAllStockMapping error message : " + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info(" end XmagAllStockMapping.handleMappingAndExecute();");
        return mapUtils.getMap();
    }
    
    public ProductStockEDSManagement.StockOptions handleMappingData1(Map<String, Object> map,Map<String, Object> sizeValue) throws Exception{
        ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
        try {
        	if (null != map && 0 < map.size()){
        		if (checkValue(map.get("Qty"))){
        			String Qty = map.get("Qty").toString();
        			stockOptions.setQuantity(Qty);
        		}
        	}
        	if (null != sizeValue && 0 < sizeValue.size()){
	        	if (checkValue(sizeValue.get("product_code"))){
	        		String product_code = sizeValue.get("product_code").toString();
	        		stockOptions.setProductCode(product_code);
	        	}
	        	if (checkValue(sizeValue.get("value"))){
	        		String value = sizeValue.get("value").toString();
	        		stockOptions.setSizeValue(value);
	        	}
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return stockOptions;
    } 
    
    private boolean checkValue(Object val) {
    	if(null != val && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
	}
    
    
}
