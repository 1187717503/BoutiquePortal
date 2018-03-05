package com.intramirror.product.core.mapper;

import com.intramirror.product.api.entity.promotion.BrandSort;
import com.intramirror.product.api.entity.promotion.CategorySort;
import com.intramirror.product.api.entity.promotion.SortPromotion;
import com.intramirror.product.api.entity.promotion.VendorSort;
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

    // sort

    List<Map<String, Object>> listSortColumn(Long promotionId);

    int updateSortColumn(SortPromotion sortPromotion);

    List<Map<String, Object>> getBrandSortItem(Long promotionId);

    List<Map<String, Object>> getVendorSortItem(Long promotionId);

    List<Map<String, Object>> getCategorySortItem(Long promotionId);

    List<Map<String, Object>> getSimpleSort(SortPromotion sortPromotion);

    int removeBrandSortItems(Long promotionId);

    int removeVendorSortItems(Long promotionId);

    int removeCategorySortItems(Long promotionId);

    int updateSimpleSort(SortPromotion sortPromotion);

    int insertBrandSort(BrandSort brandSort);

    int insertVendorSort(VendorSort vendorSort);

    int insertCategorySort(CategorySort categorySort);

    List<Map<String, Object>> listBannerPos();

    List<Map<String, Object>> listPromotionByBannerIds(List<Long> bannerIds);

    void removeSnapshotProduct(Long promotionId);

    void generateRuleToSnapshotProduct(@Param("promotionId") Long promotionId, @Param("promotionRuleId") Long promotionRuleId, @Param("vendorId") Long vendorId,
            @Param("seasonCode") String seasonCode, @Param("brandId") Long brandId, @Param("category") List<Long> category);

    void removeExcludeSnapshotProduct(@Param("vendorId") Long vendorId, @Param("seasonCode") String seasonCode, @Param("brandId") Long brandId,
            @Param("category") List<Long> category);
}

