package com.intramirror.web.mapping.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.main.api.service.SkuPropertyService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;

import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.util.ExceptionUtils;

/**
 * @author yml
 * @date 2017/8/30
 */
@Service(value = "xmagSynAllStockMapping")
public class XmagSynAllStockMapping implements IStockMapping{

    private static Logger logger = Logger.getLogger(XmagSynAllStockMapping.class);
    
    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
    
    @Autowired
    private SkuPropertyService skuPropertyService;
 
    
    @Override
	public StockOption mapping(Map<String, Object> bodyDataMap) {
		 logger.info(" start XmagAllStockMapping.handleMappingAndExecute();");
    	StockOption stockOption = new StockOption();
	        try {
	        	logger.info("mqData : ===========>>>>>>" +bodyDataMap);
	            Map<String,Object> productMap = JSONObject.parseObject(bodyDataMap.get("product").toString());
	            logger.info("sizeValue param :" + new Gson().toJson(productMap.get("Barcode").toString()));
	            Map<String, Object> param = new HashMap<>();
	            param.put("skuCode", productMap.get("Barcode").toString());
	            String vendor_id = bodyDataMap.get("vendor_id")==null?"0":
		            bodyDataMap.get("vendor_id").toString().toString();
	            param.put("vendor_id", vendor_id);
	            logger.info("sizeValue param :" + new Gson().toJson(param));
	            Map<String, Object> sizeValue = skuPropertyService.getSizeValue(param);
	            logger.info("sizeValue result :" + new Gson().toJson(sizeValue));
	            
	            ProductStockEDSManagement.StockOptions stockOptions = this.handleMappingData1(productMap,sizeValue);

	            if(StringUtils.isBlank(stockOptions.getQuantity())) {
	                return stockOption;
	            }
	            stockOptions.setVendor_id(vendor_id);
                stockOption.setType(StockContants.absolute_qty); // 库存绝对值
				stockOption.setLast_check(new Date());
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.info("XmagAllStockMapping error message : " + ExceptionUtils.getExceptionDetail(e));
	        }
	        logger.info(" end XmagAllStockMapping.handleMappingAndExecute();");
	        return stockOption;
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
