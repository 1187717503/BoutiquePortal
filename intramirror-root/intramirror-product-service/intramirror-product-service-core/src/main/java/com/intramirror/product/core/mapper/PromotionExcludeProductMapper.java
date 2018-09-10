package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PromotionExcludeProduct;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2018/1/5.
 */
public interface PromotionExcludeProductMapper {

    List<Map<String, Object>> selectByParameter(PromotionExcludeProduct promotionExcludeProduct);

    Long deletePromotionExcludeProduct(Map<String, Object> params);

    Long insertPromotionExcludeProduct(PromotionExcludeProduct promotionExcludeProduct);

    List<Long> getPromotionProductIdByParameter(Map<String, Object> params);

    Long updatePromotionSaveTimesExcludeProduct(Map<String, Object> params);
}
