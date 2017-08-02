package com.intramirror.product.api.service;

import com.intramirror.product.api.model.PriceChangeRuleCategoryBrand;

import java.util.List;
import java.util.Map;

public interface IPriceChangeRuleCategoryBrandService {

    /**
     * 添加
     *
     * @param priceChangeRuleCategoryBrand
     * @return
     */
    Long createPriceChangeRuleCategoryBrand(PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand);


    /**
     * 删除
     *
     * @param priceChangeRuleCategoryBrandId
     * @return
     */
    int deletePriceChangeRuleCategoryBrand(Long priceChangeRuleCategoryBrandId);
    
    
    /**
     * 根据priceChangeRuleId 删除
     * @param priceChangeRuleId
     * @return
     */
    int deleteByPriceChangeRuleId(Long priceChangeRuleId);


    /**
     * 根据条件删除 PriceChangeRuleCategoryBrand
     *
     * @param PriceChangeRuleCategoryBrand
     * @return
     */
    int deleteByParameter(PriceChangeRuleCategoryBrand record);


    /**
     * 根据ID查询
     *
     * @mbggenerated
     */
    PriceChangeRuleCategoryBrand selectByPrimaryKey(Long priceChangeRuleCategoryBrandId);


    /**
     * 根据ID修改
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

    List<Map<String,Object>> selectPriceChangeRuleCategoryBrandExists(Map<String,Object> map) throws Exception;

    List<PriceChangeRuleCategoryBrand> getPriceChangeRuleGroupListByPriceChangeRuleIdAndExceptionFlag(Long priceChangeRuleId, Integer exceptionFlag);

}
