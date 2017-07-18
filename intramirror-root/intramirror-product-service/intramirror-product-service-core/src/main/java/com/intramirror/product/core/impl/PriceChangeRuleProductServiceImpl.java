package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intramirror.product.api.model.PriceChangeRuleProduct;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleCategoryBrandMapper;
import com.intramirror.product.core.mapper.PriceChangeRuleProductMapper;

public class PriceChangeRuleProductServiceImpl extends BaseDao implements IPriceChangeRuleProductService{

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleProductServiceImpl.class);

    private PriceChangeRuleProductMapper priceChangeRuleProductMapper;
    
	@Override
	public int deleteByPrimaryKey(Long priceChangeRuleProductId) {
		return priceChangeRuleProductMapper.deleteByPrimaryKey(priceChangeRuleProductId);
	}

	@Override
	public int insert(PriceChangeRuleProduct record) {
		return priceChangeRuleProductMapper.insert(record);
	}

	@Override
	public int insertSelective(PriceChangeRuleProduct record) {
		return priceChangeRuleProductMapper.insertSelective(record);
	}

	@Override
	public void init() {
		priceChangeRuleProductMapper = this.getSqlSession().getMapper(PriceChangeRuleProductMapper.class);
		
	}

}