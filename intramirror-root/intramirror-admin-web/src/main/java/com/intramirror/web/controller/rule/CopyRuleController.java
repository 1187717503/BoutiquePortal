package com.intramirror.web.controller.rule;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
@CrossOrigin
@Controller
@RequestMapping("/rule/copy")
public class CopyRuleController extends BaseController{
    private static Logger logger = LoggerFactory.getLogger(CopyRuleController.class);

    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;

    @RequestMapping("/activeVendor")
    @ResponseBody
    public ResultMessage activeVendor(@Param("vendor_id")String vendor_id,@Param("discount")String discount,@Param("to_vendor_id")String to_vendor_id,HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(!this.checkParams(vendor_id) || !this.checkParams(to_vendor_id)) {
                return resultMessage.errorStatus().putMsg("info","params is error !!!");
            }
            User user = super.getUserInfo(httpRequest);
            if(user == null) {
                return resultMessage.errorStatus().putMsg("info","user is not login !!!");
            }
            Map<String,Object> params = new HashMap<>();
            params.put("vendor_id",vendor_id);
            params.put("discount",discount);
            params.put("user_id",user.getUserId());
            params.put("status", PriceChangeRuleEnum.Status.ACTIVE.getCode());
            params.put("to_vendor_id",to_vendor_id);
            return iPriceChangeRule.copyRuleByVendor(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/pengingVendor")
    @ResponseBody
    public ResultMessage pengingVendor(@Param("vendor_id")String vendor_id,@Param("discount")String discount,@Param("to_vendor_id")String to_vendor_id,HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(!this.checkParams(vendor_id)) {
                return resultMessage.errorStatus().putMsg("info","params is error !!!");
            }
            User user = super.getUserInfo(httpRequest);
            if(user == null) {
                return resultMessage.errorStatus().putMsg("info","user is not login !!!");
            }
            Map<String,Object> params = new HashMap<>();
            params.put("vendor_id",vendor_id);
            params.put("discount",discount);
            params.put("user_id",user.getUserId());
            params.put("to_vendor_id",to_vendor_id);
            params.put("status", PriceChangeRuleEnum.Status.PENDING.getCode());
            return iPriceChangeRule.copyRuleByVendor(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/seasonVendor")
    @ResponseBody
    public ResultMessage seasonVendor(@Param("price_change_rule_id")String price_change_rule_id,@Param("seasons")String seasons,@Param("vendor_id")String vendor_id,HttpServletRequest httpRequest){
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if(StringUtils.isBlank(price_change_rule_id) || seasons == null || seasons.length() == 0) {
                return resultMessage.errorStatus().putMsg("info","params is error !!!");
            }
            User user = super.getUserInfo(httpRequest);
            if(user == null) {
                return resultMessage.errorStatus().putMsg("info","user is not login !!!");
            }
            Map<String,Object> params = new HashMap<>();
            params.put("price_change_rule_id",price_change_rule_id);
            params.put("seasons",seasons);
            params.put("user_id",user.getUserId());
            params.put("vendor_id",vendor_id);
            return iPriceChangeRule.copyRuleBySeason(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    private boolean checkParams(String vendor_id){
        if(StringUtils.isBlank(vendor_id) )
            return false;
        return true;
    }
}
