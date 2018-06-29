package com.intramirror.product.api.service.promotion;

import com.intramirror.product.api.entity.promotion.SortPromotion;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.enums.SortColumn;
import com.intramirror.product.api.exception.BusinessException;
import com.intramirror.product.api.model.Promotion;
import com.intramirror.product.api.model.PromotionBrandHot;
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

    //2018-4-18 By Jian
    boolean processImportPromotionRule(Integer type, List<PromotionRule> listRule);

    Boolean removePromotionRule(List<Long> ruleIds, PromotionRuleType ruleType);

    Boolean updatePromotionRule(PromotionRuleType ruleType, PromotionRule rule);

    List<Map<String, Object>> listSortColumn(Long promotionId);

    Boolean updateSortPromotion(List<SortPromotion> listSort) throws BusinessException;

    List<Map<String, Object>> listSortItemByColumn(Long promotionId, SortColumn sortColumn);

    Boolean updateItemsSort(Long promotionId, SortColumn sortColumn, List<Map<String, Object>> items) throws BusinessException;

    List<Map<String, Object>> listBannerPos();

    List<Map<String, Object>> listPromotionByBannerIds(List<Long> bannerIds);

    Integer saveImgForBanner(Promotion promotion);

    Promotion getPromotion(Long promotionId);

    List<PromotionBrandHot> getPromotionBrandHot(Long promotionId);

    Integer updatePromotionBrandHot(List<PromotionBrandHot> listPromotionBrandHot);

    void refreshSnapshotProductByPromotion(Long promotionId);

    void refreshSnapshotForAddProduct(Long productId);

    void refreshBatchSnapshotForAddProduct(List<Long> productIds);

    /**
     * 查询已设置规则的vendor
     * @param promotionId
     * @return
     */
    List<Map<String, Object>> getPromotionBoutiqueHasRuleList(Long promotionId);

    /**
     * 查询promotion中vendor的每个season的商品数量
     * @param params
     * @return
     */
    List<Map<String, Object>> getPromotionBoutiqueProductCountBySeason(Map<String, Object> params);

    /**
     * 查询promotion排除的商品数量
     * @param promotionId
     * @return
     */
    Integer getPromotionBoutiqueExcludeProductCount(Long promotionId);

    List<Map<String, Object>> listSeasonIncludeRulePromotion(Map<String, Object> params);
    List<Map<String, Object>> listSeasonExcludeRulePromotion(Map<String, Object> params);

    int countSeasonIncludeRulePromotion(Map<String, Object> params);
    int countSeasonExcludeRulePromotion(Map<String, Object> params);

    int updatePromotionSaveTime(Long promotionId);
}
