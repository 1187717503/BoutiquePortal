package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PriceChangeRuleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceChangeRuleGroupService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRuleGroup> priceChangeRuleGroupDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleGroupService(Connection conn) {
        priceChangeRuleGroupDao = new EntityDao<PriceChangeRuleGroup>(conn);
    }

    /**
     * 创建 priceChangeRuleGroup 数据
     *
     * @param priceChangeRuleGroup
     * @return
     * @throws Exception
     */
    public PriceChangeRuleGroup createPriceChangeRuleGroup(PriceChangeRuleGroup priceChangeRuleGroup) throws Exception {
        try {
            Long price_change_rule_group_id = priceChangeRuleGroupDao.create(priceChangeRuleGroup);
            if (price_change_rule_group_id > 0) {
                priceChangeRuleGroup.price_change_rule_group_id = price_change_rule_group_id;
            } else {
                priceChangeRuleGroup = null;
            }
            return priceChangeRuleGroup;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 priceChangeRuleGroup 信息
     *
     * @param priceChangeRuleGroup
     * @throws Exception
     */
    public void updatePriceChangeRuleGroup(PriceChangeRuleGroup priceChangeRuleGroup) throws Exception {
        try {
            priceChangeRuleGroupDao.updateById(priceChangeRuleGroup);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询PriceChangeRuleGroup信息
     *
     * @param condition
     * @return
     */
    public PriceChangeRuleGroup getPriceChangeRuleGroupByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleGroup> list = priceChangeRuleGroupDao.getByCondition(PriceChangeRuleGroup.class, fieldNames, condition);
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
     * 根据 priceChangeRuleGroupId删除priceChangeRuleGroup
     *
     * @param priceChangeRuleGroupId
     */
    public void deletePriceChangeRuleGroupById(long priceChangeRuleGroupId) {
        try {
            priceChangeRuleGroupDao.deleteById(PriceChangeRuleGroup.class, priceChangeRuleGroupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件删除pricehangeRuleGroup
     *
     * @param condition
     */
    public void deletePriceChangeRuleGroupByCondition(Map<String, Object> condition) {
        try {
            priceChangeRuleGroupDao.deleteByCondition(PriceChangeRuleGroup.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据条件查询PriceChangeRuleGroupList信息
     *
     * @param condition
     * @return
     */
    public List<PriceChangeRuleGroup> getPriceChangeRuleGroupListByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleGroup> list = priceChangeRuleGroupDao.getByCondition(PriceChangeRuleGroup.class, fieldNames, condition);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


        /**
         * supply center product detail
         *
         * @param priceChangeRuleId
         * @return
         */
    public List<Map<String, Object>> getPriceChangeRuleAndProductGroupListByCondition(Long priceChangeRuleId) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT (100-pcrg.discount_percentage) AS dis_p ,pcrg.*, pg.* FROM price_change_rule_group pcrg ")
                .append(" LEFT JOIN product_group pg ON  pcrg.product_group_id=pg.product_group_id ")
                .append(" WHERE pcrg.price_change_rule_id ="+priceChangeRuleId+" ");


        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = priceChangeRuleGroupDao.executeBySql(sb.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }
}
