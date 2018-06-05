package com.intramirror.product.core.impl.price;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.Contants;
import com.intramirror.common.enums.PriceChangeRuleEnum;
import com.intramirror.common.enums.SystemPropertyEnum;
import com.intramirror.common.help.ResultMessage;
import com.intramirror.common.help.StringUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.exception.BusinessException;
import com.intramirror.product.api.model.ImPriceAlgorithm;
import com.intramirror.product.api.model.PriceChangeRule;
import com.intramirror.product.api.model.PriceChangeRuleSeasonGroup;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.model.SnapshotPriceRule;
import com.intramirror.product.api.service.price.IPriceChangeRule;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.*;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private ImPriceAlgorithmMapper imPriceAlgorithmMapper;

    private ProductMapper productMapper;

    private SnapshotPriceRuleMapper snapshotPriceRuleMapper;

    @Override
    public boolean updateVendorPrice(int categoryType, String startTime, String endTime) {

        logger.info("updateVendorPrice start");
        Map<String, Object> paramsMap = new HashMap<>();
        List<Map<String, Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
        paramsMap.put("preview_status", "0");
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);

        paramsMap.put("category_type", categoryType);

        List<Map<String, Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("vendor selSeasonGroupRuleMaps : " + selSeasonGroupRuleMaps.size());

        List<Map<String, Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("vendor selSecondCategoryRuleMaps : " + selSecondCategoryRuleMaps.size());

        List<Map<String, Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("vendor selAllCategoryRuleMaps : " + selAllCategoryRuleMaps.size());

        List<Map<String, Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("vendor selProductGroupRuleMaps : " + selProductGroupRuleMaps.size());

        List<Map<String, Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("vendor selProductRuleMaps : " + selProductRuleMaps.size());

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selSecondCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList, paramsMap);
        logger.info("updatePriceByVendor selSecondCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selAllCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList, paramsMap);
        logger.info("updatePriceByVendor selAllCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductGroupRuleMaps, Contants.num_three, selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList, paramsMap);
        logger.info("updatePriceByVendor selProductGroupRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductRuleMaps, Contants.num_four, selSeasonGroupRuleMaps);
        this.updatePriceByVendor(paramsList, paramsMap);
        logger.info("updatePriceByVendor selProductRuleMaps end !");

        this.updateRuleStatus(paramsMap);
        logger.info("updatePriceByVendor updateRuleStatus end !");

        this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.SUPPLY_PRICE, paramsMap);

        /*this.updateAdminPrice();
        
        this.updateShopPrice();*/
        return true;
    }

    @Override
    public boolean updateVendorPrice(Long vendor_id, int categoryType, Long price_change_rule_id) throws Exception {
        Map<String, Object> paramsMap = new HashMap<>();
        List<Map<String, Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
        paramsMap.put("preview_status", "1");
        paramsMap.put("vendor_id", vendor_id);
        paramsMap.put("category_type", categoryType);
        paramsMap.put("price_change_rule_id", price_change_rule_id);
        List<Map<String, Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        if (selSeasonGroupRuleMaps != null && selSeasonGroupRuleMaps.size() > 0) {
            List<Map<String, Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
            List<Map<String, Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
            selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
            List<Map<String, Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
            List<Map<String, Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);

            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selSecondCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updatePriceByVendor(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selAllCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updatePriceByVendor(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductGroupRuleMaps, Contants.num_three, selSeasonGroupRuleMaps);
            this.updatePriceByVendor(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductRuleMaps, Contants.num_four, selSeasonGroupRuleMaps);
            this.updatePriceByVendor(paramsList, paramsMap);
            this.updateRuleStatus(paramsMap);
            this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.SUPPLY_PRICE, paramsMap);
        }
        return true;
    }

    @Override
    public boolean updateAdminPrice(Long vendor_id, int categoryType, Long price_change_rule_id) {
        Map<String, Object> paramsMap = new HashMap<>();
        List<Map<String, Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
        paramsMap.put("preview_status", "1");
        paramsMap.put("vendor_id", vendor_id);
        paramsMap.put("category_type", categoryType);
        paramsMap.put("price_change_rule_id", price_change_rule_id);

        List<Map<String, Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        if (selSeasonGroupRuleMaps != null && selSeasonGroupRuleMaps.size() > 0) {
            List<Map<String, Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
            List<Map<String, Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
            selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
            List<Map<String, Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
            List<Map<String, Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);

            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selSecondCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updatePriceByAdmin(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selAllCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updatePriceByAdmin(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductGroupRuleMaps, Contants.num_three, selSeasonGroupRuleMaps);
            this.updatePriceByAdmin(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductRuleMaps, Contants.num_four, selSeasonGroupRuleMaps);
            this.updatePriceByAdmin(paramsList, paramsMap);

            this.updateRuleStatus(paramsMap);
            this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.IM_PRICE, paramsMap);
        }
        return true;
    }

    public void updateDefaultPrice(PriceChangeRuleEnum.PriceType priceType, Map<String, Object> paramsMap) {
        // update default discount
        int default_discount = this.getDeafultDisscount(priceType);
        if (default_discount > 0) {
            paramsMap.put("default_discount_percentage", default_discount);

            List<Map<String, Object>> activeSeasonList = priceChangeRuleMapper.selectActiveSeasonGroupRule(paramsMap);
            logger.info("updateDefaultPrice,selectActiveSeasonGroupRule,activeSeasonList:" + JSONObject.toJSONString(activeSeasonList));

            if (activeSeasonList != null && activeSeasonList.size() > 0) {

                for (Map<String, Object> map : activeSeasonList) {

                    String season_codes = map.get("season_codes").toString();
                    map.put("season_codes", season_codes.split(","));
                    map.put("default_discount_percentage", default_discount);
                    map.put("category_type", map.get("category_type").toString());
                    if (priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode().intValue()) {
                        int i = priceChangeRuleMapper.updateDefaultPriceByVendor(map);
                        logger.info("update vendor price by default discount : " + i + ",map:" + JSONObject.toJSONString(map));
                    } else if (priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.IM_PRICE.getCode().intValue()) {
                        int i = priceChangeRuleMapper.updateDefaultPriceByAdmin(map);
                        logger.info("update admin price by default discount : " + i + ",map:" + JSONObject.toJSONString(map));
                    }
                }
            }

            /*if(activeSeasonList != null && activeSeasonList.size() > 0) {
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
            }*/
        }
    }

    @Override
    public List<Map<String, Object>> selectNowActiveRule(Map<String, Object> params) {
        return priceChangeRuleMapper.selectNowActiveRule(params);
    }

    public static void main(String[] args) {
//        System.out.println(JSONObject.toJSON("12".split(",")));
//        BigDecimal a = new BigDecimal(1);
//        BigDecimal b = new BigDecimal(2);
        Map<String,Object> map = new HashedMap();
        map.put("info",12);
        Long info = Long.parseLong(map.get("info").toString());
        System.out.println(info);
    }

    private int updatePriceByVendor(List<Map<String, Object>> paramsList, Map<String, Object> paramsMap) {
        if (paramsList != null && paramsList.size() > 0) {
            for (int i = 0, len = paramsList.size(); i < len; i++) {
                Map<String, Object> executeMap = paramsList.get(i);

                logger.info("updatePriceByVendor,updateSkuPriceByVendor,start,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);
                priceChangeRuleMapper.updateSkuPriceByVendor(executeMap);
                logger.info("updatePriceByVendor,updateSkuPriceByVendor,end,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);

            }
        }
        return 0;

        /*// update by dingyifan 2017-10-11
        logger.info("PriceChangeRuleImplUpdatePriceByVendor,updateSkuPriceByVendor,start,paramsList:"+ JSONObject.toJSONString(paramsList));
        if(paramsList != null && paramsList.size() > 0){
            priceChangeRuleMapper.updateSkuPriceByVendor(paramsList);
        }
        logger.info("PriceChangeRuleImplUpdatePriceByVendor,updateSkuPriceByVendor,end,paramsList:"+JSONObject.toJSONString(paramsList));
        // update by dingyifan 2017-10-11*/
    }

    private int updatePriceByAdmin(List<Map<String, Object>> paramsList, Map<String, Object> paramsMap) {
        if (paramsList != null && paramsList.size() > 0) {
            for (int i = 0, len = paramsList.size(); i < len; i++) {

                Map<String, Object> executeMap = paramsList.get(i);
                logger.info("updatePriceByAdmin,updateSkuPriceByAdmin,start,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);
                priceChangeRuleMapper.updateSkuPriceByAdmin(executeMap);
                logger.info("updatePriceByAdmin,updateSkuPriceByAdmin,end,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);
            }
        }
        return 0;

        /*// update by dingyifan 2017-10-11
        logger.info("PriceChangeRuleImplUpdatePriceByAdmin,updateSkuPriceByAdmin,start,paramsList:"+JSONObject.toJSONString(paramsList));
        if(paramsList != null && paramsList.size() > 0) {
            priceChangeRuleMapper.updateSkuPriceByAdmin(paramsList);
        }
        logger.info("PriceChangeRuleImplUpdatePriceByAdmin,updateSkuPriceByAdmin,end,paramsList:"+JSONObject.toJSONString(paramsList));
        // update by dingyifan 2017-10-11*/
    }

    @Override
    public boolean updateShopPrice() {
        priceChangeRuleMapper.updateSkuPriceByImPrice();
        logger.info("updateShopPrice updateSkuPriceByImPrice end !");
        return true;
    }

    public List<Map<String, Object>> sortListByLevel(List<Map<String, Object>> maps) {
        List<Map<String, Object>> sortMaps = new ArrayList<>();
        if (maps != null && maps.size() > 0) {
            for (Map<String, Object> map : maps) {
                String level = map.get("level").toString();
                if (level.equals("1")) {
                    sortMaps.add(map);
                }
            }
            for (Map<String, Object> map : maps) {
                String level = map.get("level").toString();
                if (level.equals("2")) {
                    sortMaps.add(map);
                }
            }
            for (Map<String, Object> map : maps) {
                String level = map.get("level").toString();
                if (level.equals("3")) {
                    sortMaps.add(map);
                }
            }
        }
        return sortMaps;
    }

    @Override
    public boolean updateAdminPrice(int categoryType, String startTime, String endTime) {
        logger.info("updateAdminPrice start");
        Map<String, Object> paramsMap = new HashMap<>();
        List<Map<String, Object>> paramsList = new ArrayList<>();
        paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
        paramsMap.put("preview_status", "0");
        paramsMap.put("category_type", categoryType);
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);

        List<Map<String, Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);
        logger.info("admin selSeasonGroupRuleMaps : " + selSeasonGroupRuleMaps.size());

        List<Map<String, Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);
        logger.info("admin selSecondCategoryRuleMaps : " + selSecondCategoryRuleMaps.size());

        List<Map<String, Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
        selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);
        logger.info("admin selAllCategoryRuleMaps : " + selAllCategoryRuleMaps.size());

        List<Map<String, Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);
        logger.info("admin selProductGroupRuleMaps : " + selProductGroupRuleMaps.size());

        List<Map<String, Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);
        logger.info("admin selProductRuleMaps : " + selProductRuleMaps.size());

        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selSecondCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList, paramsMap);
        logger.info("updatePriceByAdmin selSecondCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selAllCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList, paramsMap);
        logger.info("updatePriceByAdmin selAllCategoryRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductGroupRuleMaps, Contants.num_three, selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList, paramsMap);
        logger.info("updatePriceByAdmin selProductGroupRuleMaps end !");
        paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductRuleMaps, Contants.num_four, selSeasonGroupRuleMaps);
        this.updatePriceByAdmin(paramsList, paramsMap);
        logger.info("updatePriceByAdmin selProductRuleMaps end !");

        this.updateRuleStatus(paramsMap);
        logger.info("updatePriceByAdmin updateRuleStatus end !");

        this.updateDefaultPrice(PriceChangeRuleEnum.PriceType.IM_PRICE, paramsMap);
        return true;
    }

    @Override
    public  boolean updatePreviewPriceByBoutique(Long vendor_id, Long preview_status, Integer category_type, Long price_change_rule_id, String flag) throws Exception {
        if (flag.equalsIgnoreCase("all")) {
            price_change_rule_id = null;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("category_type", category_type);
        paramsMap.put("vendor_id", vendor_id);
        paramsMap.put("price_change_rule_id", price_change_rule_id);
        //根据参数查询snapshot对应的product id 和 im price snapshot_price_detail_id的值
        List<Map<String, Object>> snapshotLists = priceChangeRuleMapper.selectSnapshotByChangeRuleId(paramsMap);
        //更新price_change_rule 的 preview——status  为0 非活动折扣 1 活动折扣
        paramsMap.put("preview_status",preview_status);
        priceChangeRuleMapper.updatePriceChangeRuleById(paramsMap);


        if(snapshotLists != null && snapshotLists.size() > 0){
            if(preview_status.intValue() == 1) {
                for (Map<String, Object> snapshot : snapshotLists) {
                    Long pk = Long.parseLong(snapshot.get("product_id").toString());
                    ProductWithBLOBs product = productMapper.selectByPrimaryKey(pk);
                    if(product.getMaxRetailPrice().compareTo(new BigDecimal(snapshot.get("im_price").toString())) == -1){
                        snapshot.put("im_price",null);
                    }
                    priceChangeRuleMapper.updateProductImPriceByPrimaryKey(snapshot);
                }
            }
            if(preview_status.intValue() == 0){
                //关闭preview 更新所有vendor——id  category——type为1的product的preview im price的值为null
                Set<Long> productIds = new HashSet<Long>();
                for (Map<String, Object> snapshot : snapshotLists) {
                    productIds.add(Long.parseLong(snapshot.get("product_id").toString()));
//                    priceChangeRuleMapper.updateProductImPriceByPrimaryKey(snapshot);
                }
                if(productIds.size() > 0){
                    //update product  im_price wei null
                    Map<String,Object> productParams = new HashMap<String,Object>();
                    productParams.put("im_price",null);
                    productParams.put("set",productIds);
                    priceChangeRuleMapper.updateProductImPriceByProductIds(productParams);
                }
            }
        }
        return true;
    }

    @Override
    public boolean updatePreviewPrice(Long vendor_id, Long preview_status, Integer category_type, Long price_change_rule_id, String flag) throws Exception {

        if (preview_status.intValue() == 0) {
            if (flag.equalsIgnoreCase("all")) {
                price_change_rule_id = -1L;
            }
            priceChangeRuleMapper.clearProductPreviewPrice(vendor_id, category_type, price_change_rule_id);
            priceChangeRuleMapper.updatePriceChangeRulePreviewStatus(vendor_id, preview_status, category_type, price_change_rule_id);
        }

        if (preview_status.intValue() == 1) {
            Map<String, Object> paramsMap = new HashMap<>();
            List<Map<String, Object>> paramsList = new ArrayList<>();
            paramsMap.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
            paramsMap.put("preview_status", "1");
            paramsMap.put("category_type", category_type);
            paramsMap.put("vendor_id", vendor_id);
            if (flag.equalsIgnoreCase("all")) {
                price_change_rule_id = -1L;
            } else {
                paramsMap.put("price_change_rule_id", price_change_rule_id);
            }

            List<Map<String, Object>> selSeasonGroupRuleMaps = priceChangeRuleMapper.selectSeasonGroupRule(paramsMap);

            List<Map<String, Object>> selSecondCategoryRuleMaps = priceChangeRuleMapper.selectSecondCategoryRule(paramsMap);

            List<Map<String, Object>> selAllCategoryRuleMaps = priceChangeRuleMapper.selectAllCategoryRule(paramsMap);
            selAllCategoryRuleMaps = this.sortListByLevel(selAllCategoryRuleMaps);

            List<Map<String, Object>> selProductGroupRuleMaps = priceChangeRuleMapper.selectProductGroupRule(paramsMap);

            List<Map<String, Object>> selProductRuleMaps = priceChangeRuleMapper.selectProductRule(paramsMap);

            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selSecondCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updateProductPreviewImPrice(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selAllCategoryRuleMaps, Contants.num_second, selSeasonGroupRuleMaps);
            this.updateProductPreviewImPrice(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductGroupRuleMaps, Contants.num_three, selSeasonGroupRuleMaps);
            this.updateProductPreviewImPrice(paramsList, paramsMap);
            paramsList = this.handleMap(new ArrayList<Map<String, Object>>(), selProductRuleMaps, Contants.num_four, selSeasonGroupRuleMaps);
            this.updateProductPreviewImPrice(paramsList, paramsMap);

            priceChangeRuleMapper.updateProductPreviewPrice(vendor_id,category_type,price_change_rule_id);
            priceChangeRuleMapper.updatePriceChangeRulePreviewStatus(vendor_id, preview_status, category_type, price_change_rule_id);
        }
        return true;
    }

    private int updateProductPreviewImPrice(List<Map<String, Object>> paramsList, Map<String, Object> paramsMap) {
        if (paramsList != null && paramsList.size() > 0) {
            for (int i = 0, len = paramsList.size(); i < len; i++) {

                Map<String, Object> executeMap = paramsList.get(i);
                logger.info("updatePriceByAdmin,updateSkuPriceByAdmin,start,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);
                priceChangeRuleMapper.updateProductPreviewImPrice(executeMap);
                logger.info("updatePriceByAdmin,updateSkuPriceByAdmin,end,executeMap:" + JSONObject.toJSONString(executeMap) + ",i:" + i + "," + len);
            }
        }
        return 0;
    }

    @Override
    public boolean updateShopProductSalePrice() throws Exception {
        priceChangeRuleMapper.updateShopProductSalePrice();
        return true;
    }

    @Override
    public boolean updateProductImPrice() throws Exception {
        priceChangeRuleMapper.updateProductImPrice();
        return true;
    }

    @Override
    public boolean updateProductRetailPrice() throws Exception {
        priceChangeRuleMapper.updateProductRetailPrice();
        return true;
    }

    @Override
    public boolean updateProductBoutiquePrice() throws Exception {
        priceChangeRuleMapper.updateProductBoutiquePrice();
        return true;
    }

    private int getDeafultDisscount(PriceChangeRuleEnum.PriceType priceType) {
        List<Map<String, Object>> mapList = systemPropertyMapper.selectSystemProperty();
        if (mapList != null && mapList.size() > 0) {
            for (Map<String, Object> map : mapList) {
                if (priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode().intValue())
                    if (map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.BOUTIQUE_DISCOUNT_DEFAULT.getCode())) {
                        return Integer.parseInt(map.get("system_property_value").toString());
                    }
                if (priceType.getCode().intValue() == PriceChangeRuleEnum.PriceType.IM_PRICE.getCode().intValue())
                    if (map.get("system_property_name").toString().equals(SystemPropertyEnum.propertyName.IM_DISCOUNT_DEFAULT.getCode())) {
                        return Integer.parseInt(map.get("system_property_value").toString());
                    }
            }
        }
        return 100;
    }

    private List<Map<String, Object>> handleMap(List<Map<String, Object>> paramsList, List<Map<String, Object>> dataList, int level,
            List<Map<String, Object>> seasonGroupMaps) {
        if (dataList != null && dataList.size() > 0) {
            for (Map<String, Object> dataMap : dataList) {
                dataMap.put("handle_level", level);
                if (seasonGroupMaps != null && seasonGroupMaps.size() > 0) {
                    List<String> season_codes = new ArrayList<>();
                    for (Map<String, Object> seasonRuleMap : seasonGroupMaps) {
                        String p1 = dataMap.get("price_change_rule_id").toString();
                        String p2 = seasonRuleMap.get("price_change_rule_id").toString();
                        if (p1.equals(p2)) {
                            season_codes.add(seasonRuleMap.get("season_code").toString());
                        }
                    }
                    if (season_codes != null && season_codes.size() > 0) {
                        dataMap.put("season_codes", season_codes);
                    }
                }
                paramsList.add(dataMap);
            }
        }
        return paramsList;
    }

    private boolean updateRuleStatus(Map<String, Object> params) {
        List<Map<String, Object>> selNowRuleMaps = priceChangeRuleMapper.selNowRule(params);
        //        List<Map<String, Object>> selectRuleInActiveMaps = priceChangeRuleMapper.selectRuleInActive(params);
        params.put("now", DateUtils.getformatDate(new Date()));
        if (selNowRuleMaps != null && selNowRuleMaps.size() > 0) {
            for (Map<String, Object> map : selNowRuleMaps) {
                //                params.put("price_change_rule_id",map.get("price_change_rule_id"));
                params.put("vendor_id", map.get("vendor_id"));
                params.put("price_change_rule_id", map.get("price_change_rule_id"));
                priceChangeRuleMapper.updateRuleInActive(params);
            }

            for (Map<String, Object> map : selNowRuleMaps) {
                params.put("price_change_rule_id", map.get("price_change_rule_id"));
                priceChangeRuleMapper.updateRuleActive(params);
            }
        }

        /*List<Map<String, Object>> selectRuleInActiveMaps = priceChangeRuleMapper.selectRuleInActive(params);

        for (Map<String, Object> activeMap : selectRuleInActiveMaps) {
            List<Map<String, Object>> activeMaps = new ArrayList<>();
            try {
                activeMaps.add(activeMap);
                this.copyAllRuleByActivePending(activeMaps, activeMap.get("vendor_id").toString(), "0", true, 999L,
                        Integer.parseInt(activeMap.get("price_type").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return true;
    }

    @Override
    public void init() {
        priceChangeRuleMapper = this.getSqlSession().getMapper(PriceChangeRuleMapper.class);
        seasonMapper = this.getSqlSession().getMapper(SeasonMapper.class);
        systemPropertyMapper = this.getSqlSession().getMapper(SystemPropertyMapper.class);
        priceChangeRuleSeasonGroupMapper = this.getSqlSession().getMapper(PriceChangeRuleSeasonGroupMapper.class);
        imPriceAlgorithmMapper = this.getSqlSession().getMapper(ImPriceAlgorithmMapper.class);
        snapshotPriceRuleMapper = this.getSqlSession().getMapper(SnapshotPriceRuleMapper.class);
        productMapper = this.getSqlSession().getMapper(ProductMapper.class);

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
    public int updateSkuImPrice() {
        return priceChangeRuleMapper.updateSkuImPrice();
    }

    @Override
    public PriceChangeRule selectByPrimaryKey(Long priceChangeRuleId) {
        return priceChangeRuleMapper.selectByPrimaryKey(priceChangeRuleId);
    }

    @Override
    public int updateByPrimaryKeySelective(PriceChangeRule record) {
        return priceChangeRuleMapper.updateByPrimaryKeySelective(record);
    }

    public String checkSeasonExists(Map<String, Object> params, String season) {
        String[] seasons = season.split(",");
        List<Map<String, Object>> datas = priceChangeRuleMapper.selectActiveSeason(params);
        if (datas == null || datas.size() == 0) {
            return "SUCCESS";
        }

        Map<String, List<String>> seasonRules = new HashMap<>();
        for (Map<String, Object> data : datas) {
            String price_rule_id = data.get("price_change_rule_id").toString();
            String season_code = data.get("season_code").toString();

            if (seasonRules.get(price_rule_id) == null) {
                List<String> seasonList = new ArrayList<>();
                seasonList.add(season_code);
                seasonRules.put(price_rule_id, seasonList);
            } else {
                seasonRules.get(price_rule_id).add(season_code);
            }
        }

        for (String key : seasonRules.keySet()) {
            List<String> valueList = seasonRules.get(key);
            if (valueList.contains(seasons[0])) {
                for (String seasonCode : seasons) {
                    if (!valueList.contains(seasonCode)) {
                        return "Already exist multiple.";
                    }
                }

                if (valueList.size() != seasons.length) {
                    return "Already exist multiple.";
                }
            }
        }

        return "SUCCESS";
    }

    @Transactional
    @Override
    public ResultMessage copyRuleByVendor(Map<String, Object> params) throws Exception {
        ResultMessage resultMessage = ResultMessage.getInstance();
        String vendor_id = params.get("vendor_id") == null ? "" : params.get("vendor_id").toString();
        String to_vendor_id = params.get("to_vendor_id") == null ? "" : params.get("to_vendor_id").toString();
        String discount = params.get("discount") == null ? "0" : params.get("discount").toString();
        Long user_id = Long.parseLong(params.get("user_id").toString());
        params.put("price_type", PriceChangeRuleEnum.PriceType.SUPPLY_PRICE.getCode());
        List<Map<String, Object>> ruleByConditionsMaps = seasonMapper.queryRuleByConditions(params);

        if (ruleByConditionsMaps == null || ruleByConditionsMaps.size() == 0) {
            return resultMessage.errorStatus().putMsg("info", " Vendor has no rules !!!");
        }

        /** start checked 新规则是否存在 */
        Map<String, Object> newParams = new HashMap<>();
        newParams.put("status", PriceChangeRuleEnum.Status.PENDING.getCode());
        newParams.put("price_type", PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
        newParams.put("vendor_id", to_vendor_id);
        newParams.put("category_type", params.get("category_type").toString());
        List<Map<String, Object>> newRuleByConditionsMaps = seasonMapper.queryRuleByConditions(newParams);
        if (newRuleByConditionsMaps != null && newRuleByConditionsMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info", " The rules that have been copied vendor !!!");
        }
        /** end checked 新规则是否存在 */

        this.copyAllRuleByActivePending(ruleByConditionsMaps, to_vendor_id, discount, true, user_id, PriceChangeRuleEnum.PriceType.IM_PRICE.getCode());
        resultMessage.successStatus().putMsg("info", "SUCCESS !!!");
        return resultMessage;
    }

    @Transactional
    @Override
    public ResultMessage copyRuleBySeason(Map<String, Object> params) throws Exception {
        ResultMessage resultMessage = ResultMessage.getInstance();
        String vendor_id = params.get("vendor_id") == null ? "" : params.get("vendor_id").toString();
        String seasons = params.get("seasons") == null ? "" : params.get("seasons").toString();
        Long user_id = Long.parseLong(params.get("user_id").toString());
        params.put("name", seasons);
        String[] season_codes = seasons.split(",");
        List<Map<String, Object>> ruleByConditionsMaps = new ArrayList<>();
        ruleByConditionsMaps.add(params);

        /** start checked 该vendor下是否存在该season_code */
        //根据price_change_rule_id查询PriceChangeRule获取categoryType
        PriceChangeRule priceChangeRule = priceChangeRuleMapper.selectByPrimaryKey(Long.parseLong(params.get("price_change_rule_id").toString()));
        params.put("categoryType", priceChangeRule.getCategoryType());
        params.put("season_codes", season_codes);

        Map<String, Object> checkParam = new HashMap<>();
        checkParam.put("vendorId", vendor_id);
        checkParam.put("price_type", params.get("price_type").toString());
        checkParam.put("categoryType", priceChangeRule.getCategoryType());
        String check = checkSeasonExists(checkParam, seasons);
        if (!check.equalsIgnoreCase("SUCCESS")) {
            return resultMessage.errorStatus().putMsg("info", check);
        }

        List<Map<String, Object>> seasonCodeMaps = seasonMapper.querySeasonByVendor(params);

        if (seasonCodeMaps != null && seasonCodeMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info", " Vendor already contains the rule !!!");
        }
        /** end checked 该vendor下是否存在该season_code */

        Integer price_type = Integer.parseInt(params.get("price_type").toString());
        Long price_change_rule_id_new = this.copyAllRuleByActivePending(ruleByConditionsMaps, vendor_id, "0", false, user_id, price_type);

        // create price_season_group
        for (String season_code : season_codes) {
            if (StringUtils.isNotBlank(season_code)) {
                PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup = new PriceChangeRuleSeasonGroup();
                priceChangeRuleSeasonGroup.setSeasonCode(season_code);
                priceChangeRuleSeasonGroup.setCreatedAt(new Date());
                priceChangeRuleSeasonGroup.setUpdatedAt(new Date());
                priceChangeRuleSeasonGroup.setEnabled(1);
                priceChangeRuleSeasonGroup.setPriceChangeRuleId(price_change_rule_id_new);
                if (season_codes.length > 1) {
                    priceChangeRuleSeasonGroup.setName("Multiple" + price_change_rule_id_new);
                } else {
                    priceChangeRuleSeasonGroup.setName(season_code);
                }

                priceChangeRuleSeasonGroupMapper.insert(priceChangeRuleSeasonGroup);
            }
        }
        resultMessage.successStatus().putMsg("info", "success !!!");
        return resultMessage;
    }

    @Override
    public ResultMessage copyRule(Map<String, Object> params) throws Exception {
        ResultMessage resultMessage = ResultMessage.getInstance();

        // select vendor exists pending rule
        params.put("status", PriceChangeRuleEnum.Status.PENDING.getCode());
        List<Map<String, Object>> pendingMaps = priceChangeRuleMapper.selRuleByVendorPriceType(params);
        if (pendingMaps != null && pendingMaps.size() > 0) {
            return resultMessage.errorStatus().putMsg("info", "vendor existing pending rules.");
        }

        // select vendor no exists active rule
        params.put("status", PriceChangeRuleEnum.Status.ACTIVE.getCode());
        List<Map<String, Object>> activeMaps = priceChangeRuleMapper.selRuleByVendorPriceType(params);
        if (activeMaps == null || activeMaps.size() == 0) {
            return resultMessage.errorStatus().putMsg("info", "vendor no active rules exist.");
        }

        // copy
        Long new_price_change_rule_id = this.copyAllRuleByActivePending(activeMaps, params.get("vendor_id").toString(), "0", true,
                Long.parseLong(params.get("user_id").toString()), Integer.parseInt(params.get("price_type").toString()));
        logger.info(" new_price_change_rule_id : " + new_price_change_rule_id);
        resultMessage.successStatus().putMsg("info", "success");
        return resultMessage;
    }

    private Long copyAllRuleByActivePending(List<Map<String, Object>> ruleByConditionsMaps, String vendor_id, String discount, boolean insert_season_group_flag,
            Long user_id, Integer priceType) throws Exception {
        Long price_change_rule_id_new = 0L;
        for (Map<String, Object> map : ruleByConditionsMaps) {
            String price_change_rule_id = map.get("price_change_rule_id").toString();
            String name = map.get("name").toString();
            //查询被复制的price_change_rule信息
            PriceChangeRule oldPriceChangeRule = priceChangeRuleMapper.selectByPrimaryKey(Long.parseLong(price_change_rule_id));

            /** start copy price_change_rule */
            PriceChangeRule priceChangeRule = new PriceChangeRule();
            priceChangeRule.setName(name);
            priceChangeRule.setPriceType(Byte.parseByte(priceType + ""));
            priceChangeRule.setStatus(PriceChangeRuleEnum.Status.PENDING.getCode());
            priceChangeRule.setVendorId(Long.parseLong(vendor_id));
            priceChangeRule.setUserId(user_id);
            priceChangeRule.setCategoryType(oldPriceChangeRule.getCategoryType());
            if (oldPriceChangeRule.getImPriceAlgorithmId() == null) {
                logger.error("oldPriceChangeRule.getImPriceAlgorithmId() is null. price_change_rule_id=" + price_change_rule_id);
                throw new BusinessException("Im price algorithm can not be empty.");
            }
            priceChangeRule.setImPriceAlgorithmId(oldPriceChangeRule.getImPriceAlgorithmId());
            priceChangeRuleMapper.insert(priceChangeRule);
            price_change_rule_id_new = priceChangeRule.getPriceChangeRuleId();
            /** end copy price_change_rule */

            /** start copy price_change_rule_category_brand */
            Map<String, Object> insert_price_change_rule_category_brand_maps = new HashMap<>();
            insert_price_change_rule_category_brand_maps.put("insert_price_change_rule_id", price_change_rule_id_new);
            insert_price_change_rule_category_brand_maps.put("price_change_rule_id", price_change_rule_id);
            insert_price_change_rule_category_brand_maps.put("discount_percentage", Integer.parseInt(discount));
            seasonMapper.copyPriceChangeRuleCategoryBrand(insert_price_change_rule_category_brand_maps);
            /** end copy price_change_rule_category_brand */

            /** start copy price_change_rule_group */
            Map<String, Object> insert_price_change_rule_group_maps = new HashMap<>();
            insert_price_change_rule_group_maps.put("insert_price_change_rule_id", price_change_rule_id_new);
            insert_price_change_rule_group_maps.put("discount_percentage", Integer.parseInt(discount));
            insert_price_change_rule_group_maps.put("price_change_rule_id", price_change_rule_id);
            seasonMapper.copyPriceChangeRuleGroup(insert_price_change_rule_group_maps);
            /** end copy price_change_rule_group */

            /** start copy price_change_rule_product */
            Map<String, Object> insert_price_change_rule_product_maps = new HashMap<>();
            insert_price_change_rule_product_maps.put("insert_price_change_rule_id", price_change_rule_id_new);
            insert_price_change_rule_product_maps.put("discount_percentage", Integer.parseInt(discount));
            insert_price_change_rule_product_maps.put("price_change_rule_id", price_change_rule_id);
            seasonMapper.copyPriceChangeRuleProduct(insert_price_change_rule_product_maps);
            /** end copy price_change_rule_product */

            if (insert_season_group_flag) {
                /** start copy price_change_rule_season_group */
                Map<String, Object> insert_price_change_rule_season_group_maps = new HashMap<>();
                insert_price_change_rule_season_group_maps.put("insert_price_change_rule_id", price_change_rule_id_new);
                insert_price_change_rule_season_group_maps.put("price_change_rule_id", price_change_rule_id);
                seasonMapper.copyPriceChangeRuleSeasonGroup(insert_price_change_rule_season_group_maps);
                /** end copy price_change_rule_season_group */
            }

            // 添加snapshot_price_rule数据
            SnapshotPriceRule snapshotPriceRule = new SnapshotPriceRule();
            snapshotPriceRule.setPriceChangeRuleId(priceChangeRule.getPriceChangeRuleId());
            snapshotPriceRule.setSaveAt(new Date());
            snapshotPriceRule.setCreatedAt(new Date());
            snapshotPriceRule.setStatus(new Byte("0"));
            snapshotPriceRuleMapper.insertSelective(snapshotPriceRule);
        }
        return price_change_rule_id_new;
    }

    @Override
    public List<PriceChangeRule> selectByName(String name, Long vendorId) {
        //组合参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("vendorId", vendorId);
        return priceChangeRuleMapper.selectByName(map);
    }

    @Override
    public List<Map<String, Object>> selRuleByVendorPriceType(Map<String, Object> params) {
        return priceChangeRuleMapper.selRuleByVendorPriceType(params);
    }

    @Override
    public int deleteSnapshot(Long price_change_rule_id) {
        return priceChangeRuleMapper.deleteSnapshot(price_change_rule_id);
    }

    @Override
    public List<ImPriceAlgorithm> selectAlgorithmsByConditions(Map<String, Object> params) {
        return imPriceAlgorithmMapper.selectByParams(params);
    }

    @Override
    public ImPriceAlgorithm getAlgorithmById(Long id) {
        return imPriceAlgorithmMapper.selectByPrimaryKey(id);
    }
}
