package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.PriceChangeRule;
import pk.shoplus.model.PriceChangeRuleCategoryBrand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceChangeRuleCategoryBrandService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRuleCategoryBrand> priceChangeRuleCategoryBrandDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleCategoryBrandService(Connection conn) {
        priceChangeRuleCategoryBrandDao = new EntityDao<PriceChangeRuleCategoryBrand>(conn);
    }

    /**
     * 创建 PriceChangeRuleCategoryBrand 数据
     *
     * @param priceChangeRuleCategoryBrand
     * @return
     * @throws Exception
     */
    public PriceChangeRuleCategoryBrand createPriceChangeRule(PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand) throws Exception {
        try {
            Long price_change_rule_category_brand_id = priceChangeRuleCategoryBrandDao.create(priceChangeRuleCategoryBrand);
            if (price_change_rule_category_brand_id > 0) {
                priceChangeRuleCategoryBrand.price_change_rule_category_brand_id = price_change_rule_category_brand_id;
            } else {
                priceChangeRuleCategoryBrand = null;
            }
            return priceChangeRuleCategoryBrand;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 priceChangeRuleCategoryBrand 信息
     *
     * @param priceChangeRuleCategoryBrand
     * @throws Exception
     */
    public void updatePriceChangeRule(PriceChangeRuleCategoryBrand priceChangeRuleCategoryBrand) throws Exception {
        try {
            priceChangeRuleCategoryBrandDao.updateById(priceChangeRuleCategoryBrand);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询priceChangeRuleCategoryBrand信息
     *
     * @param condition
     * @return
     */
    public PriceChangeRuleCategoryBrand getPriceChangeRuleByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleCategoryBrand> list = priceChangeRuleCategoryBrandDao.getByCondition(PriceChangeRuleCategoryBrand.class, fieldNames, condition);
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
     * 根据 priceChangeRuleCategoryBrandId删除priceChangeRuleCategoryBrand
     *
     * @param priceChangeRuleCategoryBrandId
     */
    public void deletePriceChangeRuleCategoryBrandById(long priceChangeRuleCategoryBrandId) {
        try {
            priceChangeRuleCategoryBrandDao.deleteById(PriceChangeRuleCategoryBrand.class, priceChangeRuleCategoryBrandId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件删除PriceChangeRuleCategoryBrand
     *
     * @param condition
     */
    public void deletePriceChangeRuleCategoryBrandByCondition(Map<String, Object> condition) {
        try {
            priceChangeRuleCategoryBrandDao.deleteByCondition(PriceChangeRuleCategoryBrand.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件查询PriceChangeRuleCategoryBrandList信息
     *
     * @param condition
     * @return
     */
    public List<PriceChangeRuleCategoryBrand> getPriceChangeRuleCategoryBrandListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleCategoryBrand> list = priceChangeRuleCategoryBrandDao.getByCondition(PriceChangeRuleCategoryBrand.class, fieldNames, condition);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
