package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleCategoryBrandMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PriceChangeRuleCategoryBrandServiceImpl extends BaseDao implements IPriceChangeRuleCategoryBrandService {

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

    @Override
    public PriceChangeRuleCategoryBrand selectByPrimaryKey(
            Long priceChangeRuleCategoryBrandId) {
        return priceChangeRuleCategoryBrandMapper.selectByPrimaryKey(priceChangeRuleCategoryBrandId);
    }

    @Override
    public int updateByPrimaryKeySelective(PriceChangeRuleCategoryBrand record) {
        return priceChangeRuleCategoryBrandMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateDiscountPercentageBySelective(
            PriceChangeRuleCategoryBrand record) {
        return priceChangeRuleCategoryBrandMapper.updateDiscountPercentageBySelective(record);
    }

    @Override
    public List<PriceChangeRuleCategoryBrand> getPriceChangeRuleGroupListByPriceChangeRuleIdAndExceptionFlag(Long priceChangeRuleId, Integer exceptionFlag) {
        return priceChangeRuleCategoryBrandMapper.getPriceChangeRuleGroupListByPriceChangeRuleIdAndExceptionFlag(priceChangeRuleId, exceptionFlag);
    }

	@Override
	public int deleteByParameter(PriceChangeRuleCategoryBrand record) {
		return priceChangeRuleCategoryBrandMapper.deleteByParameter(record);
	}

}
