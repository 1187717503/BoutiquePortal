package com.intramirror.web.controller.promotion;

import com.alibaba.fastjson15.JSONArray;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.core.common.response.Response;
import com.intramirror.product.api.service.promotion.IPromotionService;
import java.util.ArrayList;
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

    private static final String include ="include";
    private static final String exclude ="exclude";

    @Autowired
    IPromotionService promotionService;

    /**
     * @param promotionType include,exclude
     */
    @RequestMapping(value = "/{promotionType}/list",method = RequestMethod.GET)
    public Response listPromotionRule(@PathVariable("promotionType") String promotionType,@Param("bannerId") String bannerId){
        List<Map<String,Object>> data = new ArrayList<>();
        if(promotionType.equals(include)) {
            data = promotionService.listIncluedRulePromotion(bannerId);
        } else if(promotionType.equals(exclude)) {
            data = promotionService.listExcludeRulePromotion(bannerId);
        }
        return Response.status(StatusType.SUCCESS).data(transFormation(data));
    }

    public List<Map<String,Object>> transFormation(List<Map<String,Object>> data){
        for (Map<String,Object> rule : data) {
            String categorys = rule.get("categorys") == null ? "[]" : rule.get("categorys").toString();
            String brands = rule.get("brands") == null ? "[]" : rule.get("brands").toString();

            rule.put("categorys",JSONArray.parseArray(categorys));
            rule.put("brands",JSONArray.parseArray(brands));
        }
        return data;
    }

}
