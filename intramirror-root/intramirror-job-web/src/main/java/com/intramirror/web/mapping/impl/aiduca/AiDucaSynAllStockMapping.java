package com.intramirror.web.mapping.impl.aiduca;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.util.SpringContextUtils;
import pk.shoplus.util.ExceptionUtils;


/**
 * @author wzh
 * @date 2017/9/06
 */
@Service
public class AiDucaSynAllStockMapping implements IStockMapping{

    private static Logger logger = Logger.getLogger(AiDucaSynAllStockMapping.class);
    
    @Autowired
    private SkuService skuService;


	@Override
	public StockOption mapping(Map<String, Object> mqDataMap) {
        logger.info("AiDucaSynAllStockMappingMapping,inputParams,mqDataMap:"+mqDataMap);
        StockOption stockOption = new StockOption();
        try {
        //如果未注入进来,手动获取bean
        if(skuService == null){
        	skuService = (SkuService)SpringContextUtils.getContext().getBean("skuServiceImpl");
        }
        
        Map<String,Object> productMap = JSONObject.parseObject(mqDataMap.get("product").toString());
        
        Map<String, Object> skuParam = new HashMap<String, Object>();
        skuParam.put("vendorId", mqDataMap.get("vendor_id"));
        skuParam.put("skuCode", productMap.get("SKU").toString());
        String vendor_id  = mqDataMap.get("vendor_id")==null?"":mqDataMap.get("vendor_id").toString();


        //skuCode不能为# 否则会查询出多条数据  导致库存修改出错
        if("#".equals(productMap.get("SKU").toString())){
            logger.info("job mapping AiDuca 根据skuCode修改库存  获取 product时 skuCode不能为# 否则会查询出多条数据  导致库存修改出错 参数: "+ new Gson().toJson(skuParam));
            stockOption.setVendor_id(vendor_id);
            return stockOption;
        }
        
        logger.info("job mapping AiDuca  根据skuCode 获取 product 相关信息       入参:"+new Gson().toJson(skuParam) );
        Map<String, Object> skuInfo = skuService.getSkuInfoBySkuCode(skuParam);
        logger.info("job mapping AiDuca  根据skuCode 获取 product 相关信息       结果:"+ new Gson().toJson(skuParam));
        
        //为空校验
        if(skuInfo == null || skuInfo.size() == 0) {
            logger.info("job mapping AiDuca 根据skuCode 获取 product 结果为空  参数: "+ new Gson().toJson(skuParam));
            stockOption.setVendor_id(vendor_id);
            return stockOption;
        }
        
        //设置库存数量
        if (productMap != null && productMap.size() > 0 && checkValue(productMap.get("Stock"))){
            stockOption.setQuantity(productMap.get("Stock").toString());
        }

        stockOption.setProductCode(skuInfo.get("product_code").toString());
        stockOption.setSizeValue(skuInfo.get("size")==null?"":skuInfo.get("size").toString());
        stockOption.setVendor_id(vendor_id);
        stockOption.setType(StockContants.absolute_qty); // 库存绝对值
        stockOption.setLast_check(new Date());
        	
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AiDucaSynAllStockMappingMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AiDucaSynAllStockMappingMapping,outputParams,stockOption:"+mqDataMap);
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
