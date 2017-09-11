package com.intramirror.product.core.apimq.impl;


import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SkuMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SKU服务
 */
@Service
public class SkuServiceImpl extends BaseDao implements SkuService {

    private SkuMapper skuMapper;

    public void init() {
        skuMapper = this.getSqlSession().getMapper(SkuMapper.class);
    }

    public Sku getSkuBySkuCode(String skuCode) {
        return skuMapper.getSkuBySkuCodeAndEnabled(skuCode, EnabledType.USED);
    }

    @Override
    public Sku getSkuById(Long skuId) {
        return skuMapper.getSkuById(skuId);
    }

    @Override
    public Map<String, BigDecimal> getSkuInfoBySkuId(String shopProductSkuIds) {
        try {
            return skuMapper.getSkuInfoBySkuId(shopProductSkuIds);
        } catch (Exception e){
            throw e;
        }
    }
}
