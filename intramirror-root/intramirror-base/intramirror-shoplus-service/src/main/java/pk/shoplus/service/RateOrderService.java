package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.RateOrder;

public class RateOrderService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<RateOrder> rateOrderDao = null;

	/**
	 * @param conn
	 */
	public RateOrderService(Connection conn) {
		rateOrderDao = new EntityDao<RateOrder>(conn);
	}

	/**
	 * 
	 * @param RateOrder
	 * @throws Exception
	 */
	public RateOrder createRateOrder(RateOrder rateOrder) throws Exception {
		try {
			Long rate_order_id = rateOrderDao.create(rateOrder);
			if (rate_order_id > 0) {
				rateOrder.rate_order_id = rate_order_id;
			} else {
				rateOrder = null;
			}
			return rateOrder;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查找rateOrderList
	 */
	public List<RateOrder> getRateOrderListByCondition(Map<String, Object> conditionMap)
			throws Exception {
		try {
			// 查询
			List<RateOrder> rateOrderList = rateOrderDao.getByCondition(RateOrder.class,
					conditionMap);
			return rateOrderList;
		} catch (Exception e) {
			throw e;
		}
	}
}
