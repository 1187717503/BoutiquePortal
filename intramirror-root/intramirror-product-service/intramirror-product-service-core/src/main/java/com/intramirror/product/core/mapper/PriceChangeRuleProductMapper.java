package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.PriceChangeRuleProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PriceChangeRuleProductMapper {
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

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    PriceChangeRuleProduct selectByPrimaryKey(Long priceChangeRuleProductId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PriceChangeRuleProduct record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_product
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PriceChangeRuleProduct record);

    List<PriceChangeRuleProduct> getPriceChangeRuleGroupListByPriceChangeRuleId(@Param("priceChangeRuleId") Long priceChangeRuleId);
}