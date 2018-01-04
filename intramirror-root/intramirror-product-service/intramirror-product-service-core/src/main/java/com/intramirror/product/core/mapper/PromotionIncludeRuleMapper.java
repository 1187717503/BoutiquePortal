package com.intramirror.product.core.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2018/1/4.
 * @author 123
 */
public interface PromotionIncludeRuleMapper {

    List<Map<String, Object>> listActivePromotion();

    List<Map<String, Object>> listExcludeRulePromotion(@Param(value = "bannerId") String bannerId);

    List<Map<String, Object>> listIncluedRulePromotion(@Param(value = "bannerId") String bannerId);

}

