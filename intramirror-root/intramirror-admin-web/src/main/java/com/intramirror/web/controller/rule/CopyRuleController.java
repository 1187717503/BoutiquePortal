package com.intramirror.web.controller.rule;

import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.service.vendor.IQueryVendorService;
import com.intramirror.web.controller.BaseController;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dingyifan on 2017/7/20.
 */

@Controller
@RequestMapping("/rule/copy")
public class CopyRuleController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(CopyRuleController.class);

    @Resource(name = "productPriceChangeRule")
    private IPriceChangeRule iPriceChangeRule;

    @Resource(name = "userQueryVendorServiceImpl")
    private IQueryVendorService iQueryVendorService;

    @RequestMapping("/activeVendor")
    @ResponseBody
    public ResultMessage activeVendor(@Param("discount") String discount, @Param("to_vendor_id") String to_vendor_id, HttpServletRequest httpRequest,@Param("category_type") String category_type) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if (!this.checkParams(to_vendor_id)) {
                return resultMessage.errorStatus().putMsg("info", "params is error !!!");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("vendor_id", to_vendor_id);
            params.put("discount", discount);
            params.put("status", PriceChangeRuleEnum.Status.ACTIVE.getCode());
            params.put("to_vendor_id", to_vendor_id);
            params.put("category_type",category_type);

            // select user_id
            Vendor vendor = iQueryVendorService.queryVendorByVendorId(params);
            if (vendor == null) {
                return resultMessage.errorStatus().putMsg("info", " vendor is null !!!");
            }
            params.put("user_id", vendor.getUserId());
            return iPriceChangeRule.copyRuleByVendor(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/pengingVendor")
    @ResponseBody
    public ResultMessage pengingVendor(@Param("discount") String discount, @Param("to_vendor_id") String to_vendor_id, HttpServletRequest httpRequest,@Param("category_type") String category_type) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if (!this.checkParams(to_vendor_id)) {
                return resultMessage.errorStatus().putMsg("info", "params is error !!!");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("vendor_id", to_vendor_id);
            params.put("discount", discount);
            params.put("to_vendor_id", to_vendor_id);
            params.put("status", PriceChangeRuleEnum.Status.PENDING.getCode());
            params.put("category_type",category_type);

            // select user_id
            Vendor vendor = iQueryVendorService.queryVendorByVendorId(params);
            if (vendor == null) {
                return resultMessage.errorStatus().putMsg("info", " vendor is null !!!");
            }
            params.put("user_id", vendor.getUserId());

            return iPriceChangeRule.copyRuleByVendor(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/seasonVendor")
    @ResponseBody
    public ResultMessage seasonVendor(@Param("price_change_rule_id") String price_change_rule_id, @Param("seasons") String seasons,
            @Param("vendor_id") String vendor_id, @Param("price_type") String price_type, HttpServletRequest httpRequest) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if (StringUtils.isBlank(price_change_rule_id) || seasons == null || seasons.length() == 0) {
                return resultMessage.errorStatus().putMsg("info", "params is error !!!");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("price_change_rule_id", price_change_rule_id);
            params.put("seasons", seasons);
            params.put("vendor_id", vendor_id);
            params.put("price_type", price_type);

            // select user_id
            Vendor vendor = iQueryVendorService.queryVendorByVendorId(params);
            if (vendor == null) {
                return resultMessage.errorStatus().putMsg("info", " vendor is null !!!");
            }
            params.put("user_id", vendor.getUserId());

            return iPriceChangeRule.copyRuleBySeason(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    @RequestMapping("/copyRule")
    @ResponseBody
    public ResultMessage copyRule(@Param("vendor_id") String vendor_id, @Param("price_type") String price_type, @Param("discount") String discount,
            @Param("category_type") String category_type) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            if (StringUtils.isBlank(vendor_id) || StringUtils.isBlank(price_type)) {
                return resultMessage.errorStatus().putMsg("info", " params is null !!!");
            }
            Map<String, Object> params = new HashMap<>();
            params.put("vendor_id", vendor_id);
            params.put("price_type", price_type);
            params.put("discount", discount == null ? "0" : discount);
            params.put("category_type", category_type);

            // select user_id
            Vendor vendor = iQueryVendorService.queryVendorByVendorId(params);
            if (vendor == null) {
                return resultMessage.errorStatus().putMsg("info", " vendor is null !!!");
            }
            params.put("user_id", vendor.getUserId());

            return iPriceChangeRule.copyRule(params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}", e.getMessage());
            resultMessage.errorStatus().putMsg("info", "error message : " + e.getMessage());
        }
        return resultMessage;
    }

    private boolean checkParams(String vendor_id) {
        if (StringUtils.isBlank(vendor_id))
            return false;
        return true;
    }
}
