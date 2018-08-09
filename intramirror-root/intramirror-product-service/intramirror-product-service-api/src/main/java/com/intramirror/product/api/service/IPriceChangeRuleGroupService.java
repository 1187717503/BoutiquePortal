package com.intramirror.product.api.service;

import com.intramirror.product.api.model.PriceChangeRuleGroup;

import java.util.List;
import java.util.Map;

public interface IPriceChangeRuleGroupService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_group
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long priceChangeRuleGroupId);
    
    
    /**
     * 根据priceChangeRuleId 删除
     * @param priceChangeRuleId
     * @return
     */
    int deleteByPriceChangeRuleId(Long priceChangeRuleId);
    

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_group
     *
     * @mbggenerated
     */
    int insert(PriceChangeRuleGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_group
     *
     * @mbggenerated
     */
    int insertSelective(PriceChangeRuleGroup record);

    List<PriceChangeRuleGroup> getPriceChangeRuleGroupListByPriceChangeRuleId(Long priceChangeRuleId);
    
    
    
    /**
     * 根据priceChangeRuleId 查询
     * @param priceChangeRuleId
     * @return
     */
    List<PriceChangeRuleGroup> selectByPriceChangeRuleId(Long priceChangeRuleId);
    
    /**
     * 根据条件 查询
     * @param record
     * @return
     */
    List<PriceChangeRuleGroup>  selectByParameter(PriceChangeRuleGroup record);

    PriceChangeRuleGroup selectByPrimaryKey(Long id);

    List<PriceChangeRuleGroup> getChangeRulesByTagId(Long tagId);
}
