package com.intramirror.product.core.impl.price;

import com.google.gson.Gson;
import com.intramirror.common.Contants;
import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.enums.SystemPropertyEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleSeasonGroup;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.PriceChangeRuleMapper;
import com.intramirror.product.core.mapper.PriceChangeRuleSeasonGroupMapper;
import com.intramirror.product.core.mapper.SeasonMapper;
import com.intramirror.product.core.mapper.SystemPropertyMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dingyifan on 2017/7/17.
 */
@Service(value = "productPriceChangeRule")
public class PriceChangeRuleImpl extends BaseDao implements IPriceChangeRule {

    private static Logger logger = LoggerFactory.getLogger(PriceChangeRuleImpl.class);

    private PriceChangeRuleMapper priceChangeRuleMapper;

    private SeasonMapper seasonMapper;

    private SystemPropertyMapper systemPropertyMapper;

    private PriceChangeRuleSeasonGroupMapper priceChangeRuleSeasonGroupMapper;

    @Transactional
    @Override
    public boolean updateVendorPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());

        // update default discount
        int default_discount = this.getDeafultDisscount(PriceChangeRuleEnum.PriceType.SUPPLY_PRICE);
        if(default_discount > 0 && default_discount < 100) {
            paramsMap.put("default_discount_percentage",default_discount);
            priceChangeRuleMapper.updateDefaultPriceByVendor(paramsMap);
            /**
                 update sku set sku.in_price = sku.price * #{default_discount_percentage,jdbcType=BIGINT}/((1+0.22)*100)
             */
        }

        List<Map<String,Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("selSeasonGroupRuleMaps : {}",new Gson().toJson(selSeasonGroupRuleMaps));
        /**
             select pcr.price_change_rule_id,pcrsg.season_code,pcr.vendor_id,pcr.shop_id,pcr.user_id from price_change_rule pcr
             inner join price_change_rule_season_group pcrsg on (pcrsg.price_change_rule_id = pcr.price_change_rule_id and pcrsg.enabled = 1)
             where pcr.price_type = #{price_type,jdbcType=BIGINT}
             and pcr.`status` = 1
             and date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d');
         */

        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("selSecondCategoryRuleMaps : {}",new Gson().toJson(selSecondCategoryRuleMaps));
        /**
             select pcrcb.discount_percentage,pcr.vendor_id,pcr.shop_id,pcrcb.category_id,pcrcb.`level`,pcr.price_change_rule_id,pcrcb.brand_id from price_change_rule pcr
             inner join price_change_rule_category_brand pcrcb on (pcr.price_change_rule_id = pcrcb.price_change_rule_id and pcrcb.`level` = 2)
             where 1=1
             and pcr.price_type = #{price_type,jdbcType=BIGINT}
             and pcr.`status` = 1
             and pcrcb.exception_flag = 0
             and  date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')
         */

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("selAllCategoryRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));
        /**
             select pcrcb.discount_percentage,pcr.vendor_id,pcr.shop_id,pcrcb.category_id,pcrcb.`level`,pcrcb.brand_id,pcr.price_change_rule_id from price_change_rule pcr
             inner join price_change_rule_category_brand pcrcb on (pcr.price_change_rule_id = pcrcb.price_change_rule_id)
             where 1=1
             and pcr.price_type = #{price_type,jdbcType=BIGINT}
             and pcr.`status` = 1
             and pcrcb.exception_flag = 1
             and  date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')
         */

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("selProductGroupRuleMaps : {}",new Gson().toJson(selAllCategoryRuleMaps));
        /**
             select pcrg.discount_percentage,pcr.vendor_id,pcr.shop_id,pcrg.product_group_id,pcr.price_change_rule_id from price_change_rule pcr
             inner join price_change_rule_group pcrg on (pcr.price_change_rule_id = pcrg.price_change_rule_id)
             and pcr.price_type = #{price_type,jdbcType=BIGINT}
             and pcr.`status` = 1
             and date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')
         */

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("selProductRuleMaps : {}",new Gson().toJson(selProductRuleMaps));
        /**
             select pcrp.discount_percentage,pcr.vendor_id,pcr.shop_id,pcrp.product_id,pcr.price_change_rule_id from price_change_rule pcr
             inner join price_change_rule_product pcrp on (pcr.price_change_rule_id = pcrp.price_change_rule_id)
             and pcr.price_type = #{price_type,jdbcType=BIGINT}
             and pcr.`status` = 1
             and  date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')
         */

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);

        this.updateRuleStatus(paramsMap);
        return true;
    }

    private int updatePrice(List<Map<String,Object>> paramsList,Map<String,Object> paramsMap){
        if(paramsList != null && paramsList.size() > 0) {
            int i = priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);
            logger.info("update price success num : {}",i);
        }
        return 0;
    }

    @Override
    public boolean updateShopPrice() {
        priceChangeRuleMapper.updateSkuPriceByImPrice();
        /**
            update sku,shop_product_sku sps set sps.sale_price = sku.im_price
            where sku.enabled = 1 and sps.enabled = 1 and sku.sku_id = sps.sku_id
         */
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
    @Transactional
    public boolean updateAdminPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());

        int default_discount = this.getDeafultDisscount(PriceChangeRuleEnum.PriceType.IM_PRICE);
        if(default_discount > 0 && default_discount < 100) {
            paramsMap.put("default_discount_percentage",default_discount);
            priceChangeRuleMapper.updateDefaultPriceByAdmin(paramsMap);
        }

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

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);
        this.updatePrice(paramsList,paramsMap);
        return true;
    }

    private int getDeafultDisscount(PriceChangeRuleEnum.PriceType priceType){
        List<Map<String,Object>> mapList = systemPropertyMapper.selectSystemProperty();
        if(mapList != null && mapList.size() > 0) {
            for(Map<String,Object> map : mapList){
                if(priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode().intValue())
                    if(map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.BOUTIQUE_DISCOUNT_DEFAULT.getCode())) {
                        return Integer.parseInt(map.get("system_property_value").toString());
                    }
                if(priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.IM_PRICE.getCode().intValue())
                    if(map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.IM_DISCOUNT_DEFAULT.getCode())) {
                        return Integer.parseInt(map.get("system_property_value").toString());
                    }
            }
        }
        return 100;
    }

    private List<Map<String,Object>> handleMap(List<Map<String,Object>> paramsList,List<Map<String,Object>> dataList,int level,List<Map<String,Object>> seasonGroupMaps){
        if(dataList != null && dataList.size() > 0) {
            for (Map<String,Object> dataMap : dataList) {
                dataMap.put("handle_level",level);
                if(seasonGroupMaps != null && seasonGroupMaps.size() > 0) {
                    List<String> season_codes = new ArrayList<>();
                    for(Map<String,Object> seasonRuleMap : seasonGroupMaps) {
                        String p1 = dataMap.get("price_change_rule_id").toString();
                        String p2 = seasonRuleMap.get("price_change_rule_id").toString();
                        if(p1.equals(p2)) {
                            season_codes.add(seasonRuleMap.get("season_code").toString());
                        }
                    }
                    if(season_codes != null && season_codes.size() > 0) {
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
        systemPropertyMapper = this.getSqlSession().getMapper(SystemPropertyMapper.class);
        priceChangeRuleSeasonGroupMapper = this.getSqlSession().getMapper(PriceChangeRuleSeasonGroupMapper.class);
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

    @Transactional
    @Override
    public ResultMessage copyRuleByVendor(Map<String, Object> params) throws Exception{
        ResultMessage resultMessage = ResultMessage.getInstance();
        String vendor_id = params.get("vendor_id") == null ? "" : params.get("vendor_id").toString();
        String to_vendor_id = params.get("to_vendor_id") == null ? "" : params.get("to_vendor_id").toString();
        String discount = params.get("discount") == null ? "0" : params.get("discount").toString();
        Long user_id = Long.parseLong(params.get("user_id").toString());
        params.put("price_type",PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
        List<Map<String,Object>> ruleByConditionsMaps =  seasonMapper.queryRuleByConditions(params);

        if(ruleByConditionsMaps == null || ruleByConditionsMaps.size() == 0) {
            return resultMessage.errorStatus().putMsg("info"," Vendor has no rules !!!");
        }

        /** start checked 新规则是否存在 */
        Map<String,Object> newParams = new HashMap<>();
        newParams.put("status",PriceChangeRuleEnum.Status.PENDING.getCode());
        newParams.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
        newParams.put("vendor_id",to_vendor_id);
        List<Map<String,Object>> newRuleByConditionsMaps =  seasonMapper.queryRuleByConditions(newParams);
        if(newRuleByConditionsMaps != null && newRuleByConditionsMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info"," The rules that have been copied vendor !!!");
        }
        /** end checked 新规则是否存在 */

        this.copyAllRuleByActivePending(ruleByConditionsMaps,to_vendor_id,discount,true,user_id);
        resultMessage.successStatus().putMsg("info","SUCCESS !!!");
        return resultMessage;
    }

    @Transactional
    @Override
    public ResultMessage copyRuleBySeason(Map<String, Object> params) throws Exception {
        ResultMessage resultMessage = ResultMessage.getInstance();
        String vendor_id = params.get("vendor_id") == null ? "" : params.get("vendor_id").toString();
        String seasons = params.get("seasons") == null ? "" : params.get("seasons").toString();
        Long user_id = Long.parseLong(params.get("user_id").toString());
        params.put("name",seasons);
        String[] season_codes = seasons.split(",");
        List<Map<String,Object>> ruleByConditionsMaps = new ArrayList<>();
        ruleByConditionsMaps.add(params);

        /** start checked 该vendor下是否存在该season_code */
        params.put("season_codes",season_codes);
        List<Map<String,Object>> seasonCodeMaps =  seasonMapper.querySeasonByVendor(params);

        if(seasonCodeMaps != null && seasonCodeMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info"," Vendor already contains the rule !!!");
        }
        /** end checked 该vendor下是否存在该season_code */

        Long price_change_rule_id_new = this.copyAllRuleByActivePending(ruleByConditionsMaps,vendor_id,"0",false,user_id);

        // create price_season_group
        for(String season_code : season_codes) {
            if(StringUtils.isNotBlank(season_code)) {
                PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup = new PriceChangeRuleSeasonGroup();
                priceChangeRuleSeasonGroup.setSeasonCode(season_code);
                priceChangeRuleSeasonGroup.setCreatedAt(new Date());
                priceChangeRuleSeasonGroup.setUpdatedAt(new Date());
                priceChangeRuleSeasonGroup.setEnabled(1);
                priceChangeRuleSeasonGroup.setPriceChangeRuleId(price_change_rule_id_new);
                if(season_codes.length > 1) {
                    priceChangeRuleSeasonGroup.setName("Multiple" + price_change_rule_id_new);
                } else {
                    priceChangeRuleSeasonGroup.setName(season_code);
                }

                priceChangeRuleSeasonGroupMapper.insert(priceChangeRuleSeasonGroup);
            }
        }
        resultMessage.successStatus().putMsg("info","success !!!");
        return resultMessage;
    }

    private Long copyAllRuleByActivePending(List<Map<String,Object>> ruleByConditionsMaps,String vendor_id,String discount,boolean insert_season_group_flag,Long user_id) throws Exception{
        Long price_change_rule_id_new = 0L;
        for(Map<String,Object> map : ruleByConditionsMaps){
            String price_change_rule_id = map.get("price_change_rule_id").toString();
            String name = map.get("name").toString();

            /** start copy price_change_rule */
            PriceChangeRule priceChangeRule = new PriceChangeRule();
            priceChangeRule.setName(name);
            priceChangeRule.setPriceType(Byte.parseByte(PriceChangeRuleEnum.PriceType.IM_PRICE.getCode() + ""));
            priceChangeRule.setStatus(PriceChangeRuleEnum.Status.PENDING.getCode());
            priceChangeRule.setVendorId(Long.parseLong(vendor_id));
            priceChangeRule.setUserId(user_id);
            priceChangeRuleMapper.insert(priceChangeRule);
            price_change_rule_id_new = priceChangeRule.getPriceChangeRuleId();
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

            if(insert_season_group_flag) {
                /** start copy price_change_rule_season_group */
                Map<String,Object> insert_price_change_rule_season_group_maps = new HashMap<>();
                insert_price_change_rule_season_group_maps.put("insert_price_change_rule_id",price_change_rule_id_new);
                insert_price_change_rule_season_group_maps.put("price_change_rule_id",price_change_rule_id);
                seasonMapper.copyPriceChangeRuleSeasonGroup(insert_price_change_rule_season_group_maps);
                /** end copy price_change_rule_season_group */
            }
        }
        return price_change_rule_id_new;
    }

	@Override
	public List<PriceChangeRule> selectByName(String name) {
		return priceChangeRuleMapper.selectByName(name);
	}

}
