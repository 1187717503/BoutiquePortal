package com.intramirror.web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.Helper;
import com.intramirror.common.parameter.EnabledType;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.model.PriceChangeRuleGroup;
import com.intramirror.product.api.model.PriceChangeRuleProduct;
import com.intramirror.product.api.model.PriceChangeRuleSeasonGroup;
import com.intramirror.product.api.model.Shop;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.IPriceChangeRuleGroupService;
import com.intramirror.product.api.service.IPriceChangeRuleProductService;
import com.intramirror.product.api.service.IPriceChangeRuleSeasonGroupService;
import com.intramirror.product.api.service.IShopService;
import com.intramirror.product.api.service.price.IPriceChangeRule;

@Service
public class PriceChangeRuleService {

	@Autowired
	private IPriceChangeRuleCategoryBrandService priceChangeRuleCategoryBrandService;
	
	@Autowired
	private IPriceChangeRule priceChangeRuleService;
	
	@Autowired
	private IPriceChangeRuleProductService priceChangeRuleProductService;
	
	@Autowired
	private IPriceChangeRuleGroupService priceChangeRuleGroupService;
	
	@Autowired
	IPriceChangeRuleSeasonGroupService priceChangeRuleSeasonGroupService;
	
	@Autowired
	IShopService shopService;
	
	
	
	
	
	/**
	 * 初始化 PriceChangeRule
	 * @return
	 * @throws ParseException 
	 */
	@Transactional  
	public Map<String,Object> initPriceChangeRule(Map<String,Object> map) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", map.get("userId"));
		param.put("enabled", 1);
		
		Shop shop = shopService.selectByParameter(param);
		
		//添加PriceChangeRule
		PriceChangeRule priceChangeRule = new PriceChangeRule();

		priceChangeRule.setName(map.get("name").toString());
		priceChangeRule.setPriceType(Byte.valueOf(map.get("price_type").toString()));
		priceChangeRule.setVendorId(0l);
		if(shop != null){
			priceChangeRule.setShopId(shop.getShopId());
		}
	
	    priceChangeRule.setStatus(Integer.parseInt(map.get("status").toString()));

	    priceChangeRuleService.insertSelective(priceChangeRule);
	    if(priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0 ){
	    	result.put("info", "add priceChangeRule fail");
	    	return result;
	    }
	    
	    //添加price_change_rule_season_group
	    JsonArray priceChangeRuleSeasonGroupArray = new JsonParser().parse(map.get("price_change_rule_season_group_List").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleSeasonGroupArray.size(); i++) {
	    	PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroupInfo = new PriceChangeRuleSeasonGroup();
	    	priceChangeRuleSeasonGroupInfo.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
	    	priceChangeRuleSeasonGroupInfo.setName(priceChangeRule.getName());
	    	priceChangeRuleSeasonGroupInfo.setSeasonCode(priceChangeRuleSeasonGroupArray.get(i).getAsString());
	    	priceChangeRuleSeasonGroupInfo.setEnabled(1);
	    	Date currentDate = Helper.getCurrentTimeToUTCWithDate();
	    	priceChangeRuleSeasonGroupInfo.setCreatedAt(currentDate);
	    	priceChangeRuleSeasonGroupInfo.setUpdatedAt(currentDate);
	    	priceChangeRuleSeasonGroupService.insertSelective(priceChangeRuleSeasonGroupInfo);
	    }
	   
	    
	    result.put("status", StatusType.SUCCESS);
	    return result;
	}
	
	
	/**
	 * 创建 PriceChangeRule
	 * @return
	 * @throws ParseException 
	 */
	@Transactional  
	public Map<String,Object> createPriceChangeRule(Map<String,Object> map) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		/**-------------------------------------PriceChangeRule-------------------------------------------------*/
		PriceChangeRule priceChangeRule = new PriceChangeRule();

		priceChangeRule.setName(map.get("name").toString());
		priceChangeRule.setPriceType(Byte.valueOf(map.get("price_type").toString()));
		priceChangeRule.setVendorId(0l);
		priceChangeRule.setShopId(null);
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    priceChangeRule.setValidFrom(simpleDateFormat.parse(map.get("valid_from").toString()));
	    priceChangeRule.setStatus(Integer.parseInt(map.get("status").toString()));

	    
	    priceChangeRuleService.insertSelective(priceChangeRule);
	    if(priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0 ){
	    	result.put("info", "add priceChangeRule fail");
	    	return result;
	    }
		
		
	    
		/**-------------------------------------priceChangeRuleCategoryBrand-----------------------------------*/
	    JsonArray priceChangeRuleAllBrandListArray = new JsonParser().parse(map.get("price_change_rule_all_brand_list").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleAllBrandListArray.size(); i++) {
	    	
			PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
			priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
			priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("category_id").getAsLong());
			priceChangeRuleCategoryBrand.setLevel(Byte.valueOf("2"));
			priceChangeRuleCategoryBrand.setBrandId(0l);
			priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());
			priceChangeRuleCategoryBrand.setExceptionFlag(0);

			priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);
			
	        if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            throw new RuntimeException("error");
	        }
	    }

	    
	    
		/**-------------------------------------priceChangeRuleCategoryBrand  EXCEPTIONS -----------------------*/
	    JsonArray priceChangeRuleCategoryListArray = new JsonParser().parse(map.get("price_change_rule_category_brand_list").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleCategoryListArray.size(); i++) {
	        PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
	        priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
	        priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("category_id").getAsLong());
	        priceChangeRuleCategoryBrand.setLevel(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("level").getAsByte());
	        priceChangeRuleCategoryBrand.setBrandId(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("brand_id").getAsLong());
