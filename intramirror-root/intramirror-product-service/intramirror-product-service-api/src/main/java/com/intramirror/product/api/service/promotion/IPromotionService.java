package com.intramirror.product.api.service.promotion;

import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.PromotionRule;
import java.util.List;
import java.util.Map;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public interface IPromotionService {
    List<Map<String, Object>> listPromotionByBanner(Long banner);

    List<Map<String, Object>> listExcludeRulePromotion(Long promotionId);

    List<Map<String, Object>> listIncludeRulePromotion(Long promotionId);

    PromotionRule processPromotionRule(PromotionRule rule, PromotionRuleType ruleType);

    Boolean removePromotionRule(Long ruleId, PromotionRuleType ruleType);

    Boolean updatePromotionRule(PromotionRuleType ruleType, PromotionRule rule);
}
