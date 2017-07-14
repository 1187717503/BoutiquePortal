package com.intramirror.product.core.apimq.impl;


import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SkuMapper;
import org.springframework.stereotype.Service;

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
}