//	        priceChangeRuleCategoryBrand.setSeasonCode(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("season_code").getAsString());
	        priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());
	        priceChangeRuleCategoryBrand.setExceptionFlag (1);

			priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);
			
	        if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            throw new RuntimeException("error");
	        }
	    }
	    
	    
	    
	    
		/**-------------------------------------priceChangeRuleProduct ---------------- -----------------------*/
	    JsonArray priceChangeRuleProductListArray = new JsonParser().parse(map.get("price_change_rule_product_list").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleProductListArray.size(); i++) {
	        PriceChangeRuleProduct priceChangeRuleProduct = new PriceChangeRuleProduct();
	        priceChangeRuleProduct.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
	        priceChangeRuleProduct.setProductId(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("product_id").getAsLong());
	        priceChangeRuleProduct.setBoutiqueId(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("product_code").getAsString());
	        priceChangeRuleProduct.setProductName(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("product_name").getAsString());
	        priceChangeRuleProduct.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleProductListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());

	        priceChangeRuleProductService.insertSelective(priceChangeRuleProduct);
	        if (priceChangeRuleProduct.getPriceChangeRuleProductId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            throw new RuntimeException("error");
	        }
	    }

	    
	    

		/**-------------------------------------priceChangeRuleGroup ---------------- -----------------------*/
	    JsonArray priceChangeRuleGroupListArray = new JsonParser().parse(map.get("price_change_rule_group_list").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleGroupListArray.size(); i++) {
	        PriceChangeRuleGroup priceChangeRuleGroup = new PriceChangeRuleGroup();
	        priceChangeRuleGroup.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
	        priceChangeRuleGroup.setProductGroupId(priceChangeRuleGroupListArray.get(i).getAsJsonObject().get("product_group_id").getAsLong());
	        priceChangeRuleGroup.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleGroupListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());

	       priceChangeRuleGroupService.insertSelective(priceChangeRuleGroup);
	        if (priceChangeRuleGroup.getPriceChangeRuleGroupId() == null) {
	            result.put("status", StatusType.DATABASE_ERROR);
	            throw new RuntimeException("error");
	        }
	    }
	    
	    result.put("status", StatusType.SUCCESS);
	    return result;
	}
	
	
	/**
	 * 修改PriceChangeRul
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Transactional  
	public Map<String,Object> updatePriceChangeRule(Map<String,Object> map) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		/**-------------------------------------PriceChangeRule-------------------------------------------------*/
		PriceChangeRule priceChangeRule = new PriceChangeRule();
		priceChangeRule.setPriceChangeRuleId(Long.valueOf(map.get("price_change_rule_id").toString()));
		priceChangeRule.setName(map.get("name").toString());
		priceChangeRule.setVendorId(Long.valueOf(map.get("vendor_id").toString()));
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    priceChangeRule.setValidFrom(simpleDateFormat.parse(map.get("valid_from").toString()));
	    PriceChangeRule oldPriceChangeRule = priceChangeRuleService.selectByPrimaryKey(priceChangeRule.getPriceChangeRuleId());
	    
	    //修改之前的状态
        if (oldPriceChangeRule.getStatus() == 3) {
        	 priceChangeRule.setStatus(1);
        }

	    
	    int resultNum = priceChangeRuleService.updateByPrimaryKeySelective(priceChangeRule);
//	    if(priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0 ){
//	    	result.put("info", "add priceChangeRule fail");
//	    	return result;
//	    }
		
		
	    
		/**-------------------------------------priceChangeRuleCategoryBrand-----------------------------------*/
	    JsonArray priceChangeRuleAllBrandListArray = new JsonParser().parse(map.get("price_change_rule_all_brand_list").toString()).getAsJsonArray();

	    for (int i = 0; i < priceChangeRuleAllBrandListArray.size(); i++) {
	    	
			PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
			priceChangeRuleCategoryBrand.setPriceChangeRuleCategoryBrandId(priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("price_change_rule_category_brand_id").getAsLong());
			priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());

			priceChangeRuleCategoryBrandService.updateByPrimaryKeySelective(priceChangeRuleCategoryBrand);
			
//	        if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
//	            result.put("status", StatusType.DATABASE_ERROR);
//	            throw new RuntimeException("error");
//	        }
	    }
		
		
	    result.put("status", StatusType.SUCCESS);
	    return result;
	}
	

    
}
