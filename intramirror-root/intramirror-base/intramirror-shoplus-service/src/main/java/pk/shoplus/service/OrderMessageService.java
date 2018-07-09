package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.OrderMessage;

public class OrderMessageService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<OrderMessage> orderMessageDao = null;

	/**
	 * @param conn
	 */
	public OrderMessageService(Connection conn) {
		orderMessageDao = new EntityDao<OrderMessage>(conn);
	}

	/**
	 * 
	 * @param OrderMessage
	 * @throws Exception
	 */
	public OrderMessage createOrderMessage(OrderMessage orderMessage) throws Exception {
		try {
			Long order_message_id = orderMessageDao.create(orderMessage);
			if (order_message_id > 0) {
				orderMessage.order_message_id = order_message_id;
			} else {
				orderMessage = null;
			}
			return orderMessage;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 orderMessageList
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<OrderMessage> getOrderMessageListByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<OrderMessage> orderMessageList = orderMessageDao.getByCondition(OrderMessage.class, conditionMap);
			return orderMessageList;
		} catch (Exception e) {
			throw e;
		}
	}

}
