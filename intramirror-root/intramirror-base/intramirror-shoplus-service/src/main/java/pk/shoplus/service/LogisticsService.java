package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Logistics;
import pk.shoplus.parameter.EnabledType;

public class LogisticsService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Logistics> logisticsDao = null;

	/**
	 * @param conn
	 */
	public LogisticsService(Connection conn) {
		logisticsDao = new EntityDao<Logistics>(conn);
	}

	/**
	 * 
	 * @param Logistics
	 * @throws Exception
	 */
	public Logistics createLogistics(Logistics logistics) throws Exception {
		try {
			Long logistics_id = logisticsDao.create(logistics);
			if (logistics_id > 0) {
				logistics.logistics_id = logistics_id;
			} else {
				logistics = null;
			}
			return logistics;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 Logistics list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<Logistics> getLogisticsListByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<Logistics> orderLogisticsList = logisticsDao.getByCondition(Logistics.class, conditionMap);
			return orderLogisticsList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据 id 获取 order_logistics
	 * 
	 * @param order_logistics_id
	 * @return
	 * @throws Exception
	 */
	public Logistics getOrderLogisticsByOrderLogisticsId(long order_logistics_id) throws Exception {
		try {
			String fieldName = "logistics_id, order_logistics_id, user_rec_name, user_rec_province, user_rec_city, user_rec_area, user_rec_addr, user_rec_mobile, created_at";
			Logistics logistics = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("order_logistics_id", order_logistics_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<Logistics> logisticsList = logisticsDao.getByCondition(Logistics.class, fieldName,
					conditionMap);
			for (Logistics temp : logisticsList) {
				if (temp != null) {
					logistics = temp;
					break;
				}
			}
			return logistics;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新
	 * 
	 * @param logisticsList
	 * @throws Exception
	 */
	public void updateLogistics(Logistics logistics) throws Exception {
		try {
			logisticsDao.updateById(logistics);
		} catch (Exception e) {
			throw e;
		}
	}
}
