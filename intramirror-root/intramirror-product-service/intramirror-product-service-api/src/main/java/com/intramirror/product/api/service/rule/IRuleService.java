package com.intramirror.product.api.service.rule;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
public interface IRuleService {

    List<Map<String,Object>> queryRuleByHasSeason(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByNotHasSesaon(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByBrand(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByGroup(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleByProduct(Map<String,Object> params) throws Exception;

}
