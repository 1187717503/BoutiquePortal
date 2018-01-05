package com.intramirror.product.api.service.promotion;

import com.intramirror.product.api.model.PromotionExcludeProduct;
import java.util.List;

/**
 * Created by dingyifan on 2018/1/5.
 */
public interface IPromotionExcludeProductService {

    List<PromotionExcludeProduct> selectByParameter(PromotionExcludeProduct promotionExcludeProduct);

    Long deletePromotionExcludeProduct(Long promotionExcludeProductId);

    Long insertPromotionExcludeProduct(PromotionExcludeProduct promotionExcludeProduct);
}
