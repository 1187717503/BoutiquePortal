package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.EventRushToBuy;
import pk.shoplus.model.EventRushToBuySku;
import pk.shoplus.model.ShopProductSku;

/**
 * 
 * @author 作者：zhangxq
 *
 */
public class EventRushToBuyService {

	/**
	 * 获取数据库连接
	 */
	// private EntityDao<Event> eventDao = null;
	private EntityDao<EventRushToBuy> eventRushToBuyDao = null;
	private EntityDao<EventRushToBuySku> eventRushToBuySkuDao = null;
	private EntityDao<ShopProductSku> shopProductSkuDao = null;

	/**
	 * @param conn
	 */
	public EventRushToBuyService(Connection conn) {
		// eventDao = new EntityDao<Event>(conn);
		eventRushToBuyDao = new EntityDao<EventRushToBuy>(conn);
		eventRushToBuySkuDao = new EntityDao<EventRushToBuySku>(conn);
		shopProductSkuDao = new EntityDao<ShopProductSku>(conn);
	}

	public EventRushToBuy getEventRushToBuy(Long id) throws Exception {
		return eventRushToBuyDao.getById(EventRushToBuy.class, id);
	}

	public Long createEventRushToBuy(EventRushToBuy rushToBuy) throws Exception {
		Long id = eventRushToBuyDao.create(rushToBuy);
		return id;
	}

	public void updateEventRushToBuy(EventRushToBuy rushToBuy) throws Exception {
		eventRushToBuyDao.updateById(rushToBuy);
	}

	public void saveEventRushToBuySkuList(List<EventRushToBuySku> rushToBuySkuList) throws Exception {
		for (EventRushToBuySku t : rushToBuySkuList) {
			eventRushToBuySkuDao.create(t);
		}
	}

	public void updateEvenToBuySku(EventRushToBuySku sku) throws Exception {
		eventRushToBuySkuDao.updateById(sku);
	}

	public EventRushToBuySku getEventRushToBuySku(Long id) throws Exception {
		return eventRushToBuySkuDao.getById(EventRushToBuySku.class, id);
	}

	public void deleteEventRushToBuy(Long id) throws Exception {
		Object[] params = new Object[] { id };
		String deleteSkuSql = " where event_rush_to_buy_id=:p1";
		String deleteSql = " where event_rush_to_buy_id=:p1";
		eventRushToBuySkuDao.deleteBySql(EventRushToBuySku.class, deleteSkuSql, params);
		eventRushToBuyDao.deleteBySql(EventRushToBuy.class, deleteSql, params);
	}

