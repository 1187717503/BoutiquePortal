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

import java.util.*;

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
    public Response listPromotionRule(@PathVariable("ruleType") String ruleType, @Param("promotionId") Long promotionId,
                                      @Param("vendorId") Long vendorId, @Param("seasonCode") String seasonCode,
                                      @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        int total = 0;
        Map<String, Object> params = new HashMap<>();
        params.put("promotionId", promotionId);
        params.put("vendorId", vendorId);
        params.put("seasonCode", seasonCode);

        boolean pageFlg = pageSize != null && pageNo != null;

        if (pageFlg) {
            int start = pageNo > 0 ? 0 : (pageNo - 1) * pageNo;
            params.put("start", start);
            params.put("pageSize", pageSize);
        }
        if (INCLUDE.equals(ruleType)) {
            if (pageFlg) {
                total = promotionService.countSeasonIncludeRulePromotion(params);
            }
            if (pageFlg && total > 0) {
                data = promotionService.listSeasonIncludeRulePromotion(params);
            }
            result.put("total", total);
            result.put("data", transFormation(data));
        } else if (EXCLUDE.equals(ruleType)) {
            if (pageFlg) {
                total = promotionService.countSeasonExcludeRulePromotion(params);
            }
            if (pageFlg && total > 0) {
                data = promotionService.listSeasonExcludeRulePromotion(params);
            }
            result.put("total", total);
            result.put("data", transFormation(data));
        } else {
            return Response.status(StatusType.FAILURE).data(null);
        }
        return Response.status(StatusType.SUCCESS).data(result);
    }

    private List<Map<String, Object>> transFormation(List<Map<String, Object>> data) {
        for (Map<String, Object> rule : data) {
            String categorys = rule.get("categorys") == null ? "[]" : rule.get("categorys").toString();
            String brands = rule.get("brands") == null ? "[]" : rule.get("brands").toString();

            List<Map> arrCategorys = JSONArray.parseArray(categorys, Map.class);
            JSONArray arrBrands = JSONArray.parseArray(brands);
            Set<String> categoryPathSet = new HashSet<>();
            List<Map> categoryForShow = new ArrayList<>();
            for (Map category : arrCategorys) {
                if (category.get("categoryId").toString().equals("-1")) {
                    categoryForShow.add(category);
                    break;
                }
                String name = category.get("name").toString();
                String[] pathArr = name.split(" > ");
                int level = pathArr.length;
                if (level == 1) {
                    categoryPathSet.add(name.trim());
                } else if(level == 2) {
                    if (categoryPathSet.contains(pathArr[0].trim())) {
                        continue;
                    } else {
                        categoryPathSet.add(name.trim());
                    }
                } else if(level == 3) {
                    if (categoryPathSet.contains(pathArr[0].trim()) || categoryPathSet.contains(pathArr[0].trim() + " > " + pathArr[1].trim())) {
                        continue;
                    }
                } else {
                    continue;
                }
                categoryForShow.add(category);
            }

            rule.put("categorys", categoryForShow);
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
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
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
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
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
            return Response.status(StatusType.PARAM_NOT_POSITIVE).build();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("promotionId", promotionId);
        Integer count = promotionService.getPromotionBoutiqueExcludeProductCount(promotionId);
        return Response.status(StatusType.SUCCESS).data(count);
    }

}
