package com.intramirror.main.core.impl;


import com.intramirror.main.api.service.ShippingRuleService;
import com.intramirror.main.api.service.TaxService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.ShippingRuleMapper;
import com.intramirror.main.core.mapper.TaxMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class ShippingRuleServiceImpl extends BaseDao implements ShippingRuleService {

    private ShippingRuleMapper shippingRuleMapper;


    public void init() {
        shippingRuleMapper = this.getSqlSession().getMapper(ShippingRuleMapper.class);
    }


    @Override
    public List<Map<String, Object>> getShippingFeeByProductIds(String[] productIds, Integer toCountry) {
        return shippingRuleMapper.getShippingFeeByProductIds(productIds,toCountry);
    }
}
