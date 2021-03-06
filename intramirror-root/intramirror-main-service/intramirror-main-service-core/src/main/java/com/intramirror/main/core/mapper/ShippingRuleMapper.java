package com.intramirror.main.core.mapper;

import com.intramirror.main.api.model.ShippingRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShippingRuleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long shippingRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    int insert(ShippingRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    int insertSelective(ShippingRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    ShippingRule selectByPrimaryKey(Long shippingRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ShippingRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shipping_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ShippingRule record);

    List<Map<String, Object>> getShippingFeeByProductIds(@Param("productIds") String[] productIds,@Param("toCountry") Integer toCountry);
}