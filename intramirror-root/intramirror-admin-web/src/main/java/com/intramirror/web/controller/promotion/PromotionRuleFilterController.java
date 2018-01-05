package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.promotion.IPromotionService;
import static com.intramirror.web.common.request.ConstantsEntity.EXCLUDE;
import static com.intramirror.web.common.request.ConstantsEntity.INCLUDE;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Response listPromotionRule(@PathVariable("ruleType") String ruleType, @Param("promotionId") String promotionId) {
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

            for (int i = 0, len = arrBrands.size(); i < len; i++) {
                JSONObject brand = arrBrands.getJSONObject(i);
                String name = brand.getString("english_name");
                brand.remove("english_name");
                brand.put("name", name);
            }
        }
        return data;
    }

}
