package com.intramirror.web.controller.price;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.user.api.model.User;
import com.intramirror.web.controller.BaseController;
import com.intramirror.web.service.PriceChangeRuleService;

/**
 * 
 * @author wzh
 *
 */
@Controller
@RequestMapping("/priceChangeRule")
public class PriceChangeRuleController extends BaseController{
	
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
	

	
	@SuppressWarnings("unchecked")
	@RequestMapping("/initPriceChangeRule")
	@ResponseBody
	public Map<String, Object> initPriceChangeRule(@RequestBody Map<String, Object> map,HttpServletRequest httpRequest){
		logger.info("initPriceChangeRule param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		//校验登录
		Map< String, Object> userMap = new HashMap<String, Object>();
		User user = null;
		try {
			userMap = super.baseController(httpRequest);
			result.put("userStatus", userMap.get("userStatus"));
			if(Integer.parseInt(userMap.get("status").toString()) == StatusType.SUCCESS){
				user = (User) userMap.get("user");
				if(user == null){
					result.put("info","User not logged in");
					return result;
				}
				map.put("userId", user.getUserId());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			result.put("info","User not logged in");
			return result;
		}

		//校验参数
		if(!initCheckParams(map)){
			result.put("info","parameter is incorrect");
			return result;
		}
		
		//调用service 创建 事物管理
		try {
			result = priceChangeRuleService.initPriceChangeRule(map);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRule fail ");
			return result;
		}

		
        result.put("status", StatusType.SUCCESS);
		return result;
	}

	@RequestMapping("/createPriceChangeRule")
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
	
	
	
	@RequestMapping("/updatePriceChangeRule")
	@ResponseBody
	public Map<String, Object> priceChangeRuleUpdate(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		if(!checkUpdateParams(map)){
			result.put("info","parameter is incorrect");
			return result;
		}
		
		//调用service 创建 事物管理
		try {
			result = priceChangeRuleService.updatePriceChangeRule(map);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","update priceChangeRule fail ");
			return result;
		}

		
        result.put("status", StatusType.SUCCESS);
		return result;
	}
	
	
	
	/**
	 * 添加PriceChangeRuleCategoryBrand
	 * @param map
	 * @return
	 */
	@RequestMapping("/createPriceChangeRuleCategoryBrand")
	@ResponseBody
	public Map<String, Object> priceChangeRuleCategoryBrandCreate(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleCategoryBrandCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		if(!checkUpdateParams(map)){
			result.put("info","parameter is incorrect");
			return result;
		}
		
		try {
			
		    JsonObject priceChangeRuleCategory = new JsonParser().parse(map.get("price_change_rule_category_brand").toString()).getAsJsonObject();
	
	        PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
	        priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRuleCategory.get("price_change_rule_id").getAsLong());
	        priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleCategory.get("category_id").getAsLong());
	        priceChangeRuleCategoryBrand.setLevel(priceChangeRuleCategory.get("level").getAsByte());
	        priceChangeRuleCategoryBrand.setBrandId(priceChangeRuleCategory.get("brand_id").getAsLong());
	        priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleCategory.get("discount_percentage").getAsLong());
	        priceChangeRuleCategoryBrand.setExceptionFlag (1);

			priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);
			
