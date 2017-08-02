package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PriceChangeRule;

import java.util.List;
import java.util.Map;

public interface PriceChangeRuleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long priceChangeRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    int insert(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    int insertSelective(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId);
    
    
    /**
     * 根据vendorId name 模糊查询
     * @param priceChangeRuleId
     * @return
     */
    List<PriceChangeRule> selectByName(Map<String,Object> map);
    

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PriceChangeRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PriceChangeRule record);

    int updatePriceChangeRuleByVendorFirstCategory();
    int updatePriceChangeRuleByVendorAllCategory();
    int updatePriceChangeRuleByVendorProductGroup();
    int updatePriceChangeRuleByVendorProduct();

    int updatePriceChangeRuleByShopFirstCategory();
    int updatePriceChangeRuleByShopAllCategory();
    int updatePriceChangeRuleByShopProductGroup();
    int updatePriceChangeRuleByShopProduct();

    List<Map<String,Object>> selectActiveSeasonGroupRule(Map<String,Object> params) ;
    List<Map<String,Object>> selectSeasonGroupRule(Map<String,Object> params) ;
    List<Map<String,Object>> selectSecondCategoryRule(Map<String,Object> params) ;
    List<Map<String,Object>> selectAllCategoryRule(Map<String,Object> params);
    List<Map<String,Object>> selectProductGroupRule(Map<String,Object> params);
    List<Map<String,Object>> selectProductRule(Map<String,Object> params);

    int updateSkuPriceByVendor(List<Map<String,Object>> paramsList);
    int updateSkuPriceByAdmin(List<Map<String,Object>> paramsList);
    int updateSkuPriceByShop(List<Map<String,Object>> paramsList);


    List<Map<String,Object>> selNowRule(Map<String,Object> params);
    List<Map<String,Object>> selRuleByVendorPriceType(Map<String,Object> params);
    int updateRuleActive(Map<String,Object> params);
    int updateRuleInActive(Map<String,Object> params);

    int updateDefaultPriceByVendor(Map<String,Object> params);
    int updateDefaultPriceByAdmin(Map<String,Object> params);

    int updateSkuPriceByImPrice();

}