package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;

public interface PriceChangeRuleCategoryBrandMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long priceChangeRuleCategoryBrandId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    int insert(PriceChangeRuleCategoryBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    Long insertSelective(PriceChangeRuleCategoryBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    PriceChangeRuleCategoryBrand selectByPrimaryKey(Long priceChangeRuleCategoryBrandId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PriceChangeRuleCategoryBrand record);
    
    
    /**
     * 根据parameter 修改折扣信息
     *
     * @mbggenerated
     */
    int updateDiscountPercentageBySelective(PriceChangeRuleCategoryBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_category_brand
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PriceChangeRuleCategoryBrand record);
}