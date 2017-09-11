package com.intramirror.product.core.apimq.impl;


import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.ShopProductSku;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ShopProductSkuMapper;
import com.intramirror.product.core.mapper.SkuMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * SKU服务
 */
@Service
public class ShopProductSkuServiceImpl extends BaseDao implements ShopProductSkuService {

    private ShopProductSkuMapper shopProductSkuMapper;

    public void init() {
        shopProductSkuMapper = this.getSqlSession().getMapper(ShopProductSkuMapper.class);
    }

    @Override
    public List<Map<String, Object>> getSkuByShopProductId(Long shopProductId) {
        return shopProductSkuMapper.getSkuByShopProductId(shopProductId);
    }

    @Override
    public ShopProductSku getShopProductSkuById(Long shopProductSkuId) {
        return shopProductSkuMapper.selectByPrimaryKey(shopProductSkuId);
    }

    @Override
    public Map<String, Object> getSkuBySkuId(Long skuId) {
         return shopProductSkuMapper.getSkuBySkuId(skuId);
    }
}
