package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionRule;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public interface PromotionRuleMapper {

    List<Map<String, Object>> listActivePromotion();

    List<Map<String, Object>> listExcludeRulePromotion(@Param(value = "promotionId") String bannerId);

    List<Map<String, Object>> listIncluedRulePromotion(@Param(value = "promotionId") String bannerId);

    Long insertIncludeRule(PromotionRule rule);

    Long insertExcludeRule(PromotionRule rule);

    Long insertIncludeRuleDetail(PromotionInclude promotionInclude);

    Long insertExcludeRuleDetail(PromotionExclude promotionExclude);
}

