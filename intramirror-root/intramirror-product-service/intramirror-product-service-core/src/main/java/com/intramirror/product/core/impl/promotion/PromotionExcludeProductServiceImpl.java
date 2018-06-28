package com.intramirror.product.core.impl.promotion;

import com.intramirror.product.api.model.PromotionExcludeProduct;
import com.intramirror.product.api.service.promotion.IPromotionExcludeProductService;
import com.intramirror.product.core.mapper.PromotionExcludeProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2018/1/5.
 */
@Service
public class PromotionExcludeProductServiceImpl implements IPromotionExcludeProductService {

    @Autowired
    private PromotionExcludeProductMapper promotionExcludeProductMapper;

    @Override
    public List<Map<String, Object>> selectByParameter(PromotionExcludeProduct promotionExcludeProduct) {
        return promotionExcludeProductMapper.selectByParameter(promotionExcludeProduct);
    }

    @Override
    public Long deletePromotionExcludeProduct(Map<String, Object> params) {
        return promotionExcludeProductMapper.deletePromotionExcludeProduct(params);
    }

    @Override
    public Long insertPromotionExcludeProduct(PromotionExcludeProduct promotionExcludeProduct) {
        return promotionExcludeProductMapper.insertPromotionExcludeProduct(promotionExcludeProduct);
    }

    @Override
    public List<Long> getPromotionProductIdByParameter(Map<String, Object> params) {
        return promotionExcludeProductMapper.getPromotionProductIdByParameter(params);
    }
}
