package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.PromotionExcludeRule;
import com.intramirror.product.api.model.PromotionIncludeRule;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.model.PromotionRuleDetail;
import com.intramirror.product.api.service.promotion.IPromotionService;
import static com.intramirror.web.common.request.ConstantsEntity.EXCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE;
import com.intramirror.web.common.request.PromotionRuleEntity;
import com.intramirror.web.controller.cache.CategoryCache;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@RestController
public class PromotionManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionManagementController.class);

    @Autowired
    IPromotionService promotionService;

    @Autowired
    CategoryCache categoryCache;

    @GetMapping(value = "/banner/promotion")
    public Response getAllPromotion(@Param(value = "bannerId") Long bannerId) {
        List<Map<String, Object>> result = promotionService.listPromotionByBanner(bannerId);
        return Response.status(StatusType.SUCCESS).data(result);
    }

    @PutMapping(value = "/promotion/{ruleType}", consumes = "application/json")
    public Response savePromotionProductRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody PromotionRuleEntity body) {
        LOGGER.info("Save rule with type {}, {}.", ruleType, body);

        PromotionRule promotionRule;
        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            promotionRule = new PromotionIncludeRule();
            type = PromotionRuleType.INCLUDE_RULE;

        } else if (EXCLUDE.equals(ruleType)) {
            promotionRule = new PromotionExcludeRule();
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        promotionRule.setBrands(JSONArray.toJSONString(body.getBrands()));
        promotionRule.setPromotionId(body.getPromotionId());
        promotionRule.setVendorId(body.getVendorId());
        promotionRule.setSeasonCode(body.getSeasonCode());

        for (CategoryEntity category : body.getCategorys()) {
            if (category.getCategoryId() == -1L) {
                category.setName("All Category");
            } else {
                category.setName(categoryCache.getAbsolutelyCategoryPath(category.getCategoryId()));
            }
        }
        promotionRule.setCategorys(JSONArray.toJSONString(body.getCategorys()));

        List<PromotionRuleDetail> listRuleDetail = promotionService.processPromotionRule(promotionRule, type);
        return Response.status(StatusType.SUCCESS).data(listRuleDetail);

    }

    @DeleteMapping(value = "/promotion/{ruleType}/{ruleId}", consumes = "application/json")
    public Response removePromotionRule(@PathVariable(value = "ruleType") String ruleType, @PathVariable("ruleId") Long ruleId) {
        LOGGER.info("Start to remove rule with type {} and ruleId {}.", ruleType, ruleId);

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        Boolean result = promotionService.removePromotionRule(ruleId, type);
        return Response.status(StatusType.SUCCESS).data(result);
    }
}
