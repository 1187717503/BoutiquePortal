package com.intramirror.product.core.impl.price;

import com.google.gson.Gson;
import com.intramirror.common.Contants;
import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleMapper;

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

    @Override
    public boolean updateVendorPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByVendor(paramsList,selSecondCategoryRuleMaps,Contants.num_one);
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
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByAdmin(paramsList,selSecondCategoryRuleMaps,Contants.num_one,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        paramsList = this.handleMapByAdmin(paramsList,selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);

        if(paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByShop(paramsList);
            this.updateRuleStatus(paramsMap);
        }
        return true;
    }

    @Override
    public boolean updateAdminPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());

        List<Map<String,Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("selSeasonGroupRuleMaps : {}",new Gson().toJson(selSeasonGroupRuleMaps));

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMapByAdmin(paramsList,selSecondCategoryRuleMaps,Contants.num_one,selSeasonGroupRuleMaps);
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
}
