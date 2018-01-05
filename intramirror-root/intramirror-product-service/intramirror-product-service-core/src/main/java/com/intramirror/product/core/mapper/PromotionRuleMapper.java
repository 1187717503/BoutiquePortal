package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionRule;
import java.util.List;
import java.util.Map;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public interface PromotionRuleMapper {

    List<Map<String, Object>> listPromotionByBanner(Long banner);

    List<Map<String, Object>> listExcludeRulePromotion(Long promotionId);

    List<Map<String, Object>> listIncluedRulePromotion(Long promotionId);

    Long insertIncludeRule(PromotionRule rule);

    Long insertExcludeRule(PromotionRule rule);

    Long insertIncludeRuleDetail(PromotionInclude promotionInclude);

    Long insertExcludeRuleDetail(PromotionExclude promotionExclude);

    Boolean removeIncludeRule(Long ruleId);

    Boolean removeExcludeRule(Long ruleId);

    Boolean removeIncludeRuleDetail(Long ruleId);

    Boolean removeExcludeRuleDetail(Long ruleId);

}

