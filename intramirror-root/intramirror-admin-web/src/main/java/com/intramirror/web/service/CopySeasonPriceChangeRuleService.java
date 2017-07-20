package com.intramirror.web.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.product.api.model.*;
import com.intramirror.product.api.service.*;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.user.api.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CopySeasonPriceChangeRuleService {

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

    @Autowired
    VendorService vendorService;

    @Autowired
    private IPriceChangeRule iPriceChangeRule;

    /**
     * 创建 PriceChangeRule
     *
     * @return
     * @throws ParseException
     */
    @Transactional
    public Map<String, Object> copySeasonPriceChangeRule(Map<String, Object> map) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);
        JsonArray seasonGroupList = new JsonParser().parse(map.get("season_group_list").toString()).getAsJsonArray();

        /**-------------------------------------PriceChangeRule-------------------------------------------------*/
        PriceChangeRule priceChangeRule = new PriceChangeRule();

        if (seasonGroupList.size() > 1) {
            priceChangeRule.setName("multiple");
        } else {
            priceChangeRule.setName(seasonGroupList.get(0).getAsString());
        }

        priceChangeRule.setPriceType(Byte.valueOf(map.get("price_type").toString()));
        priceChangeRule.setVendorId(Long.valueOf(map.get("vendor_id").toString()));
        priceChangeRule.setShopId(null);
        priceChangeRule.setStatus(Integer.parseInt(map.get("status").toString()));

        priceChangeRuleService.insertSelective(priceChangeRule);
        if (priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0) {
            result.put("info", "add priceChangeRule fail");
            return result;
        }


        /**-------------------------------------priceChangeRuleAllBrand-----------------------------------*/
        JsonArray priceChangeRuleAllBrandListArray = new JsonParser().parse(map.get("price_change_rule_all_brand_list").toString()).getAsJsonArray();

        for (int i = 0; i < priceChangeRuleAllBrandListArray.size(); i++) {

            PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand = new PriceChangeRuleCategoryBrand();
            priceChangeRuleCategoryBrand.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
            priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("categoryId").getAsLong());
            priceChangeRuleCategoryBrand.setLevel(Byte.valueOf("2"));
            priceChangeRuleCategoryBrand.setBrandId(0l);
            priceChangeRuleCategoryBrand.setDiscountPercentage(priceChangeRuleAllBrandListArray.get(i).getAsJsonObject().get("discountPercentage").getAsLong());
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
            priceChangeRuleCategoryBrand.setCategoryId(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("categoryId").getAsLong());
            priceChangeRuleCategoryBrand.setLevel(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("level").getAsByte());
            priceChangeRuleCategoryBrand.setBrandId(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("brandId").getAsLong());
            priceChangeRuleCategoryBrand.setDiscountPercentage(priceChangeRuleCategoryListArray.get(i).getAsJsonObject().get("discountPercentage").getAsLong());
            priceChangeRuleCategoryBrand.setExceptionFlag(1);

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
            priceChangeRuleProduct.setProductId(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("productId").getAsLong());
            priceChangeRuleProduct.setBoutiqueId(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("boutiqueId").getAsString());
            priceChangeRuleProduct.setProductName(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("productName").getAsString());
            priceChangeRuleProduct.setDiscountPercentage(priceChangeRuleProductListArray.get(i).getAsJsonObject().get("discountPercentage").getAsLong());

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
            priceChangeRuleGroup.setProductGroupId(priceChangeRuleGroupListArray.get(i).getAsJsonObject().get("productGroupId").getAsLong());
            priceChangeRuleGroup.setDiscountPercentage(priceChangeRuleGroupListArray.get(i).getAsJsonObject().get("discountPercentage").getAsLong());

            priceChangeRuleGroupService.insertSelective(priceChangeRuleGroup);
            if (priceChangeRuleGroup.getPriceChangeRuleGroupId() == null) {
                result.put("status", StatusType.DATABASE_ERROR);
                throw new RuntimeException("error");
            }
        }

        result.put("price_change_rule_id", priceChangeRule.getPriceChangeRuleId());
        result.put("status", StatusType.SUCCESS);
        return result;
    }

    /**
     * 创建 PriceChangeRuleSeasonCode
     *
     * @return
     * @throws ParseException
     */
    @Transactional
    public Map<String, Object> copyPriceChangeRuleSeasonCode(Map<String, Object> map) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", StatusType.FAILURE);
        PriceChangeRule priceChangeRule = iPriceChangeRule.selectByPrimaryKey(Long.valueOf(map.get("price_change_rule_id").toString()));

        /**-------------------------------------priceChangeRuleSeasonGroup-----------------------------------*/
        JsonArray priceChangeRuleSeasonGroupArray = new JsonParser().parse(map.get("season_group_list").toString()).getAsJsonArray();
        if (priceChangeRuleSeasonGroupArray.size() > 1) {
            for (int i = 0; i < priceChangeRuleSeasonGroupArray.size(); i++) {
                PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroupInfo = new PriceChangeRuleSeasonGroup();
                priceChangeRuleSeasonGroupInfo.setName("multiple" + priceChangeRule.getPriceChangeRuleId());
                priceChangeRuleSeasonGroupInfo.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
                priceChangeRuleSeasonGroupInfo.setSeasonCode(priceChangeRuleSeasonGroupArray.get(i).getAsString());
                priceChangeRuleSeasonGroupInfo.setEnabled(1);
                Date currentDate = new Date();
                priceChangeRuleSeasonGroupInfo.setCreatedAt(currentDate);
                priceChangeRuleSeasonGroupInfo.setUpdatedAt(currentDate);
                priceChangeRuleSeasonGroupService.insertSelective(priceChangeRuleSeasonGroupInfo);
                if (priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0) {
                    result.put("info", "add priceChangeRule list fail");
                    return result;
                }
            }
        } else {
            PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroupInfo = new PriceChangeRuleSeasonGroup();
            priceChangeRuleSeasonGroupInfo.setName(priceChangeRuleSeasonGroupArray.get(0).getAsString());
            priceChangeRuleSeasonGroupInfo.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
            priceChangeRuleSeasonGroupInfo.setSeasonCode(priceChangeRuleSeasonGroupArray.get(0).getAsString());
            priceChangeRuleSeasonGroupInfo.setEnabled(1);
            Date currentDate = new Date();
            priceChangeRuleSeasonGroupInfo.setCreatedAt(currentDate);
            priceChangeRuleSeasonGroupInfo.setUpdatedAt(currentDate);
            priceChangeRuleSeasonGroupService.insertSelective(priceChangeRuleSeasonGroupInfo);
            if (priceChangeRule.getPriceChangeRuleId() == null || priceChangeRule.getPriceChangeRuleId() == 0) {
                result.put("info", "add priceChangeRule fail");
                return result;
            }
        }
        result.put("status", StatusType.SUCCESS);
        return result;
    }

}
