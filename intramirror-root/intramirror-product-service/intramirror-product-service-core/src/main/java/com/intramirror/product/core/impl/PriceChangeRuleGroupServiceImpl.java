package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleGroupMapper;
import com.intramirror.product.core.mapper.PriceChangeRuleProductMapper;

@Service
public class PriceChangeRuleGroupServiceImpl extends BaseDao implements IPriceChangeRuleGroupService{

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleGroupServiceImpl.class);

    private PriceChangeRuleGroupMapper priceChangeRuleGroupMapper;
    
	@Override
	public int deleteByPrimaryKey(Long priceChangeRuleGroupId) {
		
		return priceChangeRuleGroupMapper.deleteByPrimaryKey(priceChangeRuleGroupId);
	}

	@Override
	public int insert(PriceChangeRuleGroup record) {
		
		return priceChangeRuleGroupMapper.insert(record);
	}

	@Override
	public int insertSelective(PriceChangeRuleGroup record) {
		
		return priceChangeRuleGroupMapper.insertSelective(record);
	}

	@Override
	public void init() {
		priceChangeRuleGroupMapper = this.getSqlSession().getMapper(PriceChangeRuleGroupMapper.class);
		
	}

}
