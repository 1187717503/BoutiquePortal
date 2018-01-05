package com.intramirror.product.core.impl.promotion;

import com.intramirror.product.api.model.PromotionExcludeProduct;
import com.intramirror.product.api.service.promotion.IPromotionExcludeProductService;
import com.intramirror.product.core.mapper.PromotionExcludeProductMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2018/1/5.
 */
@Service
public class PromotionExcludeProductServiceImpl implements IPromotionExcludeProductService{

    @Autowired
    private PromotionExcludeProductMapper promotionExcludeProductMapper;

    @Override
    public List<PromotionExcludeProduct> selectByParameter(PromotionExcludeProduct promotionExcludeProduct) {
        return promotionExcludeProductMapper.selectByParameter(promotionExcludeProduct);
    }

    @Override
    public Long deletePromotionExcludeProduct(Long promotionExcludeProductId) {
        return promotionExcludeProductMapper.deletePromotionExcludeProduct(promotionExcludeProductId);
    }

    @Override
    public Long insertPromotionExcludeProduct(PromotionExcludeProduct promotionExcludeProduct) {
        return promotionExcludeProductMapper.insertPromotionExcludeProduct(promotionExcludeProduct);
    }
}
