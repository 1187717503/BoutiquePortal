package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson15.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.promotion.IPromotionService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intramirror.web.common.request.ConstantsEntity.EXCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE;

/**
 * Created by dingyifan on 2018/1/4.
 */
@RestController
@RequestMapping("/promotion")
public class PromotionRuleFilterController {

    @Autowired
    IPromotionService promotionService;

    /**
     * @param ruleType
     *         must be include or exclude
     * @param promotionId
     *         promotion id
     * @return promotion rules
     */
    @RequestMapping(value = "/{ruleType}", method = RequestMethod.GET)
    public Response listPromotionRule(@PathVariable("ruleType") String ruleType, @Param("promotionId") Long promotionId) {
        List<Map<String, Object>> data;
        if (INCLUDE.equals(ruleType)) {
            data = promotionService.listIncludeRulePromotion(promotionId);
        } else if (EXCLUDE.equals(ruleType)) {
            data = promotionService.listExcludeRulePromotion(promotionId);
        } else {
            return Response.status(StatusType.FAILURE).data(null);
        }
        return Response.status(StatusType.SUCCESS).data(transFormation(data));
    }

    private List<Map<String, Object>> transFormation(List<Map<String, Object>> data) {
        for (Map<String, Object> rule : data) {
            String categorys = rule.get("categorys") == null ? "[]" : rule.get("categorys").toString();
            String brands = rule.get("brands") == null ? "[]" : rule.get("brands").toString();

            JSONArray arrCategorys = JSONArray.parseArray(categorys);
            JSONArray arrBrands = JSONArray.parseArray(brands);

            rule.put("categorys", arrCategorys);
            rule.put("brands", arrBrands);
        }
        return data;
    }

    /**
     * 查询已设置规则的vendor
     * @param promotionId
     * @return
     */
    @RequestMapping(value = "/boutique", method = RequestMethod.GET)
    public Response getPromotionBoutiqueHasRuleList(@Param("promotionId") Long promotionId) {
        if (promotionId == null) {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).data(null);
        }
        List<Map<String, Object>> data = promotionService.getPromotionBoutiqueHasRuleList(promotionId);
        return Response.status(StatusType.SUCCESS).data(data);
    }

    /**
     * 查询promotion中vendor的每个season的商品数量
     * @param promotionId
     * @param vendorId
     * @return
     */
    @RequestMapping(value = "/include/rule/count", method = RequestMethod.GET)
    public Response getPromotionIncludeRuleProductCount(@Param("promotionId") Long promotionId, @Param("vendorId") Long vendorId) {
        if (promotionId == null || vendorId == null) {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).data(null);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("promotionId", promotionId);
        params.put("vendorId", vendorId);
        List<Map<String, Object>> data = promotionService.getPromotionBoutiqueProductCountBySeason(params);
        return Response.status(StatusType.SUCCESS).data(data);
    }

    /**
     * 查询promotion排除的商品数量
     * @param promotionId
     * @return
     */
    @RequestMapping(value = "/exclude/product/count", method = RequestMethod.GET)
    public Response getPromotionExcludeProductCount(@Param("promotionId") Long promotionId) {
        if (promotionId == null) {
            return Response.status(StatusType.PARAM_NOT_POSITIVE).data(null);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("promotionId", promotionId);
        Integer count = promotionService.getPromotionBoutiqueExcludeProductCount(promotionId);
        return Response.status(StatusType.SUCCESS).data(count);
    }

}
