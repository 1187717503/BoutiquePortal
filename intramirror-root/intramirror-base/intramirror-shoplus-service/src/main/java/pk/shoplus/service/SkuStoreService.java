package pk.shoplus.service;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;

import org.sql2o.Query;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.EnabledType;

public class SkuStoreService {

    /**
     * 获取数据库连接
     */
    private EntityDao<SkuStore> skuStoreDao = null;

    //private Connection connection;

    /**
     * @param conn
     */
    public SkuStoreService(Connection conn) {
        skuStoreDao = new EntityDao<SkuStore>(conn);
        //connection = conn;
    }

    /**
     * 创建 skuStore
     *
     * @param skuStore
     * @return
     * @throws Exception
     */
    public SkuStore createSkuStore(SkuStore skuStore) throws Exception {
        try {
            Long sku_store_id = skuStoreDao.create(skuStore);
            if (sku_store_id > 0) {
                skuStore.sku_store_id = sku_store_id;
            } else {
                skuStore = null;
            }
            return skuStore;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改skuStore信息
     *
     * @param skuStore
     * @throws Exception
     */
    public void updateSkuStore(SkuStore skuStore) throws Exception {
        try {
            skuStoreDao.updateById(skuStore);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean ifUpdateStock(Long sku_store_id) throws Exception {
        SkuStore skuStore = this.getSkuStoreByID(sku_store_id.toString());
        if (skuStore == null) {
            return false;
        }

        String sql = "select pe.id from `product_exception`  pe  where pe.`status`  = 1 and pe.`sku_id`  =" + skuStore.getSku_id();
        List<Map<String, Object>> mapList = skuStoreDao.executeBySql(sql, null);
        if (mapList == null || mapList.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Update sku stock.
     * The real stock(store) = stock - ordered - reserved,
     * if store < 0 then set as oversold that clear = -store and set store as 0.
     */
	/*public void updateStock(Long skuStoreId, Long stock) {
		String sql = "UPDATE sku_store SET store = (" +
				"CASE WHEN " +
					"(:stock - ordered - reserved) >= 0 THEN (:stock - ordered - reserved) " +
				"ELSE 0 END" +
				"), clear = (" +
				"CASE WHEN " +
					"(:stock - ordered - reserved) >= 0 THEN 0 " +
				"ELSE -(:stock - ordered - reserved) END"+
				") " +
				"WHERE sku_store_id = :id";
		Query query = skuStoreDao.createQuery(sql);
		query.addParameter("stock", stock);
		query.addParameter("id", skuStoreId);
		query.executeUpdate();
	}*/
    public void updateStock(Long skuStoreId, Long stock) {
        String sql = "UPDATE sku_store SET store = (" + "CASE WHEN " + "(:p1 - ordered - reserved) >= 0 THEN (:p2 - ordered - reserved) " + "ELSE 0 END"
                + "), clear = (" + "CASE WHEN " + "(:p3 - ordered - reserved) >= 0 THEN 0 " + "ELSE -(:p4 - ordered - reserved) END" + ") "
                + "WHERE sku_store_id = :p5";
        Query query = skuStoreDao.createQuery(sql, new Object[] { stock, stock, stock, stock, skuStoreId });
        //query.addParameter("stock", stock);
        //query.addParameter("id", skuStoreId);
        query.executeUpdate();
    }

    /**
     * 根据sku service 获取skustore
     *
     * @param id
     * @return
     * @throws Exception
     */
    public SkuStore getSkuStoreBySkuId(Long id) throws Exception {
        try {
            SkuStore skuStore = null;
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", EnabledType.USED);
            condition.put("sku_id", id);
            List<SkuStore> list = skuStoreDao.getByCondition(SkuStore.class, condition);
            for (SkuStore temp : list) {
                if (temp != null) {
                    skuStore = temp;
                    break;
                }
            }
            return skuStore;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过product_id获取 skustore 的map
     *
     * @param product_id
     * @throws Exception
     * @retur
     */
    public Map<String, Object> getSkuStoreByProductId(String fieldNames, Long product_id) throws Exception {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", EnabledType.USED);
            condition.put("product_id", product_id);
            String tableNames = "sku_store";
            Map<String, Object> result = skuStoreDao.sumByCondition(fieldNames, tableNames, condition);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    public SkuStore getSkuStoreByID(String storeId) throws Exception {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("enabled", EnabledType.USED);
            condition.put("sku_store_id", storeId);
            String tableNames = "sku_store";
            List<SkuStore> result = skuStoreDao.getByCondition(SkuStore.class, condition);
            if (!CollectionUtils.isEmpty(result)) {
                return result.get(0);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // 根据product列表 获取ProductPropertyKeyList
    public List<Map<String, Object>> getSkuStoreListByProduct(String whereStringInput) throws Exception {

        // store,remind,ordered,confirm,ship,finished,returned
        // SELECT k.product_id, sum(store), sum(remind), sum(ordered),
        // sum(confirm), sum(ship), sum(finished), sum(returned) FROM sku_store
        // k WHERE EXISTS (SELECT a.product_id FROM product a where k.product_id
        // = a.product_id and (a.product_id = 27 or a.product_id = 2) ) GROUP BY
        // product_id
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append(
                    "SELECT k.product_id, sum(store), sum(remind), sum(ordered), sum(confirm), sum(ship), sum(finished), sum(returned), sum(changed), sum(clear) FROM sku_store k ");
            sql.append("WHERE EXISTS (");
            sql.append("SELECT a.product_id FROM product a where k.product_id = a.product_id ");
            if (whereStringInput.length() > 0) {
                sql.append(" and (" + whereStringInput + ") ");
            }
            sql.append(") GROUP BY product_id");

            System.out.println("excutesql:" + sql.toString());

            result = skuStoreDao.executeBySql(sql.toString(), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public SkuStore getSkuStoreByCondition(Map<String, Object> condition) {
        try {
            List<SkuStore> list = skuStoreDao.getByCondition(SkuStore.class, condition);
            if (list != null) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSkuStoreByCondition(Map<String, Object> condition) throws Exception {
        try {
            // delete
            skuStoreDao.deleteByCondition(SkuStore.class, condition);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * api------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */
    private static final Logger logger = Logger.getLogger(SkuStoreService.class);

    public List<Map<String, Object>> api_select_store(String product_code, String size, String vendor_id) throws Exception {
		/*String sql = "select s.sku_id, ss.`sku_store_id` ,ss.`confirmed` ,ss.`store` ,ss.`reserved`,s.price,p.product_id,DATE_FORMAT(ss.`last_check`,'%Y-%m-%d %H:%i:%s') as last_check   from `product` p  \n"
				+ "inner join `sku` s on(s.`product_id` = p.`product_id`  and s.`enabled`  = 1 and p.`enabled`  = 1)\n"
				+ "inner join `sku_store`  ss on(s.`sku_id` = ss.`sku_id`  and ss.`enabled`  = 1)\n"
				+ "inner join `sku_property`  sp on(s.`sku_id` = sp.`sku_id`  and sp.`enabled`  = 1)\n"
				+ "inner join `product_sku_property_value`  pspv on(sp.`product_sku_property_value_id` = pspv.`product_sku_property_value_id`  and pspv.`enabled`  = 1)\n"
				+ "where p.`vendor_id`  = "+vendor_id+" and trim(p.`product_code`)  = '"+product_code+"' and trim(pspv.`value`) = '"+size+"'";*/
        String sql = "select s.sku_id,\n" + "       ss.`sku_store_id`,\n" + "       ss.`confirmed`,\n" + "       ss.`store`,\n" + "       ss.`reserved`,\n"
                + "       s.price,\n" + "       p.product_id,\n" + "       DATE_FORMAT(ss.`last_check`, '%Y-%m-%d %H:%i:%s') as last_check\n"
                + "  from `product` p\n" + "  inner join `sku` s on(s.`product_id`= p.`product_id`and s.`enabled`= 1\n" + "   and p.`enabled`= 1)\n"
                + "  inner join `sku_store` ss on(s.`sku_id`= ss.`sku_id`\n" + "   and ss.`enabled`= 1)\n" + " where p.`vendor_id`= " + vendor_id + "\n"
                + "   and p.`product_code`= \"" + product_code + "\"\n" + "   and s.`size`=  \"" + size + "\" ";
        logger.info("SkuStoreService,api_select_store,sql:" + sql);
        return skuStoreDao.executeBySql(sql, null);
    }

    public List<Map<String, Object>> api_select_store(String sku_code, String vendor_id) throws Exception {
        /*String sql =
                "select s.sku_id, ss.`sku_store_id` ,ss.`confirmed` ,ss.`store` ,ss.`reserved`,s.price,p.product_id,DATE_FORMAT(ss.`last_check`,'%Y-%m-%d %H:%i:%s') as last_check   from `product` p  \n"
                        + "inner join `sku` s on(s.`product_id` = p.`product_id`  and s.`enabled`  = 1 and p.`enabled`  = 1)\n"
                        + "inner join `sku_store`  ss on(s.`sku_id` = ss.`sku_id`  and ss.`enabled`  = 1)\n"
                        + "inner join `sku_property`  sp on(s.`sku_id` = sp.`sku_id`  and sp.`enabled`  = 1)\n"
                        + "inner join `product_sku_property_value`  pspv on(sp.`product_sku_property_value_id` = pspv.`product_sku_property_value_id`  and pspv.`enabled`  = 1)\n"
                        + "where p.`vendor_id`  = " + vendor_id + " and trim(s.`sku_code`) = '" + sku_code + "'";*/
        String sql =
                "select s.sku_id, ss.`sku_store_id` ,ss.`confirmed` ,ss.`store` ,ss.`reserved`,s.price,p.product_id,DATE_FORMAT(ss.`last_check`,'%Y-%m-%d %H:%i:%s') as last_check   from `product` p  \n"
                        + "inner join `sku` s on(s.`product_id` = p.`product_id`  and s.`enabled`  = 1 and p.`enabled`  = 1)\n"
                        + "inner join `sku_store`  ss on(s.`sku_id` = ss.`sku_id`  and ss.`enabled`  = 1)\n" + "where p.`vendor_id`  = " + vendor_id
                        + " and trim(s.`sku_code`) = '" + sku_code + "'";
        logger.info("SkuStoreService,api_select_store,sql:" + sql);
        return skuStoreDao.executeBySql(sql, null);
    }
}
