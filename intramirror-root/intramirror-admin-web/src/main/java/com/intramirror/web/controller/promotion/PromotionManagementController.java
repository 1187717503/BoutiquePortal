package com.intramirror.web.controller.promotion;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.enums.PromotionRuleType;
import com.intramirror.product.api.model.PromotionExcludeRule;
import com.intramirror.product.api.model.PromotionIncludeRule;
import com.intramirror.product.api.model.PromotionRule;
import com.intramirror.product.api.model.PromotionRuleDetail;
import com.intramirror.product.api.service.promotion.IPromotionService;
import static com.intramirror.web.common.request.ConstantsEntity.EXCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/4.
 * @author 123
 */
@RestController
@RequestMapping("/promotion")
public class PromotionManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionManagementController.class);

    @Autowired
    IPromotionService promotionService;

    @PutMapping(value = "/{ruleType}", consumes = "application/json")
    public Response savePromotionProductRule(@PathVariable(value = "ruleType") String ruleType, @RequestBody Map<String, Object> body) {
        LOGGER.info("Save rule with type {}.", ruleType);

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

        promotionRule.setBrands((String) body.get("brands"));
        promotionRule.setCategorys((String) body.get("categorys"));
        promotionRule.setPromotionId(Long.parseLong(body.get("promotionId").toString()));
        promotionRule.setVendorId(Long.parseLong(body.get("vendorId").toString()));
        promotionRule.setSeasonCode((String) body.get("seasonCode"));

        List<PromotionRuleDetail> listRuleDetail = promotionService.processPromotionRule(promotionRule, type);
        return Response.status(StatusType.SUCCESS).data(listRuleDetail);

    }

    @DeleteMapping(value = "/{ruleType}/{ruleId}", consumes = "application/json")
    public Response removePromotionRule(@PathVariable(value = "ruleType") String ruleType, @PathVariable("ruleId") String ruleId) {
        LOGGER.info("Start to remove rule with type {} and ruleId {}.", ruleType, ruleId);

        PromotionRuleType type;
        if (INCLUDE.equals(ruleType)) {
            type = PromotionRuleType.INCLUDE_RULE;
        } else if (EXCLUDE.equals(ruleType)) {
            type = PromotionRuleType.EXCLUDE_RULE;
        } else {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }

        Boolean result = promotionService.removePromotionRule(Long.parseLong(ruleId), type);
        return Response.status(StatusType.SUCCESS).data(result);
    }
}
