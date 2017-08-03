package pk.shoplus.service;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Event;
import pk.shoplus.model.Page;

/**
 * @author 作者：zhangxq
 *
 */
public class EventService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Event> eventDao = null;

	/**
	 * @param conn
	 */
	public EventService(Connection conn) {
		eventDao = new EntityDao<Event>(conn);
	}

	public Page getEventPage(String type, Long shopId, long pageNumber, long pageSize, String orderBy) {
		// String fieldNames = "";
		StringBuilder sb = new StringBuilder();
		sb.append("`event` e");
		String whereCondition = "e.`enabled`=1 AND e.`type`=:p1 AND e.shop_id=:p2";

		Object[] params = new Object[] { type, shopId };
		Page page = new Page();
		try {
			page = eventDao.getPageBySql(pageNumber, pageSize, null, sb.toString(), whereCondition, orderBy, params,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	public Event getEvent(Long id) throws Exception {
		return eventDao.getById(Event.class, id);
	}

	public Long createEvent(Event event) throws Exception {
		Long eventId = eventDao.create(event);
		return eventId;
	}

	public void updateEvent(Event event) throws Exception {
		eventDao.updateById(event);
	}

}
