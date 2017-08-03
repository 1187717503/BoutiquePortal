package pk.shoplus.service;

import java.math.BigDecimal;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.ShippingCost;
import pk.shoplus.parameter.EnabledType;

public class ShippingCostService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ShippingCost> shippingCostDao = null;

	/**
	 * @param conn
	 */
	public ShippingCostService(Connection conn) {
		shippingCostDao = new EntityDao<ShippingCost>(conn);
	}

	/**
	 * 创建运费信息
	 * 
	 * @param shippingCost
	 * @return 实体/null
	 * @throws Exception
	 */
	public ShippingCost createShippingCost(ShippingCost shippingCost) throws Exception {
		try {
			long shippingCostId = shippingCostDao.create(shippingCost);
			if (shippingCostId > 0) {
				shippingCost.shipping_cost_id = shippingCostId;
			} else {
				shippingCost = null;
			}
			return shippingCost;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改运费信息
	 * 
	 * @param shippingCost
	 * @throws Exception
	 */
	public void updateShippingCost(ShippingCost shippingCost, Map<String, Object> condition) throws Exception {
		try {
			shippingCostDao.updateByCondition(shippingCost, condition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改运费信息
	 * 
	 * @param shippingCost
	 * @throws Exception
	 */
	public void updateShippingCostById(ShippingCost shippingCost) throws Exception {
		try {
			shippingCostDao.updateById(shippingCost);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过shipping_cost_id 获取 ShippingCost
	 * 
	 * @param shipping_cost_id
	 * @return
	 * @throws Exception
	 */
	public ShippingCost getShippingCostByCityId(int city_id) throws Exception {
		try {
			ShippingCost shippingCost = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("address_city_id", city_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ShippingCost> shippingCostList = shippingCostDao.getByCondition(ShippingCost.class, conditionMap);

			for (ShippingCost temp : shippingCostList) {
				if (temp != null) {
					shippingCost = new ShippingCost();
					shippingCost = temp;
					break;
				}
			}

			return shippingCost;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过shipping_cost_id 获取 ShippingCost
	 * 
	 * @param shipping_cost_id
	 * @return
	 * @throws Exception
	 */
	public ShippingCost getShippingCostById(Long shipping_cost_id) throws Exception {
		try {
			ShippingCost shippingCost = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("shipping_cost_id", shipping_cost_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ShippingCost> shippingCostList = shippingCostDao.getByCondition(ShippingCost.class, conditionMap);

			for (ShippingCost temp : shippingCostList) {
				if (temp != null) {
					shippingCost = new ShippingCost();
					shippingCost = temp;
					break;
				}
			}

			return shippingCost;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取shippingCostList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public ShippingCost getShippingCostListDefault() throws Exception {
		try {
			ShippingCost shippingCost = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("is_default", true);
			conditionMap.put("address_city_id", 0);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ShippingCost> shippingCostList = shippingCostDao.getByCondition(ShippingCost.class, conditionMap);
			for (ShippingCost temp : shippingCostList) {
				if (temp != null) {
					shippingCost = new ShippingCost();
					shippingCost = temp;
					break;
				}
			}

			if (shippingCost == null) {
				ShippingCost item = new ShippingCost();
				BigDecimal initNum = new BigDecimal(1);
				item.the_first_cost = initNum;
				item.the_first_heavy = initNum;
				item.expend_heavy = initNum;
				item.expend_cost = initNum;
				item.created_at = Helper.getCurrentTimeToUTCWithDate();
				item.updated_at = Helper.getCurrentTimeToUTCWithDate();
				item.enabled = EnabledType.USED;
				item.is_default = true;
				item.address_city_id = Long.parseLong("0");
				shippingCost = createShippingCost(item);
			}

			return shippingCost;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取shippingCostList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public Page getShippingCostList(Page page) throws Exception {
		try {
			String fieldNames = " is_default, the_first_heavy,the_first_cost,expend_heavy, expend_cost,"
					+ " group_concat(shipping_cost.address_city_id) as cityIds, group_concat(name) as cityNames, group_concat(address_province_id) as provinceIds ";
			String tableNames = " shipping_cost left join address_city on shipping_cost.address_city_id = address_city.address_city_id ";
			String groupBy = " the_first_heavy,the_first_cost,expend_heavy, expend_cost, category ";
			String orderBy = " created_at ";
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			String whereCondition = "is_default = 0 and enabled=:p" + i++;
			paramList.add(EnabledType.USED);
			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			return shippingCostDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames, whereCondition,
					orderBy, params, null, groupBy);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 根据id 找 address city
	public ShippingCost getAddressCityById(long address_city_id) throws Exception {
		try {
			ShippingCost shippingCost = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("address_city_id", address_city_id);
			// 查询
			List<ShippingCost> shippingCostList = shippingCostDao.getByCondition(ShippingCost.class, conditionMap);
			for (ShippingCost temp : shippingCostList) {
				if (temp != null) {
					shippingCost = new ShippingCost();
					shippingCost = temp;
					break;
				}
			}
			return shippingCost;
		} catch (Exception e) {
			throw e;
		}
	}
}
