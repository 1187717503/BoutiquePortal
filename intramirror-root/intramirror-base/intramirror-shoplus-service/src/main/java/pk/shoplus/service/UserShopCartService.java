package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import com.google.gson.JsonArray;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.UserShopCart;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.UserShopCartStatus;

public class UserShopCartService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<UserShopCart> userShopCartDao = null;

	/**
	 * @param conn
	 */
	public UserShopCartService(Connection conn) {
		userShopCartDao = new EntityDao<UserShopCart>(conn);
	}

//	SELECT  usc.user_shop_cart_id,s.shop_id,s.shop_name,usc.shop_product_sku_id,usc.user_shop_cart_id,
//	 usc.quantity,sk.sale_price,usc.sku_id,sk.shop_product_id,p.cover_img,p.`name`, 
//	 GROUP_CONCAT(pspk.`name`) as key_name, GROUP_CONCAT(pspv.`value`) as value_name
//	 FROM user_shop_cart usc LEFT JOIN shop s ON usc.shop_id = s.shop_id
//	 LEFT JOIN shop_product_sku sk ON usc.shop_product_sku_id = sk.shop_product_sku_id
//	 LEFT JOIN product p ON usc.product_id = p.product_id 
//	 LEFT JOIN sku_property sp ON sp.sku_id= usc.sku_id
//	 LEFT JOIN product_sku_property_key pspk on sp.product_sku_property_key_id =pspk.product_sku_property_key_id
//	 LEFT JOIN product_sku_property_value pspv on sp.product_sku_property_value_id = pspv.product_sku_property_value_id
//	 WHERE usc.enabled=1 AND usc.user_id=901 AND usc.`status`=1				 GROUP BY usc.sku_id ORDER BY usc.updated_at
	
	/**
	 * 购物车
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserCartList(String user_id) throws Exception {
		try {
			String getShopList = "select s.shop_id,s.shop_name from user_shop_cart usc left join shop s on usc.shop_id=s.shop_id where usc.enabled=1 and usc.user_id=:p1 and usc.`status`=:p2  group by usc.shop_id";
			List<Map<String, Object>> userCartShopList = userShopCartDao.executeBySql(getShopList,
					new Object[] { user_id, UserShopCartStatus.IN_CART });

			String getProductList = "select usc.shop_product_sku_id,usc.user_shop_cart_id, usc.quantity,sk.sale_price,usc.sku_id,sk.shop_product_id,p.cover_img,p.`name` from user_shop_cart usc left join shop_product_sku sk on usc.shop_product_sku_id = sk.shop_product_sku_id left join product p on usc.product_id = p.product_id "
					+ "where usc.enabled=1 and usc.user_id=:p1 and usc.shop_id=:p2 and usc.`status`=:p3";

			for (int i = 0; i < userCartShopList.size(); i++) {
				Long shop_id = (Long) userCartShopList.get(i).get("shop_id");
				List<Map<String, Object>> productList = userShopCartDao.executeBySql(getProductList,
						new Object[] { user_id, shop_id, UserShopCartStatus.IN_CART });
				userCartShopList.get(i).put("product_list", productList);
			}
			return userCartShopList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Map<String, Object>> getSingleShopInCartList(String shop_id, String user_id) throws Exception {
		try {
			String getShopList = "select s.shop_id,s.shop_name from user_shop_cart usc left join shop s on usc.shop_id=s.shop_id where usc.user_id=:p1 and usc.shop_id=:p2 group by usc.shop_id ";
			List<Map<String, Object>> userCartShopList = userShopCartDao.executeBySql(getShopList,
					new Object[] { user_id, shop_id });
			String getProductList = "select usc.quantity,sk.sale_price,usc.sku_id,sk.shop_product_id,p.cover_img,p.`name` from user_shop_cart usc left join shop_product_sku sk on usc.shop_product_sku_id = sk.shop_product_sku_id left join product p on usc.product_id = p.product_id where usc.user_id=:p1 and usc.shop_id=:p2";
			List<Map<String, Object>> productList = userShopCartDao.executeBySql(getProductList,
					new Object[] { user_id, shop_id });
			for (int j = 0; j < productList.size(); j++) {
				Long sku_id = (Long) userCartShopList.get(0).get("sku_id");
				List<Map<String, Object>> skuPropList = getSkuPropertyListWithKeyAndValueBySkuId(
						String.valueOf(sku_id));
				productList.get(j).put("sku_prop_list", skuPropList);
			}
			userCartShopList.get(0).put("product_list", productList);

			return userCartShopList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public List<Map<String, Object>> getShopCartCheckoutList(JsonArray userShopCartIds) throws Exception {
		List<Map<String, Object>> result = null;
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("select usc.user_shop_cart_id, usc.updated_at,usc.quantity, sla.name as address_name, sla.address, ");
			sql.append("sps.shop_id, sps.name, sps.coverpic, shop.shop_pic, sps.sale_price, ");
			sql.append("sla.country, sla.province, sla.city, sla.telephone, sla.area, sps.shop_product_sku_id, sps.sale_price, sku.in_price, ");
			sql.append("sps.sale_price * usc.quantity as price, shop.shop_name, pi.weight_amount,");
			sql.append("group_concat(pspk.name) as pspk_name, group_concat(pspv.value) as pspv_value ");
			sql.append("from user_shop_cart as usc ");
			sql.append("left join shop on shop.shop_id = usc.shop_id ");
			sql.append("left join shop_product_sku as sps on sps.shop_product_sku_id = usc.shop_product_sku_id ");
			sql.append("left join sku on sku.sku_id = sps.sku_id ");
			sql.append("right join sku_property as sp on sp.sku_id = sps.sku_id ");
			sql.append("left join product_sku_property_key as pspk on pspk.product_sku_property_key_id ");
			sql.append("= sp.product_sku_property_key_id ");
			sql.append("left join product_sku_property_value as pspv on pspv.product_sku_property_value_id ");
			sql.append("= sp.product_sku_property_value_id ");
			sql.append("left join shop_logistics_address as sla on sla.shop_id = usc.shop_id ");
			sql.append("left join product_info as pi on usc.product_id = pi.product_id ");
			sql.append("where ");
			if (null != userShopCartIds && userShopCartIds.size() > 0) {
				sql.append("usc.user_shop_cart_id in( ");
				for (int i = 0; i < userShopCartIds.size(); i++) {
					sql.append(userShopCartIds.get(i));
					if (i < userShopCartIds.size() - 1) {
						sql.append(",");
					}
				}
				sql.append(") and  ");
			}
			sql.append("usc.enabled = :p1 ");
			sql.append("group by sps.sku_id order by usc.created_at");
			System.out.println("shop cart sql = " + sql.toString());
			result = userShopCartDao.executeBySql(sql.toString(), new Object[] { EnabledType.USED });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> getSkuPropertyListWithKeyAndValueBySkuId(String sku_id) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT b.name as key_name, c.value as value_name FROM sku_property p ");
			sql.append(
					"LEFT JOIN product_sku_property_key b on p.product_sku_property_key_id =b.product_sku_property_key_id ");
			sql.append(
					"LEFT JOIN product_sku_property_value c on p.product_sku_property_value_id = c.product_sku_property_value_id ");
			sql.append("WHERE p.sku_id=:p1");
			result = userShopCartDao.executeBySql(sql.toString(), new Object[] { sku_id });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public UserShopCart createUserShopCart(UserShopCart userShopCart) throws Exception {
		try {
			Long user_shop_cart_id = userShopCartDao.create(userShopCart);
			if (user_shop_cart_id > 0) {
				userShopCart.user_shop_cart_id = user_shop_cart_id;
			} else {
				userShopCart = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return userShopCart;
	}

	public boolean getUserShopCartByShopProductSkuId(String shop_product_sku_id, Long user_id) throws Exception {
		boolean hasItem = false;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("shop_product_sku_id", shop_product_sku_id);
			condition.put("user_id", user_id);
			condition.put("status", UserShopCartStatus.IN_CART);

			List<UserShopCart> userShopCart = userShopCartDao.getByCondition(UserShopCart.class, condition);
			if (userShopCart.size() > 0) {
				hasItem = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return hasItem;
	}

	public UserShopCart getUserShopCartById(String user_shop_cart_id) throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("user_shop_cart_id", user_shop_cart_id);
			List<UserShopCart> userShopCart = userShopCartDao.getByCondition(UserShopCart.class, condition);
			if (userShopCart.size() > 0) {
				return userShopCart.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	public void updateUserShopCart(UserShopCart userShopCart) throws Exception {
		try {
			userShopCartDao.updateById(userShopCart);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 通过条件获取UserShopCartList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<UserShopCart> getUserShopCartByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<UserShopCart> userShopCartList = userShopCartDao.getByCondition(UserShopCart.class, fieldName, condition);
			return userShopCartList;
		} catch (Exception e) {
			throw e;
		}
	}

	public Long getCountByUser(Long user_id) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// SELECT COUNT(*) as count FROM user_shop_cart WHERE user_id=233
			// AND enabled=1
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT COUNT(*) as count");
			sql.append(" FROM user_shop_cart WHERE user_id=:p1 AND enabled=:p2 AND status = :p3");
			result = userShopCartDao.executeBySql(sql.toString(), new Object[] { user_id, EnabledType.USED, UserShopCartStatus.IN_CART });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return (long) result.get(0).get("count");
	}
}
