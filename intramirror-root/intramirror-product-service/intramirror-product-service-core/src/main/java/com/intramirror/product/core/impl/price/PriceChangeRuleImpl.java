package com.intramirror.product.core.impl.price;

import com.google.gson.Gson;
import com.intramirror.common.Contants;
import com.intramirror.common.enums.PriceChangeRuleEnum;
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

        paramsList = this.handleMap(paramsList,selSecondCategoryRuleMaps,Contants.num_one);
        paramsList = this.handleMap(paramsList,selAllCategoryRuleMaps,Contants.num_second);
        paramsList = this.handleMap(paramsList,selProductGroupRuleMaps,Contants.num_three);
        paramsList = this.handleMap(paramsList,selProductRuleMaps,Contants.num_four);

        priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);

        return true;
    }

    @Override
    public boolean updateShopPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SALE_PRICE.getCode());

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMap(paramsList,selSecondCategoryRuleMaps,Contants.num_one);
        paramsList = this.handleMap(paramsList,selAllCategoryRuleMaps,Contants.num_second);
        paramsList = this.handleMap(paramsList,selProductGroupRuleMaps,Contants.num_three);
        paramsList = this.handleMap(paramsList,selProductRuleMaps,Contants.num_four);

        priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);
        return true;
    }

    @Override
    public boolean updateAdminPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));

        paramsList = this.handleMap(paramsList,selSecondCategoryRuleMaps,Contants.num_one);
        paramsList = this.handleMap(paramsList,selAllCategoryRuleMaps,Contants.num_second);
        paramsList = this.handleMap(paramsList,selProductGroupRuleMaps,Contants.num_three);
        paramsList = this.handleMap(paramsList,selProductRuleMaps,Contants.num_four);

        priceChangeRuleMapper.updateSkuPriceByAdmin(paramsList);
        return false;
    }

    private List<Map<String,Object>> handleMap(List<Map<String,Object>> paramsList,List<Map<String,Object>> dataList,int level){
        if(dataList != null && dataList.size() > 0) {
            for (Map<String,Object> dataMap : dataList) {
                dataMap.put("handle_level",level);
                paramsList.add(dataMap);
            }
        }
        return paramsList;
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
