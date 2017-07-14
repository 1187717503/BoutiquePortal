package com.intramirror.product.core.apimq.impl;


import com.google.gson.Gson;
import com.intramirror.product.api.service.ProductPropertyService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductPropertyMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品属性服务
 */
@Service
public class ProductPropertyServiceImpl extends BaseDao implements ProductPropertyService {

    private static Logger logger = LoggerFactory.getLogger(ProductPropertyServiceImpl.class);

    private ProductPropertyMapper productPropertyMapper;

    public void init() {
        productPropertyMapper = this.getSqlSession().getMapper(ProductPropertyMapper.class);
    }

    public List<Map<String, Object>> getProductPropertyByBrandIDAndColorCode(String brandID, String colorCode) {
        List<Map<String, Object>> result = productPropertyMapper.getProductPropertyByBrandIDAndColorCode(brandID, colorCode);
        logger.info("result:{}", new Gson().toJson(result));
        return result;
    }

	public List<Map<String, Object>> getProductPropertyListByProductId(String productIds) {
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("productIds", productIds.split(","));
    	
        List<Map<String, Object>> result = productPropertyMapper.selectByProductId(param);
        logger.info("result:{}", new Gson().toJson(result));
        return result;
	}
}
