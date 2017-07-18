package com.intramirror.product.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.ISkuStoreService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleCategoryBrandMapper;
import com.intramirror.product.core.mapper.SkuStoreMapper;


@Service
public class PriceChangeRuleCategoryBrandServiceImpl extends BaseDao implements IPriceChangeRuleCategoryBrandService{
	
    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleCategoryBrandServiceImpl.class);

    private PriceChangeRuleCategoryBrandMapper priceChangeRuleCategoryBrandMapper;

    public void init() {
    	priceChangeRuleCategoryBrandMapper = this.getSqlSession().getMapper(PriceChangeRuleCategoryBrandMapper.class);
    }

	@Override
	public Long createPriceChangeRuleCategoryBrand(
			PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand) {
		return priceChangeRuleCategoryBrandMapper.insertSelective(priceChangeRuleCategoryBrand);
	}

	@Override
	public int deletePriceChangeRuleCategoryBrand(
			Long priceChangeRuleCategoryBrandId) {
		return priceChangeRuleCategoryBrandMapper.deleteByPrimaryKey(priceChangeRuleCategoryBrandId);
	}

}
