package com.intramirror.web.controller.promotion;

import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.model.PromotionIncludeRule;
import com.intramirror.product.api.service.promotion.IPromotionService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

        PromotionIncludeRule promotionIncludeRule = new PromotionIncludeRule();
        promotionIncludeRule.setBrands((String) body.get("brands"));
        promotionIncludeRule.setCategorys((String) body.get("categorys"));
        promotionIncludeRule.setPromotionId(Long.parseLong(body.get("promotion_id").toString()));
        promotionIncludeRule.setVendorId(Long.parseLong(body.get("vendor_id").toString()));
        promotionIncludeRule.setSeasonCode((String) body.get("season_code"));

        Long promotionIncludeRuleId = promotionService.savePromotionIncludeRule(promotionIncludeRule);

        return Response.status(StatusType.SUCCESS).data(promotionIncludeRuleId);

    }

}
