package com.intramirror.product.api.service.rule;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
public interface IRuleService {

    /** start im rule page select */
    List<Map<String,Object>> queryRuleByHasSeason(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByNotHasSesaon(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByBrand(Map<String,Object> params) throws Exception;
    List<Map<String, Object>> queryRuleByBrandOne(Map<String, Object> params) throws Exception;
    List<Map<String,Object>> queryNotRuleByBrand(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryAllBrand() throws Exception;
    List<Map<String,Object>> queryRuleByGroup(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByProduct(Map<String,Object> params) throws Exception;
    boolean changeRule(String price_change_rule_id,Map<String, List<Map<String,Object>>> map) throws Exception;

    Map<String,Object> getSnapShotUpdateTime(Object ruleId,Map<String,Object> map) throws Exception;
    /** end im rule page select */

}
