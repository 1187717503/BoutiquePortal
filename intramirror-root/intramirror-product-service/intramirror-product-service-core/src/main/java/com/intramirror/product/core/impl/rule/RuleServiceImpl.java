package com.intramirror.product.core.impl.rule;

import com.intramirror.product.api.service.rule.IRuleService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SeasonMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dingyifan on 2017/7/20.
 */
@Service(value = "productRuleServiceImpl")
public class RuleServiceImpl extends BaseDao implements IRuleService {

    private SeasonMapper seasonMapper;

    @Override
    public void init() {
        seasonMapper = this.getSqlSession().getMapper(SeasonMapper.class);
    }

    @Override
    public List<Map<String, Object>> queryRuleByHasSeason(Map<String, Object> params) throws Exception {
        List<Map<String,Object>> seasonMaps = seasonMapper.queryRuleByHasSeason(params);
        List<Map<String,Object>> handleMaps = new ArrayList<>();
        for(Map<String,Object> seasonMap : seasonMaps) {
            String name = seasonMap.get("name").toString();

            boolean flag = true;
            for(Map<String,Object> handleMap : handleMaps) {
                String hName = handleMap.get("name") == null ? "" : handleMap.get("name").toString();
                if(name.equals(hName)) {
                    List<Object> objectList = (List<Object>) handleMap.get("season_codes");
                    objectList.add(seasonMap.get("season_code"));
                    handleMap.put("season_codes",objectList);
                    flag = false;
                }
            }
            if(flag) {
                Map<String,Object> map = new HashMap<>();
                List<Object> stringList = new ArrayList<>();
                stringList.add(seasonMap.get("season_code"));
                map.put("name",name);
                map.put("season_codes",stringList);
                map.put("price_change_rule_id",seasonMap.get("price_change_rule_id"));
                handleMaps.add(map);
            }
        }
        return handleMaps;
    }

    @Override
    public List<Map<String, Object>> queryRuleByNotHasSesaon(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByNotHasSesaon(params);
    }

    @Override
    public List<Map<String, Object>> queryRuleByBrand(Map<String, Object> params) throws Exception {
        List<Map<String,Object>> handleMaps = new ArrayList<>();
        List<Map<String,Object>> brandMaps = seasonMapper.queryRuleByBrandZero(params);
        if(brandMaps != null && brandMaps.size() > 0) {
            for(Map<String,Object> brandMap : brandMaps) {
                String brandName = brandMap.get("english_name").toString();
                boolean flag = true;
                for(Map<String,Object> handleMap : handleMaps){
                    String eName = handleMap.get("english_name") == null ? "" : handleMap.get("english_name").toString();
                    if(brandName.equals(eName)) {
                        handleMap.put(brandMap.get("category_id").toString(),brandMap.get("discount_percentage"));
                        flag = false;
                    }
                }

                if(flag) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("english_name",brandName);
                    map.put("brand_id",brandMap.get("brand_id"));
                    map.put("price_change_rule_category_brand_id",brandMap.get("price_change_rule_category_brand_id"));
                    map.put(brandMap.get("category_id").toString(),brandMap.get("discount_percentage"));
                    handleMaps.add(map);
                }
            }
        }
        return handleMaps;
    }

    @Override
    public List<Map<String, Object>> queryRuleByBrandOne(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByBrandOne(params);
    }

    @Override
    public List<Map<String, Object>> queryNotRuleByBrand(Map<String, Object> params) throws Exception {
        List<Map<String,Object>> brands = seasonMapper.queryNotRuleByBrand(params);
        Collections.sort(brands, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Map<String, Object> map1 = (Map<String, Object>) o1;
                Map<String, Object> map2 = (Map<String, Object>) o2;
                String name1 = map1.get("english_name").toString().substring(0, 1);
                String name2 = map2.get("english_name").toString().substring(0, 1);
                return name1.compareTo(name2);
            }
        });
        return brands;
    }

    @Override
    public List<Map<String, Object>> queryRuleByGroup(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByGroup(params);
    }

    @Override
    public List<Map<String, Object>> queryRuleByProduct(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByProduct(params);
    }
}