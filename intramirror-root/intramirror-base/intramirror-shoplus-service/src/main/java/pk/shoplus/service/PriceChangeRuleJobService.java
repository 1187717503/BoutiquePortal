package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PriceChangeRule;

import java.util.List;
import java.util.Map;

public class PriceChangeRuleJobService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRule> priceChangeRuleDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleJobService(Connection conn) {
        priceChangeRuleDao = new EntityDao<PriceChangeRule>(conn);
    }


    /**
     * 更新二级类目下所有品牌供货价
     * @throws Exception
     */
    public void updateSupplyPriceByPriceChangeRule() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_category_brand pcrcb,sku s,product p\n" +
                " set s.in_price = s.price * pcrcb.discount_percentage/((1+0.22)*100)\n" +
                " where pcr.price_type = 1 and\n" +
                " pcr.`status` = 1 and\n" +
                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrcb.price_change_rule_id and \n" +
                " pcrcb.exception_flag = 0 and\n" +
                " exists (\n" +
                "\t\tselect c.category_id from category c \n" +
                "\t\t\twhere c.parent_id = pcrcb.category_id and \n" +
                "\t\t\tc.category_id = p.category_id\n" +
                "\t) and\n" +
                " s.product_id = p.product_id and\n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1");

        try {
            System.out.println("updateSupplyPriceByPriceChangeRule:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 根据商品组更新商品价格
     * @throws Exception
     */
    public void updateSupplyPriceByGroup() throws Exception {
        String sql =
        "update price_change_rule pcr , price_change_rule_group pcrg,sku s, product p " +
        "set s.in_price = s.price * pcrg.discount_percentage/((1+0.22)*100) " +
        "where pcr.price_type = 1 and " +
        "pcr.status = 1 and " +
        "pcr.vendor_id = p.vendor_id and " +
        "date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and " +
        "pcr.price_change_rule_id = pcrg.price_change_rule_id and " +
        "s.product_id = p.product_id and " +
        "p.product_group_id = pcrg.product_group_id and " +
        "s.enabled = 1 and " +
        "p.enabled = 1 ";

        try {
            System.out.println("updateSupplyPriceByPriceChangeRule:SQL:" + sql);
            priceChangeRuleDao.updateBySQL(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新特定的品牌类目下的供货价
     * @throws Exception
     */
    public void updateSupplyPriceByPriceChangeRuleExceptions() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_category_brand pcrcb,sku s,product p\n" +
                " set s.in_price = s.price * pcrcb.discount_percentage/((1+0.22)*100)\n" +
                " where pcr.price_type = 1 and\n" +
                " pcr.`status` = 1 and\n" +
                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrcb.price_change_rule_id and \n" +
                " pcrcb.exception_flag = 1 and\n" +
                " p.brand_id = pcrcb.brand_id and\n" +
                " case \n" +
                "\twhen pcrcb.`level` = 3 THEN\n" +
                "\t\t pcrcb.category_id = p.category_id\n" +
                "\twhen pcrcb.`level` = 2 THEN\n" +
                "\t\tEXISTS (\n" +
                "\t\t\tselect * from category c2\n" +
                "\t\t\twhere c2.parent_id = pcrcb.category_id and\n" +
                "\t\t\t\t  c2.category_id = p.category_id\n" +
                "\t\t)\n" +
                "\twhen pcrcb.`level` = 1 THEN\n" +
                "\t\tEXISTS (\n" +
                "\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t  c3.category_id = p.category_id\n" +
                "\t\t\t) and c2.parent_id = pcrcb.category_id\n" +
                "\t\t)\n" +
                " end\n" +
                " and s.product_id = p.product_id and\n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1");

        try {
            System.out.println("updateSupplyPriceByPriceChangeRuleExceptions:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新指定商品的供货价
     * @throws Exception
     */
    public void updateSupplyPriceByPriceChangeRuleProductExceptions() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_product pcrp,sku s,product p\n" +
                " set s.in_price = s.price * pcrp.discount_percentage/((1+0.22)*100)\n" +
                " where pcr.price_type = 1 and\n" +
                " pcr.`status` = 1 and\n" +
                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrp.price_change_rule_id and \n" +
                " pcrp.product_id = p.product_id and\n" +
                " s.product_id = p.product_id and\n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1");

        try {
            System.out.println("updateSupplyPriceByPriceChangeRuleProductExceptions:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新供货价规则状态
     * @throws Exception
     */
    public void updateSupplyPriceChangeRuleStatus() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr\n" +
                " set pcr.`status` = 2\n" +
                " where pcr.price_type = 1 and\n" +
                " pcr.`status` = 1 and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')");

        try {
            System.out.println("updateSupplyPriceChangeRuleStatus:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新供货价规则状态
     * @throws Exception
     */
    public void updateSupplyPriceChangeRuleHisStatus(String vendorIds) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule\n" +
                " set status = 3\n" +
                " where \n" +
                " price_type = 1 and\n" +
                " status = 2 and\n" +
                " vendor_id in ("+vendorIds+")");

        try {
            System.out.println("updateSupplyPriceChangeRuleHisStatus:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getCurrentNewSupplyPriceChangeRule () throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> list = null;
        sb.append("select distinct pcr.vendor_id from price_change_rule pcr\n" +
                "\twhere pcr.price_type = 1 and\n" +
                "\tpcr.`status` = 1 and\n" +
                "\tdate_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')");

        try {
            System.out.println("getCurrentNewSupplyPriceChangeRule:SQL:" + sb.toString());
            list = priceChangeRuleDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return list;
    }

    /**
     * 更新二级类目下所有品牌商品销售价
     * @throws Exception
     */
    public void updateSalePriceByPriceChangeRule() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_category_brand pcrcb,sku s,product p,shop_product_sku sps,shop_product sp\n" +
                " set sps.sale_price = pcrcb.discount_percentage*s.price/100\n" +
                " where pcr.price_type = 2 and\n" +
                " pcr.`status` = 1 and\n" +
//                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrcb.price_change_rule_id and \n" +
                " pcrcb.exception_flag = 0 and\n" +
                " exists (\n" +
                "\t\tselect c.category_id from category c \n" +
                "\t\t\twhere c.parent_id = pcrcb.category_id and \n" +
                "\t\t\tc.category_id = p.category_id\n" +
                "\t) and\n" +
                " s.product_id = p.product_id and\n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1 and \n" +
                " p.product_id = sp.product_id and \n" +
                " pcr.shop_id = sp.shop_id and  \n" +
                " sp.shop_product_id = sps.shop_product_id");

        try {
            System.out.println("updateSalePriceByPriceChangeRule:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据shop商品组更新商品价格
     * @throws Exception
     */
    public void updateSalePriceByGroup() throws Exception {

        String sql =
        "update price_change_rule pcr , price_change_rule_group pcrg,sku s, shop_product sp ,shop_product_sku sps " +
        "set sps.sale_price = s.price * pcrg.discount_percentage/100 " +
        "where pcr.price_type = 2 and " +
        "pcr.status = 1 and " +
        "pcr.shop_id = sp.shop_id and " +
        "date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and " +
        "pcr.price_change_rule_id = pcrg.price_change_rule_id and " +
        "s.product_id = sp.product_id and " +
        "sp.product_group_id = pcrg.product_group_id and " +
        "s.enabled = 1 and " +
        "sp.enabled = 1 " +
        "and sp.shop_product_id = sps.shop_product_id and sps.enabled = 1 ";

        try {
            System.out.println("updateSalePriceByPriceChangeRule:SQL:" + sql);
            priceChangeRuleDao.updateBySQL(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新特定的品牌类目下的销售价
     * @throws Exception
     */
    public void updateSalePriceByPriceChangeRuleExceptions() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_category_brand pcrcb,sku s,product p,shop_product_sku sps,shop_product sp\n" +
                " set sps.sale_price = pcrcb.discount_percentage*s.price/100\n" +
                " where pcr.price_type = 2 and\n" +
                " pcr.`status` = 1 and\n" +
//                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrcb.price_change_rule_id and \n" +
                " pcrcb.exception_flag = 1 and\n" +
                " p.brand_id = pcrcb.brand_id and\n" +
                " case \n" +
                "\twhen pcrcb.`level` = 3 THEN\n" +
                "\t\t pcrcb.category_id = p.category_id\n" +
                "\twhen pcrcb.`level` = 2 THEN\n" +
                "\t\tEXISTS (\n" +
                "\t\t\tselect * from category c2\n" +
                "\t\t\twhere c2.parent_id = pcrcb.category_id and\n" +
                "\t\t\t\t  c2.category_id = p.category_id\n" +
                "\t\t)\n" +
                "\twhen pcrcb.`level` = 1 THEN\n" +
                "\t\tEXISTS (\n" +
                "\t\t\tselect * from category c2 where exists (\n" +
                "\t\t\t\tselect category_id from category c3 \n" +
                "\t\t\t\twhere c3.parent_id = c2.category_id and \n" +
                "\t\t\t\t\t  c3.category_id = p.category_id\n" +
                "\t\t\t) and c2.parent_id = pcrcb.category_id\n" +
                "\t\t)\n" +
                " end\n" +
                " and s.product_id = p.product_id and\n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1 and \n" +
                " p.product_id = sp.product_id and \n" +
                " pcr.shop_id = sp.shop_id and  \n" +
                " sp.shop_product_id = sps.shop_product_id");

        try {
            System.out.println("updateSalePriceByPriceChangeRuleExceptions:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新指定商品的销售价
     * @throws Exception
     */
    public void updateSalePriceByPriceChangeRuleProductExceptions() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr,price_change_rule_product pcrp,sku s,product p,shop_product_sku sps,shop_product sp\n" +
                " set sps.sale_price = pcrp.discount_percentage*s.price/100\n" +
                " where pcr.price_type = 2 and\n" +
                " pcr.`status` = 1 and\n" +
//                " pcr.vendor_id = p.vendor_id and\n" +
                " date_format(valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d') and\n" +
                " pcr.price_change_rule_id = pcrp.price_change_rule_id and \n" +
                " pcrp.product_id = p.product_id and\n" +
                " s.product_id = p.product_id and \n" +
                " s.enabled = 1 and\n" +
                " p.enabled = 1 and \n" +
                " p.product_id = sp.product_id and \n" +
                " pcr.shop_id = sp.shop_id and  \n" +
                " sp.shop_product_id = sps.shop_product_id");

        try {
            System.out.println("updateSalePriceByPriceChangeRuleProductExceptions:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新销售价规则状态
     * @throws Exception
     */
    public void updateSalePriceChangeRuleStatus() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule pcr\n" +
                " set pcr.`status` = 2\n" +
                " where pcr.price_type = 2 and\n" +
                " pcr.`status` = 1 and\n" +
                " date_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')");

        try {
            System.out.println("updateSalePriceChangeRuleStatus:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateSalePriceChangeRuleHisStatus(String shopIds) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("update price_change_rule\n" +
                " set status = 3\n" +
                " where \n" +
                " price_type = 2 and\n" +
                " status = 2 and\n" +
                " shop_id in ("+shopIds+")");

        try {
            System.out.println("updateSalePriceChangeRuleHisStatus:SQL:" + sb.toString());
            priceChangeRuleDao.updateBySQL(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getCurrentNewSalePriceChangeRule () throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> list = null;
        sb.append("select distinct pcr.price_change_rule_id,pcr.vendor_id,pcr.shop_id from price_change_rule pcr\n" +
                "\twhere pcr.price_type = 2 and\n" +
                "\tpcr.`status` = 1 and\n" +
                "\tdate_format(pcr.valid_from,'%y-%m-%d') = date_format(now(),'%y-%m-%d')");

        try {
            System.out.println("getCurrentNewSalePriceChangeRule:SQL:" + sb.toString());
            list = priceChangeRuleDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return list;
    }

}
