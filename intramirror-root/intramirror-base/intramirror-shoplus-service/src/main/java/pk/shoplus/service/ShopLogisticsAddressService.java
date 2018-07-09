package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ShopLogisticsAddress;
import pk.shoplus.parameter.EnabledType;

public class ShopLogisticsAddressService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ShopLogisticsAddress> shopLogisticsAddressDao = null;

	/**
	 * @param conn
	 */
	public ShopLogisticsAddressService(Connection conn) {
		shopLogisticsAddressDao = new EntityDao<ShopLogisticsAddress>(conn);
	}

	// 根据id 找 ShopLogisticsAddress
	public ShopLogisticsAddress getShopLogisticsAddressById(long shop_logistics_address_id) throws Exception {
		try {
			ShopLogisticsAddress shopLogisticsAddress = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("shop_logistics_address_id", shop_logistics_address_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ShopLogisticsAddress> shopLogisticsAddressList = shopLogisticsAddressDao
					.getByCondition(ShopLogisticsAddress.class, conditionMap);
			for (ShopLogisticsAddress temp : shopLogisticsAddressList) {
				if (temp != null) {
					shopLogisticsAddress = new ShopLogisticsAddress();
					shopLogisticsAddress = temp;
					break;
				}
			}
			return shopLogisticsAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param ShopLogisticsAddress
	 * @throws Exception
	 */
	public ShopLogisticsAddress createShopLogisticsAddress(ShopLogisticsAddress shopLogisticsAddress) throws Exception {
		try {
			Long shop_logistics_address_id = shopLogisticsAddressDao.create(shopLogisticsAddress);
			if (shop_logistics_address_id > 0) {
				shopLogisticsAddress.shop_logistics_address_id = shop_logistics_address_id;
			} else {
				shopLogisticsAddress = null;
			}
			return shopLogisticsAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 shopLogisticsAddress list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<ShopLogisticsAddress> getShopLogisticsAddressListByCondition(Map<String, Object> conditionMap)
			throws Exception {
		try {
			// 查询
			List<ShopLogisticsAddress> shopLogisticsAddressList = shopLogisticsAddressDao
					.getByCondition(ShopLogisticsAddress.class, conditionMap);
			return shopLogisticsAddressList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改shopLogisticsAddress信息
	 * 
	 * @param product
	 * @throws Exception
	 */
	public void updateShopLogisticsAddress(ShopLogisticsAddress shopLogisticsAddress) throws Exception {
		try {
			shopLogisticsAddressDao.updateById(shopLogisticsAddress);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据userId查询申请表
	 * 
	 * @param user_id
	 * @return
	 */
	public ShopLogisticsAddress getShopLogisticsAddressByShopId(Long shop_id) throws Exception {
		try {
			ShopLogisticsAddress shopLogisticsAddress = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			// 添加条件
			condition.put("shop_id", shop_id);
			condition.put("enabled", EnabledType.USED);

			List<ShopLogisticsAddress> list = shopLogisticsAddressDao.getByCondition(ShopLogisticsAddress.class,
					condition);

			for (ShopLogisticsAddress temp : list) {
				if (temp != null) {
					shopLogisticsAddress = new ShopLogisticsAddress();
					shopLogisticsAddress = temp;
					break;
				}
			}
			return shopLogisticsAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	// public ShopLogisticsAddress create(ShopLogisticsAddress
	// shopLogisticsAddress) throws Exception {
	// try {
	// Long id = shopLogisticsAddressDao.create(shopLogisticsAddress);
	// if (id > 0) {
	// shopLogisticsAddress.shop_logistics_address_id = id;
	// } else {
	// shopLogisticsAddress = null;
	// }
	// return shopLogisticsAddress;
	// } catch (Exception e) {
	// throw e;
	// }
	// }

}
