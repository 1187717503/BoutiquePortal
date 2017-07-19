package com.intramirror.web.controller.price;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.model.PriceChangeRuleProduct;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.web.service.PriceChangeRuleService;

/**
 * 
 * @author wzh
 *
 */
@Controller
@RequestMapping("/priceChangeRule")
public class PriceChangeRuleController {
	
	private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleController.class);
	
	@Autowired
	private IPriceChangeRuleCategoryBrandService priceChangeRuleCategoryBrandService;
	
	@Autowired
	private IPriceChangeRule priceChangeRule;
	
	@Autowired
	private IPriceChangeRuleProductService priceChangeRuleProductService;
	
	@Autowired
	private IPriceChangeRuleGroupService priceChangeRuleGroupService;
	
	@Autowired
	private PriceChangeRuleService priceChangeRuleService;
	


	@RequestMapping("/create")
	@ResponseBody
	public Map<String, Object> priceChangeRuleCreate(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		if(!checkParams(map)){
			result.put("info","parameter is incorrect");
			return result;
		}
		
		//调用service 创建 事物管理
		try {
			result = priceChangeRuleService.createPriceChangeRule(map);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRule fail ");
			return result;
		}

		
        result.put("status", StatusType.SUCCESS);
		return result;
	}
	
	
    /**
     * 检查参数
     */
    public static boolean checkParams(Map<String, Object> params) {
    	
    	if(params.get("name") ==null || StringUtils.isBlank(params.get("name").toString())){
    		return false;
    	}
    	
    	if(params.get("price_type") == null || StringUtils.isBlank(params.get("price_type").toString()) 
    			|| checkIntegerNumber(params.get("price_type").toString())){
    		return false;
    	}
    	
    	if(params.get("valid_from") == null || StringUtils.isBlank(params.get("valid_from").toString())){
    		return false;
    	}
    	
    	if(params.get("status") == null || StringUtils.isBlank(params.get("status").toString()) 
    			|| checkIntegerNumber(params.get("status").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_category_brand_list") == null || StringUtils.isBlank(params.get("price_change_rule_category_brand_list").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_all_brand_list") == null || StringUtils.isBlank(params.get("price_change_rule_all_brand_list").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_product_list") == null || StringUtils.isBlank(params.get("price_change_rule_product_list").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_group_list") == null || StringUtils.isBlank(params.get("price_change_rule_group_list").toString())){
    		return false;
    	}

    	return true;
    }
    
    private static boolean checkIntegerNumber(String vue){
    	if(StringUtils.isNotBlank(vue)){
    		try{
    			Integer.parseInt(vue);
    		}catch(Exception e){
    			return true;
    		}
    	}
    	return false;
    }
	
}
