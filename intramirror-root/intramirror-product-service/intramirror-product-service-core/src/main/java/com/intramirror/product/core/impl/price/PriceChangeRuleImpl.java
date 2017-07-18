package com.intramirror.product.core.impl.price;

import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.impl.SkuStoreServiceImpl;
import com.intramirror.product.core.mapper.PriceChangeRuleMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2017/7/17.
 */
@Service(value = "productPriceChangeRule")
public class PriceChangeRuleImpl extends BaseDao implements IPriceChangeRule {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleImpl.class);

    private PriceChangeRuleMapper priceChangeRuleMapper ;

    @Override
    public boolean updateVendorPrice() {

        priceChangeRuleMapper.updatePriceChangeRuleByVendorFirstCategory();
        priceChangeRuleMapper.updatePriceChangeRuleByVendorAllCategory();
        priceChangeRuleMapper.updatePriceChangeRuleByVendorProductGroup();
        priceChangeRuleMapper.updatePriceChangeRuleByVendorProduct();
        return true;
    }

    @Override
    public boolean updateShopPrice() {

        priceChangeRuleMapper.updatePriceChangeRuleByShopFirstCategory();
        priceChangeRuleMapper.updatePriceChangeRuleByShopAllCategory();
        priceChangeRuleMapper.updatePriceChangeRuleByShopProductGroup();
        priceChangeRuleMapper.updatePriceChangeRuleByShopProduct();
        return true;
    }

    @Override
    public boolean updateAdminPrice() {

        return false;
    }

    @Override
    public void init() {
        priceChangeRuleMapper = this.getSqlSession().getMapper(PriceChangeRuleMapper.class);
    }

	@Override
	public int deleteByPrimaryKey(Long priceChangeRuleId) {
		return priceChangeRuleMapper.deleteByPrimaryKey(priceChangeRuleId);
	}

	@Override
	public int insert(PriceChangeRule record) {
		return priceChangeRuleMapper.insert(record);
	}

	@Override
	public int insertSelective(PriceChangeRule record) {
		return priceChangeRuleMapper.insertSelective(record);
	}
}
