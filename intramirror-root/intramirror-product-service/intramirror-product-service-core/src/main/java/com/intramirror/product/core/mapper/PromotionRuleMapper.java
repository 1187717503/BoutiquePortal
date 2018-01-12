package com.intramirror.product.core.mapper;

import com.intramirror.product.api.entity.promotion.SortPromotion;
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

    List<Map<String, Object>> listIncludeRulePromotion(Long promotionId);

    Long insertIncludeRule(PromotionRule rule);

    Long insertExcludeRule(PromotionRule rule);

    Long insertIncludeRuleDetail(PromotionInclude promotionInclude);

    Long insertExcludeRuleDetail(PromotionExclude promotionExclude);

    int removeIncludeRule(Long ruleId);

    int removeExcludeRule(Long ruleId);

    int removeIncludeRuleDetail(Long ruleId);

    int removeExcludeRuleDetail(Long ruleId);

    int updateIncludeRule(PromotionRule rule);

    int updateExcludeRule(PromotionRule rule);

    List<Map<String, Object>> listSortColumn(Long promotionId);

    int updateSortColumn(SortPromotion sortPromotion);

    List<Map<String, Object>> getBrandSortItem(Long promotionId);

    List<Map<String, Object>> getVendorSortItem(Long promotionId);

    List<Map<String, Object>> getCategorySortItem(Long promotionId);

    List<Map<String, Object>> getSimpleSort(SortPromotion sortPromotion);
}

