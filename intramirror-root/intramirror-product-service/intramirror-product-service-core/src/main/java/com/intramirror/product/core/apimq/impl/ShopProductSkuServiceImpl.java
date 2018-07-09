package com.intramirror.product.core.apimq.impl;

import com.intramirror.product.api.model.ShopProductSku;
import com.intramirror.product.api.service.ShopProductSkuService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ShopProductSkuMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Map<String, Object>> getSkuIdByShopProductSkuId(String[] shopProductSkuIds) {
        return shopProductSkuMapper.getSkuIdByShopProductSkuId(shopProductSkuIds);
    }

    @Override
    public int insertSelective(ShopProductSku shopProductSku) {
        return shopProductSkuMapper.insertSelective(shopProductSku);
    }

    @Override
    public int updateByShopProductId(ShopProductSku record) {
        return shopProductSkuMapper.updateByShopProductId(record);
    }

    @Override
    public int batchDisableByShopProductIds(List<Long> shopProductIds) {
        return shopProductSkuMapper.batchDisableByShopProductIds(shopProductIds);
    }

    @Override
    public int batchInsertShopProductSku(List<ShopProductSku> shopProductSkus) {
        return shopProductSkuMapper.batchInsertShopProductSku(shopProductSkus);
    }
}
