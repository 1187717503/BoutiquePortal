package com.intramirror.product.core.impl.promotion;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.model.PromotionRuleDetail;
import com.intramirror.product.api.service.promotion.IPromotionService;
import com.intramirror.product.core.mapper.PromotionRuleMapper;
import java.util.ArrayList;
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
    private PromotionRuleMapper promotionRuleMapper;

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

    private List<PromotionRuleDetail> insertRuleDetailByRule(PromotionRule rule, PromotionRuleType ruleType) {
        List<BrandEntity> listBrand = JSONObject.parseArray(rule.getBrands(), BrandEntity.class);
        List<CategoryEntity> listCategory = JSONObject.parseArray(rule.getCategorys(), CategoryEntity.class);

        List<PromotionRuleDetail> listPromotionRuleDetail = new ArrayList<>();
        for (BrandEntity brand : listBrand) {
            for (CategoryEntity category : listCategory) {

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
