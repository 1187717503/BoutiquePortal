

package com.intramirror.product.core.impl.price;

import com.alibaba.fastjson.JSONObject;
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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dingyifan on 2017/7/17.
 */
@Service(value = "productPriceChangeRule")
public class PriceChangeRuleImpl extends BaseDao implements IPriceChangeRule {

    private static Logger logger = Logger.getLogger(PriceChangeRuleImpl.class);

    private PriceChangeRuleMapper priceChangeRuleMapper;

    private SeasonMapper seasonMapper;

    private SystemPropertyMapper systemPropertyMapper;

    private PriceChangeRuleSeasonGroupMapper priceChangeRuleSeasonGroupMapper;

    @Override
    public boolean updateVendorPrice() {
        Map<String,Object> paramsMap = new HashMap<>();
        List<Map<String,Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
        
        List<Map<String,Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("vendor selSeasonGroupRuleMaps : "+selSeasonGroupRuleMaps.size());
        
        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("vendor selSecondCategoryRuleMaps : "+selSecondCategoryRuleMaps.size());

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("vendor selAllCategoryRuleMaps : "+selAllCategoryRuleMaps.size());

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("vendor selProductGroupRuleMaps : "+selProductGroupRuleMaps.size());

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("vendor selProductRuleMaps : "+selProductRuleMaps.size());

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList,paramsMap);
        logger.info("updatePriceByVendor selSecondCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList,paramsMap);
        logger.info("updatePriceByVendor selAllCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList,paramsMap);
        logger.info("updatePriceByVendor selProductGroupRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList,paramsMap);
        logger.info("updatePriceByVendor selProductRuleMaps end !");
	
        this.updateRuleStatus(paramsMap);
        logger.info("updatePriceByVendor updateRuleStatus end !");

        this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.SUPPLY_PRICE,paramsMap);

        /*this.updateAdminPrice();
        
        this.updateShopPrice();*/
        return true;
    }

    private void updateDefaultPrice(PriceChangeRuleEnum.PriceType priceType,Map<String,Object> paramsMap){
        // update default discount
        int default_discount = this.getDeafultDisscount(priceType);
        if(default_discount > 0) {
            paramsMap.put("default_discount_percentage",default_discount);

            List<String> vendor_ids = new ArrayList<>();
            List<String> season_codes = new ArrayList<>();
            List<Map<String,Object>> activeSeasonList = priceChangeRuleMapper.selectActiveSeasonGroupRule(paramsMap);
            if(activeSeasonList != null && activeSeasonList.size() > 0) {
                for(Map<String,Object> map : activeSeasonList) {
                    vendor_ids.add(map.get("vendor_id").toString());
                    season_codes.add(map.get("season_code").toString());
                }
            }

            paramsMap.put("vendor_ids",vendor_ids);
            paramsMap.put("season_codes",season_codes);

            if(vendor_ids == null || vendor_ids.size() ==0 ){vendor_ids.add("-1");}
            if(season_codes == null || season_codes.size() ==0 ){season_codes.add("-1");}

            logger.info(" update default price params : "+new Gson().toJson(paramsMap));

            if(priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode().intValue()) {
                int i = priceChangeRuleMapper.updateDefaultPriceByVendor(paramsMap);
                logger.info("update vendor price by default discount : "+i );
            } else if(priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.IM_PRICE.getCode().intValue()) {
                int i = priceChangeRuleMapper.updateDefaultPriceByAdmin(paramsMap);
                logger.info("update admin price by default discount : "+i );
            }
        }
    }

    private int updatePriceByVendor(List<Map<String,Object>> paramsList,Map<String,Object> paramsMap){
        /*if(paramsList != null && paramsList.size() > 0) {
            logger.info("update vendor price success paramsList:"+new Gson().toJson(paramsList));
            for(int i = 0;i<paramsList.size();i++) {
                List<Map<String,Object>> maps = new ArrayList<>();
                maps.add(paramsList.get(i));
                priceChangeRuleMapper.updateSkuPriceByVendor(maps);
                logger.info("updatePriceByVendor,i:"+i+" :" + maps +"----" + paramsList.size());
            }
        }
        logger.info("update vendor price success num : "+0);
        return 0;*/

        // update by dingyifan 2017-10-11
        logger.info("PriceChangeRuleImplUpdatePriceByVendor,updateSkuPriceByVendor,start,paramsList:"+ JSONObject.toJSONString(paramsList)+",size:"+paramsList==null?0:paramsList.size());
        if(paramsList != null && paramsList.size() > 0){
            priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);
        }
        logger.info("PriceChangeRuleImplUpdatePriceByVendor,updateSkuPriceByVendor,end,paramsList:"+JSONObject.toJSONString(paramsList)+",size:"+paramsList==null?0:paramsList.size());
        // update by dingyifan 2017-10-11
        return 0;
    }

    private int updatePriceByAdmin(List<Map<String,Object>> paramsList,Map<String,Object> paramsMap){
        /*if(paramsList != null && paramsList.size() > 0) {
            logger.info("update admin price success paramsList:"+new Gson().toJson(paramsList));
            for(int i = 0;i<paramsList.size();i++) {
                List<Map<String,Object>> maps = new ArrayList<>();
                maps.add(paramsList.get(i));
                priceChangeRuleMapper.updateSkuPriceByAdmin(maps);
                logger.info("updatePriceByAdmin,i:"+i+" :" + maps +"----" + paramsList.size());
            }
        }
        logger.info("update admin price success num : "+0);
        return 0;*/

        // update by dingyifan 2017-10-11
        logger.info("PriceChangeRuleImplUpdatePriceByAdmin,updateSkuPriceByAdmin,start,paramsList:"+JSONObject.toJSONString(paramsList)+",size:"+paramsList==null?0:paramsList.size());
        if(paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByAdmin(paramsList);
        }
        logger.info("PriceChangeRuleImplUpdatePriceByAdmin,updateSkuPriceByAdmin,end,paramsList:"+JSONObject.toJSONString(paramsList)+",size:"+paramsList==null?0:paramsList.size());
        // update by dingyifan 2017-10-11
        return 0;
    }

