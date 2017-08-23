package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.PriceChangeRule;
import pk.shoplus.model.Product;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceChangeRuleService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRule> priceChangeRuleDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleService(Connection conn) {
        priceChangeRuleDao = new EntityDao<PriceChangeRule>(conn);
    }

    /**
     * 创建 PriceChangeRule 数据
     *
     * @param priceChangeRule
     * @return
     * @throws Exception
     */
    public PriceChangeRule createPriceChangeRule(PriceChangeRule priceChangeRule) throws Exception {
        try {
            Long price_change_rule_id = priceChangeRuleDao.create(priceChangeRule);
            if (price_change_rule_id > 0) {
                priceChangeRule.price_change_rule_id = price_change_rule_id;
            } else {
                priceChangeRule = null;
            }
            return priceChangeRule;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 PriceChangeRule 信息
     *
     * @param priceChangeRule
     * @throws Exception
     */
    public void updatePriceChangeRule(PriceChangeRule priceChangeRule) throws Exception {
        try {
            priceChangeRuleDao.updateById(priceChangeRule);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * @return
     * @throws Exception
     */
    public PriceChangeRule getPriceChangeRuleById(long id) throws Exception {
        try {
            PriceChangeRule priceChangeRule = this.priceChangeRuleDao.getById(PriceChangeRule.class, id);
            return priceChangeRule;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询priceChangeRule信息
     *
     * @param condition
     * @return
     */
    public PriceChangeRule getPriceChangeRuleByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRule> list = priceChangeRuleDao.getByCondition(PriceChangeRule.class, fieldNames, condition);
            if (list != null) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * 根据 priceChangeRuleId删除PriceChangeRule
     *
     * @param priceChangeRuleId
     */
    public void deletePriceChangeRuleById(long priceChangeRuleId) {
        try {
            priceChangeRuleDao.deleteById(PriceChangeRule.class, priceChangeRuleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deletePriceChangeRuleByCondition(HashMap<String, Object> condition) {
        try {
            priceChangeRuleDao.deleteByCondition(PriceChangeRule.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * supply center getSeasonCode
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getSeasonCode() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT distinct upper(value) AS value FROM product_property WHERE type = 1 AND key_name = 'SeasonCode' AND enabled = 1");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = priceChangeRuleDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    /**
     * supply center getSeasonCode
     *
     * @param
     * @return
     */
    public List<Map<String, Object>> getSeasonCodeByVendor(Long userId) throws Exception {

        StringBuilder sb = new StringBuilder();
        Object[] param = new Object[]{userId};
        sb.append("SELECT season_code AS value \n" +
                " FROM season where enabled  = 1");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = priceChangeRuleDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }


    public Map<String, Object> getCurrentChangeRuleByProduct(Product product) throws Exception {
        Long categoryId = product.category_id;
        Long brandId = product.brand_id;
        String boutique_id = product.product_code;
        Long vendorId = product.vendor_id;
        Long product_group_id = product.product_group_id;
        String season_code = product.season_code;
        if(categoryId == null) {categoryId = -1L;}
        if(brandId == null) {brandId = -1L;}
        if(boutique_id == null) {boutique_id = "";}
        if(vendorId == null) {vendorId = -1L;}
        if(product_group_id == null) {product_group_id = -1L;}
        if(season_code == null) {season_code ="";}

        Map<String,Object> map = new HashMap<>();
        map.put("price1","100");
        map.put("price3","100");

        // select product rule
        List<Map<String,Object>> selProductRuleByVendor = this.selProductRuleSql("1",vendorId,boutique_id,season_code);
        List<Map<String,Object>> selProductRuleByAdmin = this.selProductRuleSql("3",vendorId,boutique_id,season_code);

        // select product group rule
        List<Map<String,Object>> selProductGroupRuleByVendor = this.selProductGroupRuleSql("1",vendorId,product_group_id,season_code);
        List<Map<String,Object>> selProductGroupRuleByAdmin = this.selProductGroupRuleSql("3",vendorId,product_group_id,season_code);

        // select product category brand rule 1
        List<Map<String,Object>> selProductCategoryBrandRule1ByVendor = this.selProductCategoryBrandRuleSql1("1",vendorId,0L,categoryId,season_code);
        List<Map<String,Object>> selProductCategoryBrandRule1ByAdmin = this.selProductCategoryBrandRuleSql1("3",vendorId,0L,categoryId,season_code);

        // select product category brand rule 0
        List<Map<String,Object>> selProductCategoryBrandRule0ByVendor = this.selProductCategoryBrandRuleSql0("1",vendorId,brandId,categoryId,season_code);
        List<Map<String,Object>> selProductCategoryBrandRule0ByAdmin = this.selProductCategoryBrandRuleSql0("3",vendorId,brandId,categoryId,season_code);

        // select default
        List<Map<String,Object>> sel1Default = priceChangeRuleDao.executeBySql("select sp.system_property_value from system_property sp where sp.system_property_name = 'Boutique_Discount_Default'",null);
        List<Map<String,Object>> sel3Default = priceChangeRuleDao.executeBySql("select sp.system_property_value from system_property sp where sp.system_property_name = 'IM_Discount_Default'",null);

        if(selProductRuleByVendor != null && selProductRuleByVendor.size() > 0) {
            map.put("price1",selProductRuleByVendor.get(0).get("discount_percentage").toString());
        } else if(selProductGroupRuleByVendor != null && selProductGroupRuleByVendor.size() > 0) {
            map.put("price1",selProductGroupRuleByVendor.get(0).get("discount_percentage").toString());
        } else if(selProductCategoryBrandRule1ByVendor != null && selProductCategoryBrandRule1ByVendor.size() > 0){
            map.put("price1",selProductCategoryBrandRule1ByVendor.get(0).get("discount_percentage").toString());
        } else if(selProductCategoryBrandRule0ByVendor != null && selProductCategoryBrandRule0ByVendor.size() > 0) {
            map.put("price1",selProductCategoryBrandRule0ByVendor.get(0).get("discount_percentage").toString());
        } else if(sel1Default != null && sel1Default.size() > 0) {
            map.put("price1",sel1Default.get(0).get("system_property_value").toString());
        }

        if(selProductRuleByAdmin != null && selProductRuleByAdmin.size() > 0) {
            map.put("price3",selProductRuleByAdmin.get(0).get("discount_percentage").toString());
        } else if(selProductGroupRuleByAdmin != null && selProductGroupRuleByAdmin.size() > 0) {
            map.put("price3",selProductGroupRuleByAdmin.get(0).get("discount_percentage").toString());
        } else if(selProductCategoryBrandRule1ByAdmin != null && selProductCategoryBrandRule1ByAdmin.size() > 0){
            map.put("price3",selProductCategoryBrandRule1ByAdmin.get(0).get("discount_percentage").toString());
        } else if(selProductCategoryBrandRule0ByAdmin != null && selProductCategoryBrandRule0ByAdmin.size() > 0) {
            map.put("price3",selProductCategoryBrandRule0ByAdmin.get(0).get("discount_percentage").toString());
        } else if(sel3Default != null && sel3Default.size() > 0) {
            map.put("price3",sel3Default.get(0).get("system_property_value").toString());
        }
        return map;
    }

    private List<Map<String,Object>> selProductRuleSql(String price_type,Long vendor_id,String boutique_id,String season_code){
        List<Map<String,Object>> mapList = new ArrayList<>();
        try {
            String sql = "select distinct discount_percentage from price_change_rule pcr \n" +
                    "inner join price_change_rule_product pcrp on (pcr.price_change_rule_id = pcrp.price_change_rule_id)\n" +
                    "inner join price_change_rule_season_group pcrsg on(pcr.price_change_rule_id = pcrsg.price_change_rule_id)\n"+
                    "where pcr.price_type ="+price_type+" and pcr.`status` = 2 and pcr.vendor_id ="+vendor_id+" and pcrp.boutique_id = '"+boutique_id+"'\n" +
                    "and pcrsg.enabled = 1 and pcrsg.season_code = '"+season_code+"'";
            mapList = priceChangeRuleDao.executeBySql(sql,null);;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private List<Map<String,Object>> selProductGroupRuleSql(String price_type,Long vendor_id,Long product_group_id,String season_code){
        List<Map<String,Object>> mapList = new ArrayList<>();
        try {
            String sql = "select distinct discount_percentage from price_change_rule pcr \n" +
                    "inner join price_change_rule_group pcrg on(pcr.price_change_rule_id = pcrg.price_change_rule_id)\n" +
                    "inner join price_change_rule_season_group pcrsg on(pcr.price_change_rule_id = pcrsg.price_change_rule_id)\n"+
                    "where pcr.price_type ="+price_type+" and pcr.`status` = 2 and pcr.vendor_id ="+vendor_id+" and pcrg.product_group_id ="+product_group_id+"\n"+
                    "and pcrsg.enabled = 1 and pcrsg.season_code = '"+season_code+"'";
            mapList = priceChangeRuleDao.executeBySql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private List<Map<String,Object>> selProductCategoryBrandRuleSql1(String price_type,Long vendor_id,Long brand_id,Long category_id,String season_code){
        List<Map<String,Object>> mapList = new ArrayList<>();
        try {
            String sql = "select distinct discount_percentage from price_change_rule pcr\n" +
                    "inner join price_change_rule_category_brand pcrcb on (pcr.price_change_rule_id = pcrcb.price_change_rule_id)\n" +
                    "inner join price_change_rule_season_group pcrsg on(pcr.price_change_rule_id = pcrsg.price_change_rule_id)\n"+
                    "where pcr.price_type ="+price_type+" and pcr.`status` = 2 and pcr.vendor_id ="+vendor_id+" and pcrcb.brand_id ="+brand_id+" and pcrcb.category_id in (\n"+
                    "select c.parent_id from category c where c.category_id ="+category_id+")\n"+
                    "and pcrsg.enabled = 1 and pcrsg.season_code = '"+season_code+"'";
            mapList = priceChangeRuleDao.executeBySql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private List<Map<String,Object>> selProductCategoryBrandRuleSql0(String price_type,Long vendor_id,Long brand_id,Long category_id,String season_code){
        List<Map<String,Object>> mapList = new ArrayList<>();
        try {
            String sql = "select distinct discount_percentage from price_change_rule pcr\n" +
                    "inner join price_change_rule_category_brand pcrcb on (pcr.price_change_rule_id = pcrcb.price_change_rule_id)\n" +
                    "inner join price_change_rule_season_group pcrsg on(pcr.price_change_rule_id = pcrsg.price_change_rule_id)\n"+
                    "where pcr.price_type ="+price_type+" and pcr.`status` = 2 and pcr.vendor_id ="+vendor_id+" and pcrcb.brand_id ="+brand_id+" and pcrcb.category_id in (\n"+
                    "select c.parent_id from category c where c.category_id ="+category_id+")\n"+
                    "and pcrsg.enabled = 1 and pcrsg.season_code = '"+season_code+"'";
            mapList = priceChangeRuleDao.executeBySql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }

    /*StringBuilder sb = new StringBuilder();
    List<Map<String, Object>> list = null;
    Object[] param = new Object[]{categoryId, brandId, boutique_id, vendorId};

        sb.append("select distinct pcrcb.discount_percentage\n" +
                "\tfrom price_change_rule pcr,price_change_rule_category_brand pcrcb\n" +
                "\twhere pcr.price_change_rule_id = pcrcb.price_change_rule_id and pcr.price_type = 1 and pcr.vendor_id = :p4 and \n" +
                "\tpcrcb.exception_flag = 0 and \n" +
                "\texists (\n" +
                "\t\tselect c.category_id from category c \n" +
                "\t\t\twhere c.parent_id = pcrcb.category_id and \n" +
                "\t\t\tc.category_id = :p1 \n" +
                "\t)");

        sb.append(" and not exists (\n" +
                "\t\tselect pcrcb2.discount_percentage\n" +
                "\t\tfrom price_change_rule pcr2,price_change_rule_category_brand pcrcb2\n" +
                "\t\twhere pcr2.price_change_rule_id = pcrcb2.price_change_rule_id and pcr2.price_type = 1 and pcr2.vendor_id = :p4 and  \n" +
                "\t\tpcrcb2.exception_flag = 1 and \n" +
                "\t\tpcrcb2.brand_id = :p2 and\n" +
                "\t\tcase \n" +
                "\t\t\twhen pcrcb2.`level` = 3 THEN\n" +
                "\t\t\t\t pcrcb2.category_id = :p1\n" +
                "\t\t\twhen pcrcb2.`level` = 2 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2\n" +
                "\t\t\t\t\twhere c2.parent_id = pcrcb2.category_id and\n" +
                "\t\t\t\t\t\t  c2.category_id = :p1\n" +
                "\t\t\t\t)\n" +
                "\t\t\twhen pcrcb2.`level` = 1 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t\t\t  c3.category_id = :p1\n" +
                "\t\t\t\t\t) and c2.parent_id = pcrcb2.category_id\n" +
                "\t\t\t\t)\n" +
                "\t\tend\n" +
                "\t)");

        sb.append("  and not exists (\n" +
                "\t\tselect pcrp.discount_percentage\n" +
                "\t\tfrom price_change_rule_product pcrp\n" +
                "\t\twhere pcrp.boutique_id = :p3\n" +
                "\t)");

        sb.append(" union all\n" +
                "\tselect pcrcb2.discount_percentage\n" +
                "\t\tfrom price_change_rule pcr2,price_change_rule_category_brand pcrcb2\n" +
                "\t\twhere pcr2.price_change_rule_id = pcrcb2.price_change_rule_id and pcr2.price_type = 1 and pcr2.vendor_id = :p4 and  \n" +
                "\t\tpcrcb2.exception_flag = 1 and \n" +
                "\t\tpcrcb2.brand_id = :p2 and\n" +
                "\t\tcase \n" +
                "\t\t\twhen pcrcb2.`level` = 3 THEN\n" +
                "\t\t\t\t pcrcb2.category_id = :p1\n" +
                "\t\t\twhen pcrcb2.`level` = 2 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2\n" +
                "\t\t\t\t\twhere c2.parent_id = pcrcb2.category_id and\n" +
                "\t\t\t\t\t\t  c2.category_id = :p1\n" +
                "\t\t\t\t)\n" +
                "\t\t\twhen pcrcb2.`level` = 1 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t\t\t  c3.category_id = :p1\n" +
                "\t\t\t\t\t) and c2.parent_id = pcrcb2.category_id\n" +
                "\t\t\t\t)\n" +
                "\t\tend");

        sb.append(" union all\n" +
                "\tselect pcrp.discount_percentage\n" +
                "\tfrom price_change_rule_product pcrp \n" +
                "\twhere pcrp.boutique_id = :p3");

    list = priceChangeRuleDao.executeBySql(sb.toString(), param);*/

    public List<Map<String, Object>> getSalesPriceChangeRuleByProduct(Long categoryId, Long brandId, String productId) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> list = null;
        Object[] param = new Object[]{categoryId, brandId, productId};

        sb.append("select distinct pcrcb.discount_percentage\n" +
                "\tfrom price_change_rule pcr,price_change_rule_category_brand pcrcb\n" +
                "\twhere pcr.price_change_rule_id = pcrcb.price_change_rule_id and pcr.price_type = 2 and \n" +
                "\tpcrcb.exception_flag = 0 and \n" +
                "\texists (\n" +
                "\t\tselect c.category_id from category c \n" +
                "\t\t\twhere c.parent_id = pcrcb.category_id and \n" +
                "\t\t\tc.category_id = :p1 \n" +
                "\t)");

        sb.append(" and not exists (\n" +
                "\t\tselect pcrcb2.discount_percentage\n" +
                "\t\tfrom price_change_rule pcr2,price_change_rule_category_brand pcrcb2\n" +
                "\t\twhere pcr2.price_change_rule_id = pcrcb2.price_change_rule_id and pcr2.price_type = 2 and  \n" +
                "\t\tpcrcb2.exception_flag = 1 and \n" +
                "\t\tpcrcb2.brand_id = :p2 and\n" +
                "\t\tcase \n" +
                "\t\t\twhen pcrcb2.`level` = 3 THEN\n" +
                "\t\t\t\t pcrcb2.category_id = :p1\n" +
                "\t\t\twhen pcrcb2.`level` = 2 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2\n" +
                "\t\t\t\t\twhere c2.parent_id = pcrcb2.category_id and\n" +
                "\t\t\t\t\t\t  c2.category_id = :p1\n" +
                "\t\t\t\t)\n" +
                "\t\t\twhen pcrcb2.`level` = 1 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t\t\t  c3.category_id = :p1\n" +
                "\t\t\t\t\t) and c2.parent_id = pcrcb2.category_id\n" +
                "\t\t\t\t)\n" +
                "\t\tend\n" +
                "\t)");

        sb.append("  and not exists (\n" +
                "\t\tselect pcrp.discount_percentage\n" +
                "\t\tfrom price_change_rule_product pcrp\n" +
                "\t\twhere pcrp.product_id = :p3\n" +
                "\t)");

        sb.append(" union all\n" +
                "\tselect pcrcb2.discount_percentage\n" +
                "\t\tfrom price_change_rule pcr2,price_change_rule_category_brand pcrcb2\n" +
                "\t\twhere pcr2.price_change_rule_id = pcrcb2.price_change_rule_id and pcr2.price_type = 2 and  \n" +
                "\t\tpcrcb2.exception_flag = 1 and \n" +
                "\t\tpcrcb2.brand_id = :p2 and\n" +
                "\t\tcase \n" +
                "\t\t\twhen pcrcb2.`level` = 3 THEN\n" +
                "\t\t\t\t pcrcb2.category_id = :p1\n" +
                "\t\t\twhen pcrcb2.`level` = 2 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2\n" +
                "\t\t\t\t\twhere c2.parent_id = pcrcb2.category_id and\n" +
                "\t\t\t\t\t\t  c2.category_id = :p1\n" +
                "\t\t\t\t)\n" +
                "\t\t\twhen pcrcb2.`level` = 1 THEN\n" +
                "\t\t\t\tEXISTS (\n" +
                "\t\t\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t\t\t  c3.category_id = :p1\n" +
                "\t\t\t\t\t) and c2.parent_id = pcrcb2.category_id\n" +
                "\t\t\t\t)\n" +
                "\t\tend");

        sb.append(" union all\n" +
                "\tselect pcrp.discount_percentage\n" +
                "\tfrom price_change_rule_product pcrp \n" +
                "\twhere pcrp.product_id = :p3");

        list = priceChangeRuleDao.executeBySql(sb.toString(), param);

        return list;
    }

    /**
     * 获取PriceChangeRule已经相关库存信息
     *
     * @param pageNumber
     * @param pageSize
     * @param fieldNames
     * @param orderBy
     * @return
     * @throws Exception
     */
    public Page getPriceChangeRulePageWithStore(Long pageNumber, Long pageSize,
                                                String fieldNames, String orderBy, Integer priceType, Long userId) throws Exception {

        try {
            // 先计数
            StringBuilder countSql = new StringBuilder();
            Object[] params = null;
            if (priceType == 1l) {
                countSql.append("select count(*) from price_change_rule a where a.price_type=:p1 and a.vendor_id=:p2");
                params = new Object[]{priceType, userId};
            } else {
                countSql.append("select count(*) from price_change_rule a where a.price_type=:p1 and a.shop_id=:p2");
                params = new Object[]{priceType, userId};
            }
            System.out.println("count sql: " + countSql.toString());


            // 1. 获取 总数
            Long totalRow = priceChangeRuleDao.createQuery(countSql.toString(), params).executeScalar(Long.class);

            if (totalRow == null || totalRow <= 0) {
                return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
            }

            // 2. 获取 分页
            StringBuilder sql = new StringBuilder("");
            if (Helper.isNullOrEmpty(fieldNames)) {
                fieldNames = "*";
            }

            if (priceType == 1l) {
                sql.append("SELECT a.*,v.vendor_name,sg.season_code FROM price_change_rule a left join price_change_rule_season_group sg on a.price_change_rule_id=sg.price_change_rule_id inner join vendor v on a.vendor_id = v.vendor_id where a.price_type=:p1 and a.vendor_id=:p2");
            } else {
                sql.append("SELECT a.*,s.shop_name,sg.season_code FROM price_change_rule a left join price_change_rule_season_group sg on a.price_change_rule_id=sg.price_change_rule_id inner join shop s on a.shop_id = s.shop_id  where a.price_type=:p1 and a.shop_id=:p2");
            }
            // order by
            if (!Helper.isNullOrEmpty(orderBy)) {
                sql.append(" order by ").append(orderBy);
            }

            // pageSize或者pageNumber
            // 如果小于1就返回全部数据
            if (pageSize < 1) {
                return new Page();
            }

            if (pageNumber < 1) {
                return new Page();
            }

            // 计数
            long totalPage = totalRow / pageSize;
            if (totalRow % pageSize != 0) {
                totalPage++;
            }
            if (pageNumber > totalPage) {
                return new Page();
            }

            // 拼MySQL分页
            long offset = pageSize * (pageNumber - 1);
            sql.append(" limit ").append(offset).append(",").append(pageSize);
            System.out.println("excutesql:" + sql.toString());
            List<Map<String, Object>> productMapList = priceChangeRuleDao.executeBySql(sql.toString(), params);
            return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
