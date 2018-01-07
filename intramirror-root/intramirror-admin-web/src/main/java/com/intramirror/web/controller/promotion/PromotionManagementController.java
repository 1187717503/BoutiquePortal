package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.entity.promotion.CategoryEntity;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.PromotionExcludeProduct;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.api.service.promotion.IPromotionExcludeProductService;

import com.intramirror.product.api.service.promotion.IPromotionService;
import static com.intramirror.utils.transform.JsonTransformUtil.toJson;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    IPromotionExcludeProductService promotionExcludeProductService;

    @Autowired
    IProductService productService;

    @GetMapping(value = "/banner/promotion")
    public Response getAllPromotion(@Param(value = "bannerId") Long bannerId) {
        List<Map<String, Object>> result = promotionService.listPromotionByBanner(bannerId);
        return Response.status(StatusType.SUCCESS).data(result);
    }

    @PostMapping(value = "/promotion/{ruleType}", consumes = "application/json")
    public Response savePromotionProductRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody PromotionRuleEntity body) {
        LOGGER.info("Save rule with type {}, {}.", ruleType, body);
        if (body.getPromotionId() == null) {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRule promotionRule = transformPromotionRule(body);

        return Response.status(StatusType.SUCCESS).data(promotionService.processPromotionRule(promotionRule, type));

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

    @PutMapping(value = "/exclude/product/save", consumes = "application/json")
    public Response savePromotionExcludeProduct(@RequestBody PromotionExcludeProduct promotionExcludeProduct) {
        LOGGER.info("savePromotionExcludeProduct by promotionExcludeProduct : {}", toJson(promotionExcludeProduct));

        ProductWithBLOBs productWithBLOBs = productService.selectByPrimaryKey(promotionExcludeProduct.getProductId());
        if (productWithBLOBs == null || !productWithBLOBs.getEnabled()) {
            return Response.status(StatusType.FAILURE).data("product_id not found.");
        }

        List<PromotionExcludeProduct> promotionExcludeProducts = promotionExcludeProductService.selectByParameter(promotionExcludeProduct);
        if (promotionExcludeProducts.size() > 0) {
            return Response.status(StatusType.FAILURE).data("product_id already existed.");
        }

        promotionExcludeProductService.insertPromotionExcludeProduct(promotionExcludeProduct);
        return Response.status(StatusType.SUCCESS).data(promotionExcludeProduct);
    }

    @DeleteMapping(value = "/exclude/product/{promotionExcludeProductId}")
    public Response removePromotionExcludeProduct(@PathVariable("promotionExcludeProductId") Long promotionExcludeProductId) {
        LOGGER.info("removePromotionExcludeProduct by promotionExcludeProductId : {}", promotionExcludeProductId);

        Long row = promotionExcludeProductService.deletePromotionExcludeProduct(promotionExcludeProductId);

        return Response.status(StatusType.SUCCESS).data(row);
    }

    @GetMapping(value = "/exclude/product/list")
    public Response queryPromotionExcludeProduct(@RequestParam(value = "promotionId", required = true) Long promotionId) {
        LOGGER.info("queryPromotionExcludeProduct by promotionId : {}", promotionId);

        PromotionExcludeProduct promotionExcludeProduct = new PromotionExcludeProduct(promotionId);
        List<PromotionExcludeProduct> promotionExcludeProducts = promotionExcludeProductService.selectByParameter(promotionExcludeProduct);

        return Response.status(StatusType.SUCCESS).data(promotionExcludeProducts);
    }

    @PutMapping(value = "/promotion/{ruleType}", consumes = "application/json")
    public Response setPromotionRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody PromotionRuleEntity body) {
        LOGGER.info("Update rule with type {}, {}.", ruleType, body);
        if (body.getRuleId() == null) {
            return Response.status(StatusType.FAILURE).data("rule id is empty.");
        }

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {

            type = PromotionRuleType.INCLUDE_RULE;

        } else if (EXCLUDE.equals(ruleType)) {

            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        PromotionRule promotionRule = transformPromotionRule(body);

        return Response.status(StatusType.SUCCESS).data(promotionService.updatePromotionRule(type, promotionRule));
    }

    private PromotionRule transformPromotionRule(PromotionRuleEntity body) {
        PromotionRule promotionRule = new PromotionRule();
        promotionRule.setRuleId(body.getRuleId());
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
        return promotionRule;
    }
}
