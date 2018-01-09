package com.intramirror.product.core.impl.promotion;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.Category;
import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.model.PromotionRuleDetail;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.product.core.mapper.CategoryMapper;
import com.intramirror.product.core.mapper.PromotionRuleMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@Service(value = "promotionService")

public class PromotionServiceImpl implements IPromotionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionServiceImpl.class);

    @Autowired
    PromotionRuleMapper promotionRuleMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> listPromotionByBanner(Long bannerId) {
        return promotionRuleMapper.listPromotionByBanner(bannerId);
    }

    @Override
    public List<Map<String, Object>> listExcludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listExcludeRulePromotion(promotionId);
    }

    @Override
    public List<Map<String, Object>> listIncludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listIncluedRulePromotion(promotionId);
    }

    @Transactional
    @Override
    public PromotionRule processPromotionRule(PromotionRule rule, PromotionRuleType ruleType) {
        LOGGER.info("Start to save promotion include rule.");
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            promotionRuleMapper.insertIncludeRule(rule);
        } else {
            promotionRuleMapper.insertExcludeRule(rule);
        }
        insertRuleDetailByRule(rule, ruleType);

        return rule;
    }

    @Transactional
    @Override
    public Boolean removePromotionRule(Long ruleId, PromotionRuleType ruleType) {
        Boolean flag = false;
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            if (promotionRuleMapper.removeIncludeRule(ruleId) > 0) {
                flag = promotionRuleMapper.removeIncludeRuleDetail(ruleId) > 0;
            }

        } else {
            if (promotionRuleMapper.removeExcludeRule(ruleId) > 0) {
                flag = promotionRuleMapper.removeExcludeRuleDetail(ruleId) > 0;
            }
        }

        return flag;
    }

    @Transactional
    @Override
    public Boolean updatePromotionRule(PromotionRuleType ruleType, PromotionRule rule) {
        Boolean flag;
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            flag = promotionRuleMapper.removeIncludeRuleDetail(rule.getRuleId()) > 0;
            flag = (flag & promotionRuleMapper.updateIncludeRule(rule) > 0);

        } else {
            flag = promotionRuleMapper.removeExcludeRuleDetail(rule.getRuleId()) > 0;
            flag = (flag & promotionRuleMapper.updateExcludeRule(rule) > 0);
        }
        return flag & (insertRuleDetailByRule(rule, ruleType).size() > 0);

    }

    private void removeSubCategoryInList(List<CategoryEntity> list, Long parentId, int level) {
        for (CategoryEntity category : list) {
            if (!category.getDel()) {
                continue;
            }
            if (category.getParentId().longValue() == parentId.longValue()) {
                if (level < 3) {
                    removeSubCategoryInList(list, category.getCategoryId(), level + 1);
                }
                category.setDel(true);
            }
        }
    }

    private List<PromotionRuleDetail> insertRuleDetailByRule(PromotionRule rule, PromotionRuleType ruleType) {
        List<BrandEntity> listBrand = JSONObject.parseArray(rule.getBrands(), BrandEntity.class);
        List<CategoryEntity> listCategory = JSONObject.parseArray(rule.getCategorys(), CategoryEntity.class);

        List<CategoryEntity> listRuleCategory = new ArrayList<>();

        Category param = new Category();
        param.setEnabled(true);
        List<Category> listDBCategory = categoryMapper.listAllCategoryByConditions(param);
        Boolean allCategory = false;
        CategoryEntity allCategoryEntity = null;
        for (CategoryEntity category : listCategory) {
            if (category.getCategoryId() == -1L) {
                allCategory = true;
                allCategoryEntity = category;
                break;
            }

            for (Category dbCategory : listDBCategory) {
                if (dbCategory.getCategoryId().longValue() == category.getCategoryId().longValue()) {
                    category.setLevel(dbCategory.getLevel());
                    category.setParentId(dbCategory.getParentId());
                    category.setDel(false);
                }
            }
        }

        if (allCategory) {
            listRuleCategory.add(allCategoryEntity);
        } else {
            for (CategoryEntity category : listCategory) {
                if (category.getLevel() == 1) {
                    removeSubCategoryInList(listCategory, category.getCategoryId(), 1);
                }
            }

            LOGGER.info("After level 1, category size is:{}.", listCategory.size());

            for (CategoryEntity category : listCategory) {
                if (category.getDel()) {
                    continue;
                }

                if (category.getLevel() == 2) {
                    removeSubCategoryInList(listCategory, category.getCategoryId(), 2);
                }
            }
            LOGGER.info("After level 2, category size is:{}.", listCategory.size());

            for (CategoryEntity category : listCategory) {
                if (category.getDel()) {
                    continue;
                }

                if (category.getLevel() == 3) {
                    removeSubCategoryInList(listCategory, category.getCategoryId(), 3);
                }
            }
            LOGGER.info("After level 3, category size is:{}.", listCategory.size());

            listRuleCategory = listCategory;
        }

        List<PromotionRuleDetail> listPromotionRuleDetail = new ArrayList<>();
        for (BrandEntity brand : listBrand) {
            for (CategoryEntity category : listRuleCategory) {
                if (category.getDel()) {
                    continue;
                }

                if (ruleType == PromotionRuleType.INCLUDE_RULE) {
                    PromotionInclude ruleDetail = new PromotionInclude();
                    ruleDetail.setBrandId(brand.getBrandId());
                    ruleDetail.setCategoryId(category.getCategoryId());
                    ruleDetail.setPromotionId(rule.getPromotionId());
                    ruleDetail.setPromotionIncludeRuleId(rule.getRuleId());
                    ruleDetail.setSeasonCode(rule.getSeasonCode());
                    ruleDetail.setVendorId(rule.getVendorId());
                    promotionRuleMapper.insertIncludeRuleDetail(ruleDetail);
                    listPromotionRuleDetail.add(ruleDetail);
                } else {
                    PromotionExclude ruleDetail = new PromotionExclude();
                    ruleDetail.setBrandId(brand.getBrandId());
                    ruleDetail.setCategoryId(category.getCategoryId());
                    ruleDetail.setPromotionId(rule.getPromotionId());
                    ruleDetail.setPromotionExcludeRuleId(rule.getRuleId());
                    ruleDetail.setSeasonCode(rule.getSeasonCode());
                    ruleDetail.setVendorId(rule.getVendorId());
                    promotionRuleMapper.insertExcludeRuleDetail(ruleDetail);
                    listPromotionRuleDetail.add(ruleDetail);
                }
            }
        }
        return listPromotionRuleDetail;
    }
}
