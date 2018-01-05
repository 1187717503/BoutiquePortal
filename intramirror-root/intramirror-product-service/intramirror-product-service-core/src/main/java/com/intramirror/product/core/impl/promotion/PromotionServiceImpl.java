package com.intramirror.product.core.impl.promotion;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.entity.promotion.BrandEntity;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.PromotionExclude;
import com.intramirror.product.api.model.PromotionExcludeRule;
import com.intramirror.product.api.model.PromotionInclude;
import com.intramirror.product.api.model.PromotionIncludeRule;
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
    public List<Map<String, Object>> listActivePromotion() {
        return promotionRuleMapper.listActivePromotion();
    }

    @Override
    public List<Map<String, Object>> listExcludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listExcludeRulePromotion(promotionId);
    }

    @Override
    public List<Map<String, Object>> listIncludeRulePromotion(Long promotionId) {
        return promotionRuleMapper.listIncluedRulePromotion(promotionId);
    }

    @Override
    public List<PromotionRuleDetail> processPromotionRule(PromotionRule rule, PromotionRuleType ruleType) {
        LOGGER.info("Start to save promotion include rule.");
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            promotionRuleMapper.insertIncludeRule(rule);
        } else {
            promotionRuleMapper.insertExcludeRule(rule);
        }
        return insertRuleDetailByRule(rule, ruleType);
    }

    @Override
    public Boolean removePromotionRule(Long ruleId, PromotionRuleType ruleType) {
        if (ruleType == PromotionRuleType.INCLUDE_RULE) {
            promotionRuleMapper.removeIncludeRule(ruleId);
            promotionRuleMapper.removeIncludeRuleDetail(ruleId);
        } else {
            promotionRuleMapper.removeExcludeRule(ruleId);
            promotionRuleMapper.removeExcludeRuleDetail(ruleId);
        }

        return true;
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
                    ruleDetail.setPromotionIncludeRuleId(((PromotionIncludeRule) rule).getPromotionIncludeRuleId());
                    ruleDetail.setSeasonCode(rule.getSeasonCode());
                    ruleDetail.setVendorId(rule.getVendorId());
                    promotionRuleMapper.insertIncludeRuleDetail(ruleDetail);
                    listPromotionRuleDetail.add(ruleDetail);
                } else {
                    PromotionExclude ruleDetail = new PromotionExclude();
                    ruleDetail.setBrandId(brand.getBrandId());
                    ruleDetail.setCategoryId(category.getCategoryId());
                    ruleDetail.setPromotionId(rule.getPromotionId());
                    ruleDetail.setPromotionExcludeRuleId(((PromotionExcludeRule) rule).getPromotionExcludeRuleId());
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