	public Map<String, Object> getExistRushInfoByShopProductId(Long shopProductId) {
		/*
		 * SELECT er.*,e.start_at,e.end_at FROM event_rush_to_buy er INNER JOIN
		 * EVENT e ON e.event_id=er.event_id WHERE e.start_at<=SYSDATE() AND
		 * e.end_at>=DATE(SYSDATE()) AND er.shop_product_id=11 AND er.enabled=1
		 * AND e.enabled=1;
		 * 
		 */
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT er.*,e.start_at,e.end_at FROM event_rush_to_buy er INNER JOIN EVENT e ON e.event_id=er.event_id")
				.append(" WHERE e.start_at<=SYSDATE() AND e.end_at>=DATE(SYSDATE()) AND er.shop_product_id=:p1 AND er.enabled=1 AND e.enabled=1");
		Object[] params = new Object[] { shopProductId };

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = eventRushToBuyDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}

	}

	public List<Map<String, Object>> getEventRushToBuyList(Long eventId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT t.event_rush_to_buy_id,p.`name`,p.cover_img,spsg.min_sale_price,spsg.max_sale_price,rsg.min_rush_price,rsg.max_rush_price,rsg.quantity,rsg.remaining,t.max_up_to_percentage FROM event_rush_to_buy t")
				.append(" LEFT JOIN shop_product sp ON sp.shop_product_id=t.shop_product_id")
				.append(" LEFT JOIN product p ON p.product_id=sp.product_id")
				.append(" LEFT JOIN (SELECT sps.shop_product_id,MIN(sps.sale_price) min_sale_price,MAX(sps.sale_price) max_sale_price FROM shop_product_sku sps WHERE sps.enabled=1 AND sps.shop_product_id IN (SELECT tt.shop_product_id FROM event_rush_to_buy tt WHERE tt.event_id=:p1)")
				.append(" GROUP BY sps.shop_product_id) spsg ON spsg.shop_product_id=t.shop_product_id")
				.append(" LEFT JOIN (SELECT rs.event_rush_to_buy_id,MIN(rs.rush_price) min_rush_price,MAX(rs.rush_price) max_rush_price,sum(quantity) quantity,sum(remaining) remaining FROM event_rush_to_buy_sku rs WHERE rs.`event_rush_to_buy_id` IN (SELECT tt.event_rush_to_buy_id FROM event_rush_to_buy tt WHERE tt.event_id=:p2)")
				.append(" GROUP BY rs.event_rush_to_buy_id) rsg ON rsg.event_rush_to_buy_id=t.event_rush_to_buy_id")
				.append(" WHERE  t.enabled=1 AND t.event_id=:p3").append("");
		Object[] params = new Object[] { eventId, eventId, eventId };

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = eventRushToBuyDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 
	 * @param eventId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getEventRushToBuyProductList(Long eventId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT t.event_rush_to_buy_id,p.`name`,p.cover_img,spsg.min_sale_price,spsg.max_sale_price,spsg.min_price,spsg.max_price,rsg.min_rush_price,rsg.max_rush_price,rsg.quantity,rsg.remaining,t.max_up_to_percentage,sks.stock,sks.sales FROM event_rush_to_buy t")
				.append(" LEFT JOIN shop_product sp ON sp.shop_product_id=t.shop_product_id")
				.append(" LEFT JOIN product p ON p.product_id=sp.product_id")
				.append(" LEFT JOIN (SELECT sps.shop_product_id,MIN(sps.sale_price) min_sale_price,MAX(sps.sale_price) max_sale_price,MAX(sk.price) max_price,MIN(sk.price) min_price FROM shop_product_sku sps INNER JOIN sku sk ON sk.sku_id=sps.sku_id WHERE sps.enabled=1 ")
				.append(" AND sps.shop_product_id IN (SELECT tt.shop_product_id FROM event_rush_to_buy tt WHERE tt.event_id=:p1) GROUP BY sps.shop_product_id) spsg ON spsg.shop_product_id=t.shop_product_id")
				.append(" LEFT JOIN (SELECT rs.event_rush_to_buy_id,MIN(rs.rush_price) min_rush_price,MAX(rs.rush_price) max_rush_price,sum(quantity) quantity,sum(remaining) remaining FROM event_rush_to_buy_sku rs ")
				.append(" WHERE rs.`event_rush_to_buy_id` IN (SELECT tt.event_rush_to_buy_id FROM event_rush_to_buy tt WHERE tt.event_id=:p2) GROUP BY rs.event_rush_to_buy_id) rsg ON rsg.event_rush_to_buy_id=t.event_rush_to_buy_id")
				.append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s WHERE  s.enabled=1 and s.product_id in (SELECT tt.product_id FROM event_rush_to_buy tt WHERE tt.event_id=:p3) ")
				.append(" GROUP BY product_id) sks ON sks.product_id=t.product_id")
				.append(" WHERE  t.enabled=1 AND t.event_id=:p4").append("");
		Object[] params = new Object[] { eventId, eventId, eventId, eventId };

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = eventRushToBuyDao.executeBySql(sb.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ShopProductSku> getShopProductSkuList(Long shopProductId) throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("enabled", true);
		condition.put("shop_product_id", shopProductId);
		List<ShopProductSku> list = shopProductSkuDao.getByCondition(ShopProductSku.class, condition);
		return list;
	}

	public List<Map<String, Object>> getAddProductList(Long shopId, Integer status, String orderBy) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT t.product_id,t3.`name`,t3.cover_img,t.`status`,t3.valid_at,t1.max_price,t1.min_price,t1.max_sale_price,t1.min_sale_price,t1.max_raise_price,t2.stock,t2.sales,t.shop_product_id FROM shop_product t")
				.append(" LEFT JOIN product t3 ON t3.product_id=t.product_id")
				.append(" LEFT JOIN (SELECT shop_sku.`shop_product_id`,MAX(sku.`price`) max_price,MIN(sku.`price`) min_price,MAX(shop_sku.`sale_price`) max_sale_price,MIN(shop_sku.`sale_price`) min_sale_price,MAX(shop_sku.`sale_price`-sku.`price`) max_raise_price")
				.append(" FROM shop_product_sku shop_sku INNER JOIN sku sku ON sku.`sku_id`=shop_sku.`sku_id` WHERE shop_sku.enabled=1 and shop_sku.shop_product_id in (SELECT t.shop_product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p1 and t.status=:p2)")
				.append(" GROUP BY shop_sku.`shop_product_id`)  t1 ON t1.shop_product_id=t.shop_product_id")
				.append(" LEFT JOIN (SELECT product_id,SUM(store) stock,SUM(finished) sales FROM sku_store s WHERE  s.enabled=1 and s.product_id in (SELECT t.product_id FROM shop_product t WHERE t.enabled=1 AND t.shop_id=:p3 and t.status=:p4) GROUP BY product_id) t2 ON t2.product_id=t.product_id")
				.append(" where t.enabled=1 and t.shop_id=:p5 and t.status=:p6")
				.append(" and t.shop_product_id not in(")
				.append(" SELECT er.shop_product_id FROM event_rush_to_buy er INNER JOIN EVENT e ON e.event_id=er.event_id")
				.append(" WHERE er.enabled=1 AND e.end_at>=DATE(SYSDATE()) AND e.shop_id=:p7)");
		List<Object> params = new ArrayList<Object>();
		params.add(shopId);
		params.add(status);
		params.add(shopId);
		params.add(status);
		params.add(shopId);
		params.add(status);
		params.add(shopId);
		sb.append(" order by " + orderBy);
		// System.err.println(sb.toString());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = eventRushToBuyDao.executeBySql(sb.toString(), params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> getRushSkuList(Long rushToBuyId) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT rs.event_rush_to_buy_sku_id,t.shop_product_id,sku.sku_code,shop_sku.sale_price sale_price,sku.price price,shop_sku.shop_product_sku_id,shop_sku.sku_id,sku_store.store stock,rs.rush_price,rs.quantity,rs.remaining FROM event_rush_to_buy t")
				.append(" LEFT JOIN shop_product sp ON sp.shop_product_id=t.shop_product_id")
				.append(" inner join product product on product.product_id=sp.product_id and product.enabled=1")
				.append(" inner join shop_product_sku shop_sku on shop_sku.shop_product_id=t.shop_product_id and shop_sku.enabled=1")
				.append(" inner join sku sku on sku.sku_id=shop_sku.sku_id and sku.enabled=1")
				.append(" left join  sku_store sku_store on sku_store.sku_id=shop_sku.sku_id")
				.append(" INNER JOIN event_rush_to_buy_sku rs ON rs.shop_product_sku_id=shop_sku.shop_product_sku_id")
				.append(" where t.event_rush_to_buy_id=:p1");
		StringBuilder sbSub = new StringBuilder();
		sbSub.append(
				"SELECT sku_p.sku_property_id,sku_p.sku_id,pro_key.`name` key_name,pro_val.`value` key_value FROM `sku_property` sku_p")
				.append(" inner JOIN product_sku_property_key pro_key on sku_p.product_sku_property_key_id=pro_key.product_sku_property_key_id and pro_key.enabled=1")
				.append(" inner JOIN product_sku_property_value pro_val on sku_p.product_sku_property_value_id=pro_val.product_sku_property_value_id and pro_val.enabled=1")
				.append(" where sku_p.enabled=1 and sku_p.sku_id=:p1");

		Object[] params = new Object[] { rushToBuyId };

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
		try {
			list = eventRushToBuyDao.executeBySql(sb.toString(), params);
			for (Map<String, Object> map : list) {
				Long skuId = Long.parseLong(map.get("sku_id").toString());
				Object[] skuParams = new Object[] { skuId };
				sublist = eventRushToBuyDao.executeBySql(sbSub.toString(), skuParams);
				map.put("sku_propertys", sublist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

}
