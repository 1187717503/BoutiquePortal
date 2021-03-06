package com.intramirror.web.controller.price;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.api.service.rule.IRuleService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/19.
 */

@Controller
@RequestMapping("/rule")
public class RuleController {

    private static Logger logger = LoggerFactory.getLogger(RuleController.class);

    @Resource(name = "productRuleServiceImpl")
    private IRuleService iRuleService;
    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;

    /**
     * 查询vendor不同status规则中包含的season
     * @param ruleStatus
     * @see PriceChangeRuleEnum.Status
     * @param vendor_id
     * @return ResultMessage
     */
    @RequestMapping("/select/queryRuleByHasSeason")
    @ResponseBody
    public ResultMessage queryRuleByHasSeason(@Param("ruleStatus") String ruleStatus,@Param("vendor_id")String vendor_id,
                                              @Param("price_type")String price_type,@Param("categoryType")String categoryType){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(ruleStatus) || StringUtils.isBlank(vendor_id))
                return resultMessage.errorStatus().putMsg("info"," params is null !!!");

            Map<String,Object> params = new HashMap<>();
//            params.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            params.put("price_type",price_type);
            params.put("status",ruleStatus);
            params.put("vendor_id",vendor_id);
            params.put("categoryType",categoryType);
            List<Map<String,Object>> hasSeasonMaps =  iRuleService.queryRuleByHasSeason(params);
            resultMessage.successStatus().putMsg("info","success").setData(hasSeasonMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    /**
     * 查询vendor不同status规则中不包含的season
     * @param ruleStatus
     * @see PriceChangeRuleEnum.Status
     * @param vendor_id
     * @return ResultMessage
     */
    @RequestMapping("/select/queryRuleByNotHasSesaon")
    @ResponseBody
    public ResultMessage queryRuleByNotHasSesaon(@Param("ruleStatus") String ruleStatus,@Param("vendor_id") String vendor_id,
                                                 @Param("price_type")String price_type,@Param("categoryType")String categoryType){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(ruleStatus) || StringUtils.isBlank(vendor_id))
                return resultMessage.errorStatus().putMsg("info"," params is null !!!");

            Map<String,Object> params = new HashMap<>();
//            params.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            params.put("price_type",price_type);
            params.put("status",ruleStatus);
            params.put("vendor_id",vendor_id);
            params.put("categoryType",categoryType);
            List<Map<String,Object>> notHasSeasonMaps =  iRuleService.queryRuleByNotHasSesaon(params);
            resultMessage.successStatus().putMsg("info","success").setData(notHasSeasonMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    /**
     * 根据price_change_rule_id查询exception_flag为0的品牌目录规则(brand_id为0的是默认规则),支持品牌模糊查询
     * @param price_change_rule_id
     * @param english_name
     * @return ResultMessage
     */
    @RequestMapping("/select/queryRuleByBrandZero")
    @ResponseBody
    public ResultMessage queryRuleByBrandZero(@Param("price_change_rule_id")String price_change_rule_id,@Param("english_name")String english_name){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }
            //PriceChangeRule priceChangeRule=iPriceChangeRule.selectByPrimaryKey(Long.valueOf(price_change_rule_id));
            Map<String,Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("english_name",english_name);
            params.put("price_change_rule_id",price_change_rule_id);
            /*if(priceChangeRule != null) {
                params.put("categoryType", Integer.valueOf(priceChangeRule.getCategoryType()));
            }*/
            List<Map<String,Object>> brandMaps =  iRuleService.queryRuleByBrand(params);
            resultMessage.successStatus().putMsg("info","success").setData(brandMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    /**
     * 根据price_change_rule_id查询改规则中不包含的品牌数据
     * @param price_change_rule_id
     * @param english_name
     * @return ResultMessage
     */
    @RequestMapping("/select/queryNotRuleByBrand")
    @ResponseBody
    public ResultMessage queryNotRuleByBrand(@Param("price_change_rule_id")String price_change_rule_id,@Param("english_name")String english_name){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id)) {
                return resultMessage.errorStatus().putMsg("info","price_change_rule_id is null !!!");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("exception_flag", 0);
            params.put("english_name",english_name);
            params.put("price_change_rule_id",price_change_rule_id);
            /*PriceChangeRule priceChangeRule = iPriceChangeRule.selectByPrimaryKey(Long.valueOf(price_change_rule_id));
            if(priceChangeRule != null) {
                params.put("categoryType", Integer.valueOf(priceChangeRule.getCategoryType()));
            }*/
            List<Map<String,Object>> brandMaps =  iRuleService.queryNotRuleByBrand(params);
            resultMessage.successStatus().putMsg("info","success").setData(brandMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    /**
     * 根据price_change_rule_id查询exception_flag为1的品牌目录规则
     * @param price_change_rule_id
     * @return ResultMessage
     */
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
            List<Map<String,Object>> brandMaps =  iRuleService.queryRuleByBrandOne(params);
            resultMessage.successStatus().putMsg("info","success").setData(brandMaps);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    /**
     * 根据price_change_rule_id
     * @param price_change_rule_id
     * @return
     */
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


    /**
     * 异步请求刷新当前refresh im price状态
     * @param price_change_rule_id
     * @param flag
     * @param ruleStatus
     * @return
     */
    @RequestMapping(value = "/select/queryRuleRefreshStatus" ,method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage queryRuleRefreshStatus(@Param("price_change_rule_id")String price_change_rule_id,@Param("flag")String flag,
                                                @Param("ruleStatus") String ruleStatus){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try{

            if(StringUtils.isBlank(ruleStatus) || StringUtils.isBlank(price_change_rule_id)){
                return resultMessage.errorStatus().putMsg("info"," params is null !!!");
            }
            List<Map<String,Object>> refreshDateMaps = new ArrayList<>();
            if(StringUtils.isBlank(flag)){
                //falg 为空查询当前的season的refresh状态
                Map<String,Object> refreshDateMap =new HashMap<>();
                //snapshot  update_at ||  refresh IM
                refreshDateMap = iRuleService.getSnapShotUpdateTime(price_change_rule_id,refreshDateMap);
                refreshDateMap.put("price_change_rule_id",price_change_rule_id);
                refreshDateMaps.add(refreshDateMap);
                resultMessage.successStatus().putMsg("info","success").setData(refreshDateMaps);
            }
            if(flag.equals("all")){
                PriceChangeRule pcrModel = iPriceChangeRule.selectByPrimaryKey(Long.parseLong(price_change_rule_id));
                if(pcrModel != null){
                    Map<String,Object> params = new HashMap<>();
                    params.put("status",ruleStatus);
                    params.put("vendor_id",pcrModel.getVendorId());
                    params.put("price_type",pcrModel.getPriceType());
                    params.put("categoryType",pcrModel.getCategoryType());
                    refreshDateMaps =  iRuleService.queryRuleByHasSeason(params);
                }
                resultMessage.successStatus().putMsg("info","success").setData(refreshDateMaps);
            }
        } catch(Exception e){
            e.printStackTrace();
            logger.error(" error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }
}
