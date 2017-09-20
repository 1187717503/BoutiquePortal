package com.intramirror.web.mapping.impl.aiduca;

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
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.api.service.category.ICategoryService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.util.SpringContextUtils;

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
 * @author wzh
 * @date 2017/9/06
 */
@Service
public class AiDucaSynAllStockMapping implements IStockMapping{

    private static Logger logger = Logger.getLogger(AiDucaSynAllStockMapping.class);
    
    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
    
    @Autowired
    private SkuService skuService;


	@Override
	public StockOption mapping(Map<String, Object> mqDataMap) {
        logger.info("job mapping AiDuca start mapping.handleMappingAndExecute 入参:"+mqDataMap);
        
        //如果未注入进来,手动获取bean
        if(skuService == null){
        	skuService = (SkuService)SpringContextUtils.getContext().getBean("skuServiceImpl");
        }
        
        StockOption stockOption = new StockOption();

        Map<String,Object> productMap = JSONObject.parseObject(mqDataMap.get("product").toString());
        
        Map<String, Object> skuParam = new HashMap<String, Object>();
        skuParam.put("vendorId", mqDataMap.get("vendor_id"));
        skuParam.put("boutiqueSkuId", productMap.get("SKU").toString());
        
        logger.info("job mapping AiDuca  根据vendorId boutiqueSkuId获取 product 相关信息       入参:"+ new Gson().toJson(skuParam));
        Map<String,Object> skuInfo = null;//skuService.getSkuInfoByBoutiqueSkuId(skuParam);
        logger.info("job mapping AiDuca  根据vendorId boutiqueSkuId获取 product 相关信息       结果:"+ new Gson().toJson(skuInfo));
        
        //为空校验
        if(skuInfo == null) {
            logger.info("job mapping AiDuca 根据vendorId boutiqueSkuId获取 product 结果为空  参数: "+ new Gson().toJson(skuParam));
            return stockOption;
        }
        
        try {
        	
        	//设置库存数量
        	if (productMap != null && productMap.size() > 0 && checkValue(productMap.get("Stock"))){
        		stockOption.setQuantity(productMap.get("Stock").toString());
        	}
        	
        	//设置size product_code
        	if (skuInfo != null && skuInfo.size() > 0 ){
        		
	        	if (checkValue(skuInfo.get("product_code"))){
	        		stockOption.setProductCode(skuInfo.get("product_code").toString());
	        	}
	        	if (checkValue(skuInfo.get("size"))){
	        		stockOption.setSizeValue(skuInfo.get("size").toString());
	        	}
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return stockOption;
	}
	
	
    
    /**
     * 参数为空校验
     * @param val
     * @return
     */
    private boolean checkValue(Object val) {
    	if(null != val && StringUtils.isNotBlank(val.toString())){
    		return true;
    	}
    	return false;
	}
    
    
}
