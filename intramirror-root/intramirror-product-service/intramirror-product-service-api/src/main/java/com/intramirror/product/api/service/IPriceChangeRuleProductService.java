package com.intramirror.product.api.service;

import com.intramirror.product.api.model.PriceChangeRuleProduct;

import java.util.List;

public interface IPriceChangeRuleProductService {
	
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long priceChangeRuleProductId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    int insert(PriceChangeRuleProduct record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    int insertSelective(PriceChangeRuleProduct record);

    List<PriceChangeRuleProduct> getPriceChangeRuleGroupListByPriceChangeRuleId(Long priceChangeRuleId);
}
