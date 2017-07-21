package com.intramirror.product.core.impl.price;

import com.google.gson.Gson;
import com.intramirror.common.Contants;
import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleMapper;

import com.intramirror.product.core.mapper.SeasonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/17.
 */
@Service(value = "productPriceChangeRule")
public class PriceChangeRuleImpl extends BaseDao implements IPriceChangeRule {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleImpl.class);

    private PriceChangeRuleMapper priceChangeRuleMapper;

    private SeasonMapper seasonMapper;

    @Override
    public boolean updateVendorPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        selSecondCategoryRuleMaps = this.sortListByLevel(selSecondCategoryRuleMaps);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByVendor(paramsList,selSecondCategoryRuleMaps,Contants.num_second);
        paramsList = this.handleMapByVendor(paramsList,selAllCategoryRuleMaps,Contants.num_second);
        paramsList = this.handleMapByVendor(paramsList,selProductGroupRuleMaps,Contants.num_three);
        paramsList = this.handleMapByVendor(paramsList,selProductRuleMaps,Contants.num_four);

        if(paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);
            this.updateRuleStatus(paramsMap);
        }
        return true;
    }

    @Override
    public boolean updateShopPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());

        List<Map<String,Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("selSeasonGroupRuleMaps : {}",new Gson().toJson(selSeasonGroupRuleMaps));

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        selSecondCategoryRuleMaps = this.sortListByLevel(selSecondCategoryRuleMaps);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selProductGroupRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByAdmin(paramsList,selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);

        if(paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByShop(paramsList);
            this.updateRuleStatus(paramsMap);
        }
        return true;
    }

    public List<Map<String,Object>> sortListByLevel(List<Map<String,Object>> maps) {
        List<Map<String,Object>> sortMaps = new ArrayList<>();
        if(maps != null && maps.size() > 0) {
            for(Map<String,Object> map : maps) {
                String level = map.get("level").toString();
                if(level.equals("1")) {
                    sortMaps.add(map);
                }
            }
            for(Map<String,Object> map : maps) {
                String level = map.get("level").toString();
                if(level.equals("2")) {
                    sortMaps.add(map);
                }
            }
            for(Map<String,Object> map : maps) {
                String level = map.get("level").toString();
                if(level.equals("3")) {
                    sortMaps.add(map);
                }
            }
        }
        return sortMaps;
    }

    @Override
    public boolean updateAdminPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());

        List<Map<String,Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("selSeasonGroupRuleMaps : {}",new Gson().toJson(selSeasonGroupRuleMaps));

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        selSecondCategoryRuleMaps = this.sortListByLevel(selSecondCategoryRuleMaps);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selProductGroupRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByAdmin(paramsList,selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);

        if( paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByAdmin(paramsList);
            this.updateRuleStatus(paramsMap);
        }
        return true;
    }

    private List<Map<String,Object>> handleMapByVendor(List<Map<String,Object>> paramsList,List<Map<String,Object>> dataList,int level){
        if(dataList != null && dataList.size() > 0) {
            for (Map<String,Object> dataMap : dataList) {
                dataMap.put("handle_level",level);
                paramsList.add(dataMap);
            }
        }
        return paramsList;
    }

    private List<Map<String,Object>> handleMapByAdmin(List<Map<String,Object>> paramsList,List<Map<String,Object>> dataList,int level,List<Map<String,Object>> seasonGroupMaps){
        if(dataList != null && dataList.size() > 0) {
            for (Map<String,Object> dataMap : dataList) {
                dataMap.put("handle_level",level);
                if(seasonGroupMaps != null && seasonGroupMaps.size() > 0) {
                    String seasons = "";
                    for(Map<String,Object> seasonRuleMap : seasonGroupMaps) {
                        String p1 = dataMap.get("price_change_rule_id").toString();
                        String p2 = dataMap.get("price_change_rule_id").toString();
                        if(p1.equals(p2)) {
                            seasons = seasons + "'"+seasonRuleMap.get("season_code").toString()+"'" + ",";
                        }
                    }
                    if(StringUtils.isNotBlank(seasons)) {
                        String season_codes = seasons.substring(0,seasons.length() - 1);
                        dataMap.put("season_codes",season_codes);
                    }
                }
                paramsList.add(dataMap);
            }
        }
        return paramsList;
    }

    private boolean updateRuleStatus(Map<String,Object> params){
        List<Map<String,Object>> selNowRuleMaps = priceChangeRuleMapper.selNowRule(params);
        if(selNowRuleMaps != null && selNowRuleMaps.size() > 0) {
            priceChangeRuleMapper.updateRuleInActive(params);
        }
        priceChangeRuleMapper.updateRuleActive(params);
        return true;
    }
    @Override
    public void init() {
        priceChangeRuleMapper = this.getSqlSession().getMapper(PriceChangeRuleMapper.class);
        seasonMapper = this.getSqlSession().getMapper(SeasonMapper.class);
    }

	@Override
	public int deleteByPrimaryKey(Long priceChangeRuleId) {
		return priceChangeRuleMapper.deleteByPrimaryKey(priceChangeRuleId);
	}

	@Override
	public int insert(PriceChangeRule record) {
		return priceChangeRuleMapper.insert(record);
	}

	@Override
	public int insertSelective(PriceChangeRule record) {
		return priceChangeRuleMapper.insertSelective(record);
	}

	@Override
	public PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId) {
		return priceChangeRuleMapper.selectByPrimaryKey(priceChangeRuleId);
	}

	@Override
	public int updateByPrimaryKeySelective(PriceChangeRule record) {
		return priceChangeRuleMapper.updateByPrimaryKeySelective(record);
	}

    @Override
    public ResultMessage copyRule(Map<String, Object> params) {
        ResultMessage resultMessage = ResultMessage.getInstance();
        try {
            String vendor_id = params.get("vendor_id") == null ? "" : params.get("vendor_id").toString();
            String discount = params.get("discount") == null ? "0" : params.get("discount").toString();
            String status = params.get("status") == null ? "" : params.get("status").toString();
            params.put("price_type",PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
            List<Map<String,Object>> ruleByConditionsMaps =  seasonMapper.queryRuleByConditions(params);

            if(ruleByConditionsMaps == null || ruleByConditionsMaps.size() == 0) {
                return resultMessage.errorStatus().putMsg("info"," 没有规则 !!!");
            }
            this.copyAllRuleByActivePending(ruleByConditionsMaps,vendor_id,discount);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : {}",e.getMessage());
            resultMessage.errorStatus().putMsg("info","error message : " + e.getMessage());
        }
        return resultMessage;
    }

    private void copyAllRuleByActivePending(List<Map<String,Object>> ruleByConditionsMaps,String vendor_id,String discount){
        for(Map<String,Object> map : ruleByConditionsMaps){
            String price_change_rule_id = map.get("price_change_rule_id").toString();
            String name = map.get("name").toString();

            /** start copy price_change_rule */
            PriceChangeRule priceChangeRule = new PriceChangeRule();
            priceChangeRule.setName(name);
            priceChangeRule.setPriceType(Byte.parseByte(PriceChangeRuleEnum.PriceType.IM_PRICE.getCode() + ""));
            priceChangeRule.setStatus(PriceChangeRuleEnum.Status.PENDING.getCode());
            priceChangeRule.setVendorId(Long.parseLong(vendor_id));
            priceChangeRule.setUserId(12L);
            priceChangeRuleMapper.insert(priceChangeRule);
            Long price_change_rule_id_new = priceChangeRule.getPriceChangeRuleId();
            /** end copy price_change_rule */

            /** start copy price_change_rule_category_brand */
            Map<String,Object> insert_price_change_rule_category_brand_maps = new HashMap<>();
            insert_price_change_rule_category_brand_maps.put("insert_price_change_rule_id",price_change_rule_id_new);
            insert_price_change_rule_category_brand_maps.put("price_change_rule_id",price_change_rule_id);
            insert_price_change_rule_category_brand_maps.put("discount_percentage",Integer.parseInt(discount));
            seasonMapper.copyPriceChangeRuleCategoryBrand(insert_price_change_rule_category_brand_maps);
            /** end copy price_change_rule_category_brand */

            /** start copy price_change_rule_group */
            Map<String,Object> insert_price_change_rule_group_maps = new HashMap<>();
            insert_price_change_rule_group_maps.put("insert_price_change_rule_id",price_change_rule_id_new);
            insert_price_change_rule_group_maps.put("discount_percentage",Integer.parseInt(discount));
            insert_price_change_rule_group_maps.put("price_change_rule_id",price_change_rule_id);
            seasonMapper.copyPriceChangeRuleGroup(insert_price_change_rule_group_maps);
            /** end copy price_change_rule_group */

            /** start copy price_change_rule_product */
            Map<String,Object> insert_price_change_rule_product_maps = new HashMap<>();
            insert_price_change_rule_product_maps.put("insert_price_change_rule_id",price_change_rule_id_new);
            insert_price_change_rule_product_maps.put("discount_percentage",Integer.parseInt(discount));
            insert_price_change_rule_product_maps.put("price_change_rule_id",price_change_rule_id);
            seasonMapper.copyPriceChangeRuleProduct(insert_price_change_rule_product_maps);
            /** end copy price_change_rule_product */

            /** start copy price_change_rule_season_group */
            Map<String,Object> insert_price_change_rule_season_group_maps = new HashMap<>();
            insert_price_change_rule_season_group_maps.put("insert_price_change_rule_id",price_change_rule_id_new);
            insert_price_change_rule_season_group_maps.put("price_change_rule_id",price_change_rule_id);
            seasonMapper.copyPriceChangeRuleSeasonGroup(insert_price_change_rule_season_group_maps);
            /** end copy price_change_rule_season_group */
        }
    }

}
