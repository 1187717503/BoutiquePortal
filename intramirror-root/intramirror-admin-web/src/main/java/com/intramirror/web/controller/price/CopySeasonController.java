package com.intramirror.web.controller.price;


import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.api.service.IPriceChangeRuleSeasonGroupService;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.CopySeasonPriceChangeRuleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/season")
public class CopySeasonController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CopySeasonController.class);

    @Autowired
    private CopySeasonPriceChangeRuleService copySeasonPriceChangeRule;

    @Autowired
    private IPriceChangeRuleGroupService iPriceChangeRuleGroupService;

    @Autowired
    private IPriceChangeRuleProductService iPriceChangeRuleProductService;

    @Autowired
    private IPriceChangeRuleCategoryBrandService iPriceChangeRuleCategoryBrandService;

    @Autowired
    private IPriceChangeRuleSeasonGroupService iPriceChangeRuleSeasonGroupService;

    @Autowired
    private IPriceChangeRule iPriceChangeRule;


    @RequestMapping(value = "/copyPriceChangeRule", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> copyPriceChangeRule(@RequestBody Map<String, Object> map) {
        logger.info("priceChangeRuleCreate param:" + new Gson().toJson(map));
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> parms = new HashMap<>();
        result.put("status", StatusType.FAILURE);
        if (!checkCopyParams(map)) {
            result.put("info", "parameter is incorrect");
            return result;
        }

        //获取要复制的规则
        PriceChangeRule priceChangeRule = iPriceChangeRule.selectByPrimaryKey(Long.valueOf(map.get("price_change_rule_id").toString()));
        //获取要复制的priceChangeRuleGroup的数据
        List<PriceChangeRuleGroup> priceChangeRuleGroupList = iPriceChangeRuleGroupService.getPriceChangeRuleGroupListByPriceChangeRuleId(Long.valueOf(map.get("price_change_rule_id").toString()));
        //获取要复制的PriceChangeRuleProduct的数据
        List<PriceChangeRuleProduct> priceChangeRuleProductList = iPriceChangeRuleProductService.getPriceChangeRuleGroupListByPriceChangeRuleId(Long.valueOf(map.get("price_change_rule_id").toString()));
        //获取要复制的PriceChangeRuleCategoryBrand的数据
        List<PriceChangeRuleCategoryBrand> priceChangeRuleCategoryBrandList = iPriceChangeRuleCategoryBrandService.getPriceChangeRuleGroupListByPriceChangeRuleIdAndExceptionFlag(Long.valueOf(map.get("price_change_rule_id").toString()), 1);
        //获取要复制的PriceChangeRuleAllBrand的数据
        List<PriceChangeRuleCategoryBrand> priceChangeRuleAllBrandList = iPriceChangeRuleCategoryBrandService.getPriceChangeRuleGroupListByPriceChangeRuleIdAndExceptionFlag(Long.valueOf(map.get("price_change_rule_id").toString()), 0);
        //获取要复制的PriceChangeRuleSeasonGroup的数据
        List<PriceChangeRuleSeasonGroup> priceChangeRuleSeasonGroupList = iPriceChangeRuleSeasonGroupService.getPriceChangeRuleGroupListByPriceChangeRuleIdAndSeasonCode(Long.valueOf(map.get("price_change_rule_id").toString()), map.get("season_code").toString());

        //调用service 创建 事物管理
        try {
//            JsonArray seasonGroupList = new JsonParser().parse(map.get("season_group_list").toString()).getAsJsonArray();
            //数据组装
            parms.put("name", priceChangeRule.getName());
            parms.put("price_type", priceChangeRule.getPriceType());
            parms.put("vendor_id", priceChangeRule.getVendorId());
            parms.put("status", priceChangeRule.getStatus());
            parms.put("price_change_rule_group_list", new Gson().toJson(priceChangeRuleGroupList));
            parms.put("price_change_rule_product_list", new Gson().toJson(priceChangeRuleProductList));
            parms.put("price_change_rule_category_brand_list", new Gson().toJson(priceChangeRuleCategoryBrandList));
            parms.put("price_change_rule_all_brand_list", new Gson().toJson(priceChangeRuleAllBrandList));
            parms.put("season_group_list", map.get("season_group_list").toString());
            //保存数据
            result = copySeasonPriceChangeRule.copySeasonPriceChangeRule(parms);
            //seasonGroup数据单独复制
            result.put("season_group_list", map.get("season_group_list").toString());
            copySeasonPriceChangeRule.copyPriceChangeRuleSeasonCode(result);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("info", "create priceChangeRule fail ");
            return result;
        }


        result.put("status", StatusType.SUCCESS);
        return result;
    }


    /**
     * 检查参数
     */
    public static boolean checkCopyParams(Map<String, Object> params) {

        if (params.get("price_change_rule_id") == null || StringUtils.isBlank(params.get("price_change_rule_id").toString())) {
            return false;
        }

        if (params.get("season_code") == null || StringUtils.isBlank(params.get("season_code").toString())) {
            return false;
        }

        if (params.get("season_group_list") == null || StringUtils.isBlank(params.get("season_group_list").toString())) {
            return false;
        }

        return true;
    }

}
