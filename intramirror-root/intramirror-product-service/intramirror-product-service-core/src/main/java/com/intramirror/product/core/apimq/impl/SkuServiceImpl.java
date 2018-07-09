package com.intramirror.product.core.apimq.impl;

import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.product.api.model.Sku;
import com.intramirror.product.api.service.SkuService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SkuMapper;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
            List<Map<String, Object>> list = skuMapper.getSkuInfoBySkuId(shopProductSkuIds);
            Map<String, BigDecimal> prices = new HashMap<String, BigDecimal>();
            if (list.size() > 0 && Helper.checkNotNull(list.get(0).get("in_price")) && Helper.checkNotNull(list.get(0).get("sale_price"))) {

                BigDecimal inPrice = new BigDecimal(list.get(0).get("in_price").toString());
                BigDecimal price = new BigDecimal(list.get(0).get("price").toString());
                BigDecimal salePrice = new BigDecimal(list.get(0).get("sale_price").toString());
                BigDecimal imPrice = new BigDecimal(list.get(0).get("im_price").toString());

                prices.put("inPrice", inPrice);
                prices.put("price", price);
                prices.put("salePrice", salePrice);
                prices.put("imPrice", imPrice);
                return prices;
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Map<String, Object> getSkuInfoBySkuCode(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return skuMapper.getSkuInfoBySkuCode(condition);
    }

    @Override
    public List<Sku> listSkuInfoByProductId(Long productId) {
        return skuMapper.listSkuInfoByProductId(productId);
    }

    @Override
    public List<Sku> listSkuInfoByProductIds(List<Long> productIds) {
        return skuMapper.listSkuInfoByProductIds(productIds);
    }

}
