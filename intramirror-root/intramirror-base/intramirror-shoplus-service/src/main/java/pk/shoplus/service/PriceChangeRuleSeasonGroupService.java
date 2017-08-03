package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PriceChangeRuleSeasonGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceChangeRuleSeasonGroupService {

    /**
     * 获取数据库连接
     */
    private EntityDao<PriceChangeRuleSeasonGroup> priceChangeRuleSeasonGroupDao = null;

    /**
     * @param conn
     */
    public PriceChangeRuleSeasonGroupService(Connection conn) {
        priceChangeRuleSeasonGroupDao = new EntityDao<PriceChangeRuleSeasonGroup>(conn);
    }

    /**
     * 创建 PriceChangeRuleSeasonGroup 数据
     *
     * @param priceChangeRuleSeasonGroup
     * @return
     * @throws Exception
     */
    public PriceChangeRuleSeasonGroup createPriceChangeRuleSeasonGroup(PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup) throws Exception {
        try {
            Long price_change_rule_id = priceChangeRuleSeasonGroupDao.create(priceChangeRuleSeasonGroup);
            if (price_change_rule_id > 0) {
                priceChangeRuleSeasonGroup.price_change_rule_id = price_change_rule_id;
            } else {
                priceChangeRuleSeasonGroup = null;
            }
            return priceChangeRuleSeasonGroup;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改 PriceChangeRuleSeasonGroup 信息
     *
     * @param priceChangeRuleSeasonGroup
     * @throws Exception
     */
    public void updatePriceChangeRuleSeasonGroup(PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup) throws Exception {
        try {
            priceChangeRuleSeasonGroupDao.updateById(priceChangeRuleSeasonGroup);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * @return
     * @throws Exception
     */
    public PriceChangeRuleSeasonGroup getPriceChangeRuleSeasonGroupById(long id) throws Exception {
        try {
            PriceChangeRuleSeasonGroup priceChangeRuleSeasonGroup = this.priceChangeRuleSeasonGroupDao.getById(PriceChangeRuleSeasonGroup.class, id);
            return priceChangeRuleSeasonGroup;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 根据条件查询priceChangeRuleSeasonGroup信息
     *
     * @param condition
     * @return
     */
    public PriceChangeRuleSeasonGroup getPriceChangeRuleSeasonGroupByCondition(Map<String, Object> condition, String fieldNames) throws Exception {
        try {
            List<PriceChangeRuleSeasonGroup> list = priceChangeRuleSeasonGroupDao.getByCondition(PriceChangeRuleSeasonGroup.class, fieldNames, condition);
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
     * 根据 priceChangeRuleSeasonGroupId删除PriceChangeRuleSeasonGroup
     *
     * @param priceChangeRuleSeasonGroupId
     */
    public void deletePriceChangeRuleSeasonGroupById(long priceChangeRuleSeasonGroupId) {
        try {
            priceChangeRuleSeasonGroupDao.deleteById(PriceChangeRuleSeasonGroup.class, priceChangeRuleSeasonGroupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deletePriceChangeRuleSeasonGroupByCondition(HashMap<String, Object> condition) {
        try {
            priceChangeRuleSeasonGroupDao.deleteByCondition(PriceChangeRuleSeasonGroup.class, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
