package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleGroupMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
	public List<PriceChangeRuleGroup> getPriceChangeRuleGroupListByPriceChangeRuleId(Long priceChangeRuleId) {
		return priceChangeRuleGroupMapper.getPriceChangeRuleGroupListByPriceChangeRuleId(priceChangeRuleId);
	}

	@Override
	public void init() {
		priceChangeRuleGroupMapper = this.getSqlSession().getMapper(PriceChangeRuleGroupMapper.class);
		
	}

	@Override
	public int deleteByPriceChangeRuleId(Long priceChangeRuleId) {
		
		return priceChangeRuleGroupMapper.deleteByPriceChangeRuleId(priceChangeRuleId);
	}

	@Override
	public List<PriceChangeRuleGroup> selectByPriceChangeRuleId(
			Long priceChangeRuleId) {
		
		return priceChangeRuleGroupMapper.selectByPriceChangeRuleId(priceChangeRuleId);
	}

	@Override
	public List<PriceChangeRuleGroup> selectByParameter(
			PriceChangeRuleGroup record) {
		
		return priceChangeRuleGroupMapper.selectByParameter(record);
	}

}
