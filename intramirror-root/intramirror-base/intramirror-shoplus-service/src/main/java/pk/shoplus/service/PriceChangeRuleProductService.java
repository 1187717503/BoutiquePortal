package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PriceChangeRuleCategoryBrand;
import pk.shoplus.model.PriceChangeRuleProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceChangeRuleProductService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRuleProduct> priceChangeRuleProductDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleProductService(Connection conn) {
        priceChangeRuleProductDao = new EntityDao<PriceChangeRuleProduct>(conn);
    }

    /**
     * 创建 priceChangeRuleProduct 数据
     *
     * @param priceChangeRuleProduct
     * @return
     * @throws Exception
     */
    public PriceChangeRuleProduct createPriceChangeRuleProduct(PriceChangeRuleProduct priceChangeRuleProduct) throws Exception {
        try {
            Long price_change_rule_product_id = priceChangeRuleProductDao.create(priceChangeRuleProduct);
            if (price_change_rule_product_id > 0) {
                priceChangeRuleProduct.price_change_rule_product_id = price_change_rule_product_id;
            } else {
                priceChangeRuleProduct = null;
            }
            return priceChangeRuleProduct;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 priceChangeRuleProduct 信息
     *
     * @param priceChangeRuleProduct
     * @throws Exception
     */
    public void updatePriceChangeRuleProduct(PriceChangeRuleProduct priceChangeRuleProduct) throws Exception {
        try {
            priceChangeRuleProductDao.updateById(priceChangeRuleProduct);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询PriceChangeRuleProduct信息
     *
     * @param condition
     * @return
     */
    public PriceChangeRuleProduct getPriceChangeRuleProductByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleProduct> list = priceChangeRuleProductDao.getByCondition(PriceChangeRuleProduct.class, fieldNames, condition);
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
     * 根据 priceChangeRuleProductId删除priceChangeRuleProduct
     *
     * @param priceChangeRuleProductId
     */
    public void deletePriceChangeRuleProductById(long priceChangeRuleProductId) {
        try {
            priceChangeRuleProductDao.deleteById(PriceChangeRuleProduct.class, priceChangeRuleProductId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件删除pricehangeRuleProduct
     *
     * @param condition
     */
    public void deletePriceChangeRuleProductByCondition(HashMap<String, Object> condition) {
        try {
            priceChangeRuleProductDao.deleteByCondition(PriceChangeRuleProduct.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件查询PriceChangeRuleProductList信息
     *
     * @param condition
     * @return
     */
    public List<PriceChangeRuleProduct> getPriceChangeRuleProductListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleProduct> list = priceChangeRuleProductDao.getByCondition(PriceChangeRuleProduct.class, fieldNames, condition);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
