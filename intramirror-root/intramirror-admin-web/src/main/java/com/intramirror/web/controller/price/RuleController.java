package com.intramirror.web.controller.price;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.service.rule.IRuleService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */
@CrossOrigin
@Controller
@RequestMapping("/rule")
public class RuleController {

    private static Logger logger = LoggerFactory.getLogger(RuleController.class);

    @Resource(name = "productRuleServiceImpl")
    private IRuleService iRuleService;

    @RequestMapping("/select/queryRuleByHasSeason")
    @ResponseBody
    public ResultMessage queryRuleByHasSeason(@Param("ruleStatus") String ruleStatus){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            params.put("status",ruleStatus);
            List<Map<String,Object>> hasSeasonMaps =  iRuleService.queryRuleByHasSeason(params);
            resultMessage.successStatus().putMsg("info","success").setData(hasSeasonMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleByNotHasSesaon")
    @ResponseBody
    public ResultMessage queryRuleByNotHasSesaon(@Param("ruleStatus") String ruleStatus){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            params.put("status",ruleStatus);
            List<Map<String,Object>> notHasSeasonMaps =  iRuleService.queryRuleByNotHasSesaon(params);
            resultMessage.successStatus().putMsg("info","success").setData(notHasSeasonMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleByBrandZero")
    @ResponseBody
    public ResultMessage queryRuleByBrandZero(@Param("price_change_rule_id")String price_change_rule_id,@Param("english_name")String english_name){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("english_name",english_name);
            params.put("price_change_rule_id",price_change_rule_id);
            List<Map<String,Object>> brandMaps =  iRuleService.queryRuleByBrand(params);
            resultMessage.successStatus().putMsg("info","success").setData(brandMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleByBrandOne")
    @ResponseBody
    public ResultMessage queryRuleByBrandOne(@Param("price_change_rule_id")String price_change_rule_id){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("exception_flag", 1);
            params.put("price_change_rule_id",price_change_rule_id);
            List<Map<String,Object>> brandMaps =  iRuleService.queryRuleByBrand(params);
            resultMessage.successStatus().putMsg("info","success").setData(brandMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleByGroup")
    @ResponseBody
    public ResultMessage queryRuleByGroup(@Param("price_change_rule_id")String price_change_rule_id){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("price_change_rule_id",price_change_rule_id);
            List<Map<String,Object>> groupMaps =  iRuleService.queryRuleByGroup(params);
            resultMessage.successStatus().putMsg("info","success").setData(groupMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/select/queryRuleByProduct")
    @ResponseBody
    public ResultMessage queryRuleByProduct(@Param("price_change_rule_id")String price_change_rule_id){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("price_change_rule_id",price_change_rule_id);
            List<Map<String,Object>> productMaps =  iRuleService.queryRuleByProduct(params);
            resultMessage.successStatus().putMsg("info","success").setData(productMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }

        return resultMessage;
    }

}
