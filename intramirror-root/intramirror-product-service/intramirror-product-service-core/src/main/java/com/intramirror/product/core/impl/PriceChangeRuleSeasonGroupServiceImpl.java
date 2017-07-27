package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.PriceChangeRuleSeasonGroup;
import com.intramirror.product.api.service.IPriceChangeRuleSeasonGroupService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleSeasonGroupMapper;

import java.util.List;

@Service
public class PriceChangeRuleSeasonGroupServiceImpl extends BaseDao implements IPriceChangeRuleSeasonGroupService {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleSeasonGroupServiceImpl.class);

    private PriceChangeRuleSeasonGroupMapper priceChangeRuleSeasonGroupMapper;

    @Override
    public int deleteByPrimaryKey(Integer priceChangeRuleSeasonGroupId) {

        return priceChangeRuleSeasonGroupMapper.deleteByPrimaryKey(priceChangeRuleSeasonGroupId);
    }

    @Override
    public int insert(PriceChangeRuleSeasonGroup record) {

        return priceChangeRuleSeasonGroupMapper.insert(record);
    }

    @Override
    public int insertSelective(PriceChangeRuleSeasonGroup record) {

        return priceChangeRuleSeasonGroupMapper.insertSelective(record);
    }

    @Override
    public PriceChangeRuleSeasonGroup selectByPrimaryKey(
            Integer priceChangeRuleSeasonGroupId) {

        return priceChangeRuleSeasonGroupMapper.selectByPrimaryKey(priceChangeRuleSeasonGroupId);
    }

    @Override
    public int updateByPrimaryKeySelective(PriceChangeRuleSeasonGroup record) {

        return priceChangeRuleSeasonGroupMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PriceChangeRuleSeasonGroup record) {

        return priceChangeRuleSeasonGroupMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<PriceChangeRuleSeasonGroup> getPriceChangeRuleGroupListByPriceChangeRuleIdAndSeasonCode(Long priceChangeRuleId, String seasonCode) {
        return priceChangeRuleSeasonGroupMapper.getPriceChangeRuleGroupListByPriceChangeRuleIdAndSeasonCode(priceChangeRuleId, seasonCode);
    }

    @Override
    public void init() {
        priceChangeRuleSeasonGroupMapper = this.getSqlSession().getMapper(PriceChangeRuleSeasonGroupMapper.class);
    }

	@Override
	public int deleteByPriceChangeRuleId(Long priceChangeRuleId) {
		
		return priceChangeRuleSeasonGroupMapper.deleteByPriceChangeRuleId(priceChangeRuleId);
	}

}
