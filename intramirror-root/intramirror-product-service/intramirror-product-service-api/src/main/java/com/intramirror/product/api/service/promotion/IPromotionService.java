package com.intramirror.product.api.service.promotion;

import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionIncludeRule;
import java.util.List;
import java.util.Map;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public interface IPromotionService {
    List<Map<String, Object>> listActivePromotion();

    List<Map<String, Object>> listExcludeRulePromotion(String bannerId);

    List<Map<String, Object>> listIncluedRulePromotion(String bannerId);

    Long savePromotionIncludeRule(PromotionIncludeRule rule);

    Long savePromotionInclude(PromotionInclude promotionIncludeProduct);

}