    @Override
    public boolean updateShopPrice() {
        priceChangeRuleMapper.updateSkuPriceByImPrice();
        logger.info("updateShopPrice updateSkuPriceByImPrice end !");
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
        logger.info("admin selSeasonGroupRuleMaps : "+selSeasonGroupRuleMaps.size());
        
        List<Map<String,Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("admin selSecondCategoryRuleMaps : "+selSecondCategoryRuleMaps.size());

        List<Map<String,Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("admin selAllCategoryRuleMaps : "+selAllCategoryRuleMaps.size());

        List<Map<String,Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("admin selProductGroupRuleMaps : "+selProductGroupRuleMaps.size());

        List<Map<String,Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("admin selProductRuleMaps : "+selProductRuleMaps.size());

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selSecondCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList,paramsMap);
        logger.info("updatePriceByAdmin selSecondCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selAllCategoryRuleMaps,Contants.num_second,selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList,paramsMap);
        logger.info("updatePriceByAdmin selAllCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductGroupRuleMaps,Contants.num_three,selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList,paramsMap);
        logger.info("updatePriceByAdmin selProductGroupRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(),selProductRuleMaps,Contants.num_four,selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList,paramsMap);
        logger.info("updatePriceByAdmin selProductRuleMaps end !");

        this.updateRuleStatus(paramsMap);
        logger.info("updatePriceByAdmin updateRuleStatus end !");

        this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.IM_PRICE,paramsMap);
        return true;
    }

    @Override
    public boolean updateShopProductSalePrice() throws Exception {
        priceChangeRuleMapper.updateShopProductSalePrice();
        return true;
    }

    @Override
    public boolean updateProductRetailPrice() throws Exception {
        priceChangeRuleMapper.updateProductRetailPrice();
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
            for(Map<String,Object> map : selNowRuleMaps){
//                params.put("price_change_rule_id",map.get("price_change_rule_id"));
                params.put("vendor_id",map.get("vendor_id"));
                priceChangeRuleMapper.updateRuleInActive(params);
            }

            for(Map<String,Object> map : selNowRuleMaps){
                params.put("price_change_rule_id",map.get("price_change_rule_id"));
                priceChangeRuleMapper.updateRuleActive(params);
            }
        }
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

        this.copyAllRuleByActivePending(ruleByConditionsMaps,to_vendor_id,discount,true,user_id, PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
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
        
        Integer price_type =Integer.parseInt(params.get("price_type").toString());
        Long price_change_rule_id_new = this.copyAllRuleByActivePending(ruleByConditionsMaps,vendor_id,"0",false,user_id, price_type);

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

    @Override
    public ResultMessage copyRule(Map<String, Object> params) throws Exception {
        ResultMessage resultMessage = ResultMessage.getInstance();

        // select vendor exists pending rule
        params.put("status",PriceChangeRuleEnum.Status.PENDING.getCode());
        List<Map<String,Object>> pendingMaps = priceChangeRuleMapper.selRuleByVendorPriceType(params);
        if(pendingMaps != null && pendingMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info","vendor existing pending rules.");
        }

        // select vendor no exists active rule
        params.put("status",PriceChangeRuleEnum.Status.ACTIVE.getCode());
        List<Map<String,Object>> activeMaps = priceChangeRuleMapper.selRuleByVendorPriceType(params);
        if(activeMaps == null || activeMaps.size() == 0) {
            return resultMessage.errorStatus().putMsg("info","vendor no active rules exist.");
        }

        // copy
        Long new_price_change_rule_id = this.copyAllRuleByActivePending(activeMaps,params.get("vendor_id").toString(),"0",true,Long.parseLong(params.get("user_id").toString()), Integer.parseInt(params.get("price_type").toString()));
        logger.info(" new_price_change_rule_id : "+new_price_change_rule_id);
        resultMessage.successStatus().putMsg("info","success");
        return resultMessage;
    }

    private Long copyAllRuleByActivePending(List<Map<String,Object>> ruleByConditionsMaps,String vendor_id,String discount,boolean insert_season_group_flag,Long user_id,Integer priceType) throws Exception{
        Long price_change_rule_id_new = 0L;
        for(Map<String,Object> map : ruleByConditionsMaps){
            String price_change_rule_id = map.get("price_change_rule_id").toString();
            String name = map.get("name").toString();

            /** start copy price_change_rule */
            PriceChangeRule priceChangeRule = new PriceChangeRule();
            priceChangeRule.setName(name);
            priceChangeRule.setPriceType(Byte.parseByte(priceType+ ""));
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
    public List<PriceChangeRule> selectByName(String name,Long vendorId) {
    	//组合参数
    	Map<String,Object> map =new HashMap<String, Object>();
    	map.put("name", name);
    	map.put("vendorId", vendorId);
        return priceChangeRuleMapper.selectByName(map);
    }
    
    @Override
	public List<Map<String,Object>> selRuleByVendorPriceType(Map<String,Object> params){
    	return priceChangeRuleMapper.selRuleByVendorPriceType(params);
    };

}
