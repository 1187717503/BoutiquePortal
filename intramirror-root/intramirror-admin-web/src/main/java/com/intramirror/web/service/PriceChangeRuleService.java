package com.intramirror.web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    
}
