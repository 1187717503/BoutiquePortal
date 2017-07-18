package com.intramirror.web.controller.price;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;
import com.intramirror.product.api.service.IPriceChangeRuleCategoryBrandService;
import com.intramirror.product.api.service.price.IPriceChangeRule;

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
	private IPriceChangeRule priceChangeRuleService;
	

	@SuppressWarnings("unchecked")
	@RequestMapping("/create")
	@ResponseBody
	public Map<String, Object> priceChangeRuleCreate(@RequestBody Map<String, Object> map){
		logger.info("priceChangeRuleCreate param:"+new Gson().toJson(map));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", StatusType.FAILURE);
		
		
		/**-------------------------------------PriceChangeRule-------------------------------------------------*/
		PriceChangeRule priceChangeRule = new PriceChangeRule();
	    try {
	    	priceChangeRule.setName(map.get("name").toString());
	    	priceChangeRule.setPriceType(Byte.valueOf(map.get("price_type").toString()));
	    	priceChangeRule.setVendorId(0l);
	    	priceChangeRule.setShopId(null);
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        priceChangeRule.setValidFrom(simpleDateFormat.parse(map.get("valid_from").toString()));
	        priceChangeRule.setStatus(Integer.parseInt(map.get("status").toString()));
		} catch (ParseException e) {
			e.printStackTrace();
			result.put("info", "参数异常");
			return result;
		}
        
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
                return result;
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
//            priceChangeRuleCategoryBrand.setSeasonCode(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("season_code").getAsString());
            priceChangeRuleCategoryBrand.setDiscountPercentage(Long.valueOf("100") - priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("discount_percentage").getAsLong());
            priceChangeRuleCategoryBrand.setExceptionFlag (1);

    		priceChangeRuleCategoryBrandService.createPriceChangeRuleCategoryBrand(priceChangeRuleCategoryBrand);
    		
            if (priceChangeRuleCategoryBrand.getPriceChangeRuleCategoryBrandId() == null) {
                result.put("status", StatusType.DATABASE_ERROR);
                return result;
            }
        }
		
		
		return result;
	}
	
	
    /**
     * 检查参数
     */
    public static int checkParams(Map<String, String> params) {
    	
//    	if(params.get("name")){
//    		
//    	}
//
//        checker.add("name", params.get("name")).required().maxLength(256);
//
//        checker.add("price_type", params.get("price_type")).required().maxLength(64).numeric();
//
//        //checker.add("season_code", params.get("season_code")).required().json();
//
//        checker.add("valid_from", params.get("valid_from")).required().date();
//
//        checker.add("status", params.get("status")).required().maxLength(64).numeric();
//
//        checker.add("price_change_rule_category_brand_list", params.get("price_change_rule_category_brand_list")).required().json();
//
//        checker.add("price_change_rule_all_brand_list", params.get("price_change_rule_all_brand_list")).required().json();
//
//        checker.add("price_change_rule_product_list", params.get("price_change_rule_product_list")).required().json();
//
//        checker.add("price_change_rule_group_list", params.get("price_change_rule_group_list")).required().json();
//
//        Reports reports = checker.check();

//        return reports.firstError().toStatus();
    	return 0;
    }
	
}