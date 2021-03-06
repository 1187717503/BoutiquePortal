package com.intramirror.product.api.service;

import com.intramirror.product.api.model.PriceChangeRuleSeasonGroup;

import java.util.List;

public interface IPriceChangeRuleSeasonGroupService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer priceChangeRuleSeasonGroupId);



    /**
     * 根据priceChangeRuleId 删除
     * @param priceChangeRuleId
     * @return
     */
    int deleteByPriceChangeRuleId(Long priceChangeRuleId);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    int insert(PriceChangeRuleSeasonGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    int insertSelective(PriceChangeRuleSeasonGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    PriceChangeRuleSeasonGroup selectByPrimaryKey(Integer priceChangeRuleSeasonGroupId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PriceChangeRuleSeasonGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table price_change_rule_season_group
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PriceChangeRuleSeasonGroup record);

    List<PriceChangeRuleSeasonGroup> getPriceChangeRuleGroupListByPriceChangeRuleIdAndSeasonCode(Long priceChangeRuleId, String seasonCode);
    List<PriceChangeRuleSeasonGroup> getPriceChangeRuleGroupListByPriceChangeRuleId(Long priceChangeRuleId);
}
