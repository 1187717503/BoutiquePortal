package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Season;

import java.util.List;
import java.util.Map;

public interface SeasonMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String seasonCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    int insert(Season record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    int insertSelective(Season record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    Season selectByPrimaryKey(String seasonCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Season record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SeasonServiceImpl
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Season record);

    List<Map<String, Object>> queryRuleByHasSeason(Map<String, Object> params);

    List<Map<String, Object>> queryRuleByNotHasSesaon(Map<String, Object> params);

    List<Map<String, Object>> queryRuleByBrandZero(Map<String, Object> params);

    List<Map<String, Object>> queryRuleByBrandOne(Map<String, Object> params);

    List<Map<String, Object>> queryNotRuleByBrand(Map<String, Object> params);

    List<Map<String, Object>> queryAllBrand();

    List<Map<String, Object>> queryRuleByGroup(Map<String, Object> params);

    List<Map<String, Object>> queryRuleByProduct(Map<String, Object> params);

    List<Map<String, Object>> queryRuleByConditions(Map<String, Object> params);

    int copyPriceChangeRule(Map<String, Object> params);

    int copyPriceChangeRuleCategoryBrand(Map<String, Object> params);

    int copyPriceChangeRuleGroup(Map<String, Object> params);

    int copyPriceChangeRuleProduct(Map<String, Object> params);

    int copyPriceChangeRuleSeasonGroup(Map<String, Object> params);

    int deleteCategoryBrandRule(String price_change_rule_id);

    int insertCategoryBrandRule(Map<String,Object> params);

    List<Map<String, Object>> querySeasonByVendor(Map<String, Object> params);

    List<String> listAllSeasonCode();

    /**
     * 根据price_change_rule_id查找im_price_algorithm_id 然后 根据id查找name
     * @param priceChangeRuleId
     * @return
     */
    Map<String,Object> queryImPriceAlgorithm(Object priceChangeRuleId);

    int deleteCategoryBrandRuleException(String price_change_rule_id);

    int insertCategoryBrandRuleException(Map<String,Object> params);

    int deleteProductGroupRule(String price_change_rule_id);

    int insertProductGroupRule(Map<String,Object> params);

    int deleteProductRule(String price_change_rule_id);

    int insertProductRule(Map<String,Object> params);

    /**
     * 查找refresh时间
     * @param priceChangeRuleId
     * @return
     */
    Map<String,Object> querySnapShotTimeByRuleId(Object priceChangeRuleId);
}