	        if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            return result;
	        }
		    
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRuleCategoryBrand fail ");
			return result;
		}

		
        result.put("status", StatusType.SUCCESS);
		return result;
	}
	
	
	

	/**
	 * 根据price_change_rule_category_brand_id 删除PriceChangeRuleCategoryBrand
	 * @param map
	 * @return
	 */
	@RequestMapping("/deletePriceChangeRuleCategoryBrand")
	@ResponseBody
	public Map<String, Object> deletePriceChangeRuleCategoryBrand(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleCategoryBrandCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		//校验
		if(map.get("price_change_rule_category_brand_id") == null ||StringUtils.isBlank(map.get("price_change_rule_category_brand_id").toString())){
			result.put("info","Parameter cannot be null");
			return result;
		}
		
		try {
			//根据ID删除
			String priceChangeRuleCategoryBrandId = map.get("price_change_rule_category_brand_id").toString();
			int row = priceChangeRuleCategoryBrandService.deletePriceChangeRuleCategoryBrand(Long.parseLong(priceChangeRuleCategoryBrandId));
			
			//判断是否成功
	        if (row > 0) {
	            result.put("status", StatusType.SUCCESS);
	        }else{
	        	 result.put("info", "delete priceChangeRuleCategoryBrand fail");
	        }
		    
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRuleCategoryBrand fail ");
			return result;
		}
		
		return result;
	}
	
	
	
	
	/**
	 * 添加priceChangeRuleProductGroup
	 * @param map
	 * @return
	 */
	@RequestMapping("/createPriceChangeRuleProductGroup")
	@ResponseBody
	public Map<String, Object> priceChangeRuleProductGroupCreate(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleProductGroupCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		if(!checkUpdateParams(map)){
			result.put("info","parameter is incorrect");
			return result;
		}
		
		try {
			//获取对象值
			JsonObject priceChangeRuleGroupjson= new JsonParser().parse(map.get("price_change_rule_product_group").toString()).getAsJsonObject();

	        PriceChangeRuleGroup priceChangeRuleGroup = new PriceChangeRuleGroup();
	        priceChangeRuleGroup.setPriceChangeRuleId(priceChangeRuleGroupjson.get("price_change_rule_id").getAsLong());
	        priceChangeRuleGroup.setProductGroupId(priceChangeRuleGroupjson.get("product_group_id").getAsLong());
	        priceChangeRuleGroup.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleGroupjson.get("discount_percentage").getAsLong());

	        //创建
	        priceChangeRuleGroupService.insertSelective(priceChangeRuleGroup);
	        
	        if (priceChangeRuleGroup.getPriceChangeRuleGroupId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            return result;
	        }
		    
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRuleCategoryBrand fail ");
			return result;
		}

		
        result.put("status", StatusType.SUCCESS);
		return result;
	}
	
	
	
	/**
	 * 根据price_change_rule_group_id 删除PriceChangeRuleGroup
	 * @param map
	 * @return
	 */
	@RequestMapping("/deletePriceChangeRuleGroup")
	@ResponseBody
	public Map<String, Object> deletePriceChangeRuleGroup(@RequestBody Map<String, Object> map){
		logger.info("deletePriceChangeRuleGroup param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		//校验
		if(map.get("price_change_rule_group_id") == null ||StringUtils.isBlank(map.get("price_change_rule_group_id").toString())){
			result.put("info","Parameter cannot be null");
			return result;
		}
		
		try {
			//根据ID删除
			String priceChangeRuleGroupId = map.get("price_change_rule_group_id").toString();
			int row = priceChangeRuleGroupService.deleteByPrimaryKey(Long.parseLong(priceChangeRuleGroupId));
			
			//判断是否成功
	        if (row > 0) {
	            result.put("status", StatusType.SUCCESS);
	        }else{
	        	 result.put("info", "delete priceChangeRuleGroupId fail");
	        }
		    
		} catch (Exception e) {
			e.printStackTrace();
			result.put("info","create priceChangeRuleGroupId fail ");
			return result;
		}
		
		return result;
	}
	
	
    /**
     * 检查参数
     */
    public static boolean  initCheckParams(Map<String, Object> params) {
    	
    	if(params.get("name") ==null || StringUtils.isBlank(params.get("name").toString())){
    		return false;
    	}
    	
    	if(params.get("price_type") == null || StringUtils.isBlank(params.get("price_type").toString()) 
    			|| checkIntegerNumber(params.get("price_type").toString())){
    		return false;
    	}
    	
    	if(params.get("status") == null || StringUtils.isBlank(params.get("status").toString()) 
    			|| checkIntegerNumber(params.get("status").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_season_group_List") == null || StringUtils.isBlank(params.get("price_change_rule_season_group_List").toString())){
    		return false;
    	}

    	return true;
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
    
    

    
    
    /**
     * 检查参数
     */
    public static boolean checkUpdateParams(Map<String, Object> params) {
    	
    	if(params.get("name") ==null || StringUtils.isBlank(params.get("name").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_id") == null || StringUtils.isBlank(params.get("price_change_rule_id").toString())){
    		return false;
    	}
    	
    	if(params.get("valid_from") == null || StringUtils.isBlank(params.get("valid_from").toString())){
    		return false;
    	}
    	
    	if(params.get("status") == null || StringUtils.isBlank(params.get("status").toString()) 
    			|| checkIntegerNumber(params.get("status").toString())){
    		return false;
    	}
    	
    	if(params.get("price_change_rule_all_brand_list") == null || StringUtils.isBlank(params.get("price_change_rule_all_brand_list").toString())){
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
