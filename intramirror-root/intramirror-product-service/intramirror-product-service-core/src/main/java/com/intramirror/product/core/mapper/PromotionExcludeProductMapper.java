package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PromotionExcludeProduct;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2018/1/5.
 */
public interface PromotionExcludeProductMapper {

    List<Map<String, Object>> selectByParameter(PromotionExcludeProduct promotionExcludeProduct);

    Long deletePromotionExcludeProduct(Long promotionExcludeProductId);

    Long insertPromotionExcludeProduct(PromotionExcludeProduct promotionExcludeProduct);
}
