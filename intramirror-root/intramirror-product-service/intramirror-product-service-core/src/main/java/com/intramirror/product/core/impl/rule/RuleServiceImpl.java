package com.intramirror.product.core.impl.rule;

import com.google.gson.Gson;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.SnapshotPriceRule;
import com.intramirror.product.api.service.ISnapshotPriceRuleService;
import com.intramirror.product.api.service.rule.IRuleService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SeasonMapper;
import com.intramirror.product.core.mapper.SnapshotPriceRuleMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dingyifan on 2017/7/20.
 */
@Service(value = "productRuleServiceImpl")
public class RuleServiceImpl extends BaseDao implements IRuleService {

    private SeasonMapper seasonMapper;
    private SnapshotPriceRuleMapper snapshotPriceRuleMapper;;

    @Autowired
    private ISnapshotPriceRuleService snapshotPriceRuleService;

    private static Logger logger = LoggerFactory.getLogger(RuleServiceImpl.class);

    @Override
    public void init() {
        seasonMapper = this.getSqlSession().getMapper(SeasonMapper.class);
        snapshotPriceRuleMapper = this.getSqlSession().getMapper(SnapshotPriceRuleMapper.class);
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
                //根据price_change_rule_id查找im_price_algorithm_id 然后 根据id查找name
                Map<String,Object>  algorithmMap= seasonMapper.queryImPriceAlgorithm(seasonMap.get("price_change_rule_id"));
                if(algorithmMap != null){
                    map.put("im_price_algorithm_name",algorithmMap.get("name"));
                    map.put("im_price_algorithm_id",algorithmMap.get("im_price_algorithm_id"));
                }else{
                    map.put("im_price_algorithm_name","");
                    map.put("im_price_algorithm_id","");
                }
                //snapshot  update_at ||  refresh IM
                map = this.getSnapShotUpdateTime(seasonMap.get("price_change_rule_id"),map);
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
        List<Map<String,Object>> newMaps = new ArrayList<>();
        if(brandMaps != null && brandMaps.size() > 0) {
            for(Map<String,Object> brandMap : brandMaps) {
                String englishName = brandMap.get("english_name") == null ? "" : brandMap.get("english_name").toString();
                String brandId = brandMap.get("brand_id").toString();
                if(StringUtils.isBlank(englishName) && !brandId.equals("0")) {
                    logger.info("englishName is null !!!{}",new Gson().toJson(brandMap));
                    continue;
                }
                boolean flag = true;
                for(Map<String,Object> handleMap : handleMaps){
                    String eName = handleMap.get("brand_id") == null ? "" : handleMap.get("brand_id").toString();
                    if(brandId.equals(eName)) {
                        handleMap.put(brandMap.get("category_id").toString(),brandMap.get("discount_percentage"));
                        flag = false;
                    }
                }

                if(flag) {
                    Map<String,Object> map = new HashMap<>();
                    if(brandId.equals("0")) {
                        englishName = "Default";
                    }
                    map.put("english_name",englishName);
                    map.put("brand_id",brandId);
//                    map.put("price_change_rule_category_brand_id",brandMap.get("price_change_rule_category_brand_id"));
                    map.put(brandMap.get("category_id").toString(),brandMap.get("discount_percentage"));
                    handleMaps.add(map);
                }
            }

            if(handleMaps != null && handleMaps.size() > 0) {
                for(Map<String,Object> brandMap : handleMaps) {
                    String brandName = brandMap.get("english_name") == null ? "" : brandMap.get("english_name").toString();
                    if(brandName.equals("Default")) {
                        newMaps.add(brandMap);
                        break;
                    }
                }

                for(Map<String,Object> brandMap : handleMaps) {
                    String brandName = brandMap.get("english_name") == null ? "" : brandMap.get("english_name").toString();
                    if(!brandName.equals("Default")) {
                        newMaps.add(brandMap);
                    }
                }
            }

        }
        return newMaps;
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
    public List<Map<String, Object>> queryAllBrand() throws Exception {
        return seasonMapper.queryAllBrand();
    }

    @Override
    public List<Map<String, Object>> queryRuleByGroup(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByGroup(params);
    }

    @Override
    public List<Map<String, Object>> queryRuleByProduct(Map<String, Object> params) throws Exception {
        return seasonMapper.queryRuleByProduct(params);
    }

    @Transactional
    @Override
    public boolean changeRule(String price_change_rule_id, Map<String, List<Map<String, Object>>> sheetExcelData) throws Exception {
        List<Map<String, Object>> categoryBrandMapList = sheetExcelData.get("priceRuleList");
        List<Map<String, Object>> categoryBrandExceptionMapList = sheetExcelData.get("categoryBrandList");
        List<Map<String, Object>> productGroupList = sheetExcelData.get("productGroupList");
        List<Map<String, Object>> productList = sheetExcelData.get("productList");
        seasonMapper.deleteCategoryBrandRule(price_change_rule_id);
        Collections.reverse(categoryBrandMapList);
        for (Map<String, Object> params : categoryBrandMapList) {
            seasonMapper.insertCategoryBrandRule(params);
        }
        if (CollectionUtils.isNotEmpty(categoryBrandExceptionMapList)) {
            seasonMapper.deleteCategoryBrandRuleException(price_change_rule_id);
            Collections.reverse(categoryBrandExceptionMapList);
            for (Map<String, Object> params : categoryBrandExceptionMapList) {
                seasonMapper.insertCategoryBrandRuleException(params);
            }
        }
        if (CollectionUtils.isNotEmpty(productGroupList)) {
            seasonMapper.deleteProductGroupRule(price_change_rule_id);
            Collections.reverse(productGroupList);
            for (Map<String, Object> params : productGroupList) {
                seasonMapper.insertProductGroupRule(params);
            }
        }
        if (CollectionUtils.isNotEmpty(productList)) {
            seasonMapper.deleteProductRule(price_change_rule_id);
            Collections.reverse(productList);
            for (Map<String, Object> params : productList) {
                seasonMapper.insertProductRule(params);
            }
        }

        SnapshotPriceRule snapshotPriceRule = new SnapshotPriceRule();
        snapshotPriceRule.setPriceChangeRuleId(Long.parseLong(price_change_rule_id));
        snapshotPriceRule.setSaveAt(new Date());
        snapshotPriceRuleMapper.updateSaveAtByPriceChangeRuleId(snapshotPriceRule);
        return true;
    }

    @Override
    public Map<String,Object> getSnapShotUpdateTime(Object ruleId,Map<String,Object> map){
        Map<String,Object> snapShotRefreshTime = seasonMapper.querySnapShotTimeByRuleId(ruleId);
        if(snapShotRefreshTime != null){
            if(snapShotRefreshTime.get("updated_at") != null){
                map.put("updated_at",snapShotRefreshTime.get("updated_at").toString());
            }else{
                map.put("updated_at",null);
            }

            map.put("refresh_status",snapShotRefreshTime.get("refresh"));// 0  -> false 1 -> true 2->無狀態
        }else{
            map.put("updated_at",null);
            map.put("refresh_status",2);
        }
        return map;
    }
}
