package pk.shoplus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Notification;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.NotificationMsg;
import pk.shoplus.parameter.NotificationRoleType;
import pk.shoplus.parameter.NotificationStatusType;
import pk.shoplus.parameter.ShopCategoryStatusType;

public class NotificationService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<Notification> notificationDao = null;

	/**
	 * @param conn
	 */
	public NotificationService(Connection conn) {
		notificationDao = new EntityDao<Notification>(conn);
	}

	/**
	 * 
	 * SELECT * FROM notification n WHERE n.enabled=1 AND (n.role in (3,4) OR
	 * n.user_id = 897) AND n.send_time <= UTC_TIMESTAMP() AND ( n.category_id
	 * IS NULL OR n.category_id in ( SELECT DISTINCT a.root_category_id FROM (
	 * SELECT c.category_id, COALESCE(c3.category_id, c2.category_id,
	 * c1.category_id, c.category_id) root_category_id FROM category c LEFT JOIN
	 * category c1 ON c1.category_id=c.parent_id AND c1.enabled=1 LEFT JOIN
	 * category c2 ON c2.category_id=c1.parent_id AND c2.enabled=1 LEFT JOIN
	 * category c3 ON c3.category_id=c2.parent_id AND c3.enabled=1 WHERE
	 * c.enabled=1 )a WHERE a.category_id in ( SELECT
	 * GROUP_CONCAT(vc.category_id) FROM vendor v LEFT JOIN vendor_category vc
	 * ON v.vendor_applicant_id = vc.vendor_application_id WHERE v.vendor_id =
	 * 123 ) ) ) ORDER BY n.send_time DESC
	 * 
	 * 
	 * 根据条件分页查询Notification
	 * 
	 * @param page,roles
	 * 
	 * @return
	 * @throws Exception
	 */
	public Page getNotificationListByCondition(Page page, Long userId, Long vendorId, Long ShopId) throws Exception {
		try {
			String fieldNames = "*";
			String tableName = "notification n";
			StringBuilder whereCondition = new StringBuilder("");
			List<Object> paramList = new ArrayList<Object>();

			whereCondition.append("n.enabled=:p1 ")
					.append("AND (n.role =:p2 OR n.role = :p3 OR n.user_id = :p4) ")
					.append("AND n.send_time <= UTC_TIMESTAMP() ");
			paramList.add(EnabledType.USED);
			if (Helper.checkNotNull(vendorId)) {
				paramList.add(NotificationRoleType.VENDOR);
			} else if (Helper.checkNotNull(ShopId)) {
				paramList.add(NotificationRoleType.SHOP);
			} else {
				paramList.add(NotificationRoleType.CUSTOMER);
			}
			paramList.add(NotificationRoleType.ALL);
			paramList.add(userId);

			if (Helper.checkNotNull(vendorId)) {
				whereCondition.append("AND ( n.category_id IS NULL ").append("OR n.category_id in ( ")
						.append("SELECT DISTINCT a.root_category_id FROM ( ")
						.append("SELECT c.category_id, COALESCE(c3.category_id, c2.category_id, c1.category_id, c.category_id) root_category_id ")
						.append("FROM category c ")
						.append("LEFT JOIN category c1 ON c1.category_id=c.parent_id AND c1.enabled=:p5 ")
						.append("LEFT JOIN category c2 ON c2.category_id=c1.parent_id AND c2.enabled=:p6 ")
						.append("LEFT JOIN category c3 ON c3.category_id=c2.parent_id AND c3.enabled=:p7 ")
						.append("WHERE c.enabled=:p8 ) a ").append("WHERE a.category_id in ( ")
						.append("SELECT GROUP_CONCAT(vc.category_id) FROM vendor v ")
						.append("LEFT JOIN vendor_category vc ON v.vendor_applicant_id = vc.vendor_application_id ")
						.append("WHERE v.vendor_id =:p9 ").append(") ) ) ");
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(vendorId);
			} else if (Helper.checkNotNull(ShopId)) {
				whereCondition.append("AND ( n.category_id IS NULL ").append("OR n.category_id in ( ")
						.append("SELECT DISTINCT a.root_category_id FROM ( ")
						.append("SELECT c.category_id, COALESCE(c3.category_id, c2.category_id, c1.category_id, c.category_id) root_category_id ")
						.append("FROM category c ")
						.append("LEFT JOIN category c1 ON c1.category_id=c.parent_id AND c1.enabled=:p5 ")
						.append("LEFT JOIN category c2 ON c2.category_id=c1.parent_id AND c2.enabled=:p6 ")
						.append("LEFT JOIN category c3 ON c3.category_id=c2.parent_id AND c3.enabled=:p7 ")
						.append("WHERE c.enabled=:p8 ) a ").append("WHERE a.category_id in ( ")
						.append("SELECT GROUP_CONCAT(sc.category_id) FROM shop s ")
						.append("LEFT JOIN shop_category sc ON s.shop_id = sc.shop_id AND sc.`status` = :p9 ")
						.append("WHERE s.shop_id=:p10 ").append(") ) ) ");
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(EnabledType.USED);
				paramList.add(ShopCategoryStatusType.ACTIVED);
				paramList.add(ShopId);
			}
			
			String orderBy = " n.send_time DESC ";
			page = notificationDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName,
					whereCondition.toString(), orderBy, paramList.toArray(), null, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * SELECT * FROM notification n WHERE n.enabled=1 AND n.role =2 OR n.role =
	 * 4 OR n.user_id = 901 AND n.send_time <= UTC_TIMESTAMP() AND n.category_id
	 * IS NULL OR n.category_id in ( SELECT DISTINCT a.root_category_id FROM (
	 * SELECT c.category_id, COALESCE(c3.category_id , c2.category_id,
	 * c1.category_id, c.category_id) root_category_id FROM category c LEFT JOIN
	 * category c1 ON c1.category_id=c.parent_id AND c1.enabled=1 LEFT JOIN
	 * category c2 ON c2.category_id=c1.parent_id AND c2.enabled=1 LEFT JOIN
	 * category c3 ON c3.category_id=c2.parent_id AND c3.enabled=1 WHERE
	 * c.enabled=1 )a WHERE a.category_id in ( SELECT
	 * GROUP_CONCAT(sc.category_id) FROM shop s LEFT JOIN shop_category sc ON
	 * s.shop_id = sc.shop_id AND sc.`status` = 1 WHERE s.shop_id=87 ) ) ORDER
	 * BY n.send_time DESC LIMIT 0,6
	 * 
	 * @param userId
	 * @param ShopId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getShopDashboardNotes(Long userId, Long ShopId) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();

		sb.append("SELECT * FROM notification n ").append("WHERE n.enabled=:p1 ")
				.append("AND ( n.role = :p2 OR n.role = :p3 OR n.user_id = :p4 ) ")
				.append("AND n.send_time <= UTC_TIMESTAMP() ");
		paramList.add(EnabledType.USED);
		paramList.add(NotificationRoleType.SHOP);
		paramList.add(NotificationRoleType.ALL);
		paramList.add(userId);

		if (Helper.checkNotNull(ShopId)) {
			sb.append("AND ( n.category_id IS NULL ").append("OR n.category_id in ( ")
					.append("SELECT DISTINCT a.root_category_id FROM ( ")
					.append("SELECT c.category_id, COALESCE(c3.category_id, c2.category_id, c1.category_id, c.category_id) root_category_id ")
					.append("FROM category c ")
					.append("LEFT JOIN category c1 ON c1.category_id=c.parent_id AND c1.enabled=:p5 ")
					.append("LEFT JOIN category c2 ON c2.category_id=c1.parent_id AND c2.enabled=:p6 ")
					.append("LEFT JOIN category c3 ON c3.category_id=c2.parent_id AND c3.enabled=:p7 ")
					.append("WHERE c.enabled=:p8 ) a ").append("WHERE a.category_id in ( ")
					.append("SELECT GROUP_CONCAT(sc.category_id) FROM shop s ")
					.append("LEFT JOIN shop_category sc ON s.shop_id = sc.shop_id AND sc.`status` = :p9 ")
					.append("WHERE s.shop_id=:p10 ").append(") ) ) ");
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(ShopCategoryStatusType.ACTIVED);
			paramList.add(ShopId);
		}
		sb.append("ORDER BY n.send_time DESC ").append("LIMIT 0,6 ");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = notificationDao.executeBySql(sb.toString(), paramList.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public long createNotidicationBySystem(String title, String content, Long userId, Integer notificationType,
			Date sendTime) throws Exception {
		try {
			Date currentTime = Helper.getCurrentTimeToUTCWithDate();
			Notification notification = new Notification();
			notification.title = title;
			notification.content = content;
			notification.pic = NotificationMsg.NOTIFICATION_PIC;
			notification.created_at = currentTime;
			notification.updated_at = currentTime;
			notification.send_time = sendTime;
			notification.type = 0;
			notification.role = notificationType;
			notification.enabled = EnabledType.USED;
			notification.user_id = userId;
			long id = notificationDao.create(notification);
			return id;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param page
	 * @param role
	 * @param period
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public Page getNotificationsList(Page page, String role, String period, int status, Boolean isSystemSend)
			throws Exception {

		try {
			String fieldNames = "*, date_format(send_time,'%Y-%m-%d') as send_date";
			String tableNames = "notification";
			StringBuilder whereCondition = new StringBuilder("");
			List<Object> paramList = new ArrayList<Object>();
			int i = 1;
			whereCondition.append("enabled=:p" + i++);
			paramList.add(EnabledType.USED);

			if (Helper.checkNotNull(isSystemSend)) {
				whereCondition.append(" AND is_system_send = :p" + i++);
				paramList.add(isSystemSend);
			}
			if (!Helper.isNullOrEmpty(role)) {
				whereCondition.append(" AND role = :p" + i++);
				paramList.add(role);
			}
			if (!Helper.isNullOrEmpty(period)) {
				if (Helper.isValidDate(period.split("to")[0].trim())
						&& Helper.isValidDate(period.split("to")[1].trim())) {
					whereCondition.append(" AND date_format(send_time,'%Y-%m-%d') >= '")
							.append(period.split("to")[0].trim()).append("' AND date_format(send_time,'%Y-%m-%d') <= '")
							.append(period.split("to")[1].trim()).append("' ");
				}
			}

			if (status == NotificationStatusType.SENT) {
				whereCondition.append(" and send_time <= now()");
			} else if (status == NotificationStatusType.PENDING) {
				whereCondition.append(" and send_time > now()");
			}

			Object[] params = null;
			if (paramList.size() > 0) {
				params = paramList.toArray();
			}
			String orderBy = "send_time DESC";
			return notificationDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableNames,
					whereCondition.toString(), orderBy, params, null, null);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long createNotifications(Notification notifications) throws Exception {
		try {
			Long notificationsId = notificationDao.create(notifications);
			if (notificationsId != 0) {
				return notificationsId;
			}
		} catch (Exception e) {
			throw e;
		}

		return 0l;
	}

	/**
	 * 根据notifications id 获取 notifications
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Notification getNotificationById(Long id) throws Exception {
		try {
			Notification notification = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("notification_id", id);

			List<Notification> list = this.notificationDao.getByCondition(Notification.class, condition);

			for (Notification temp : list) {
				if (temp != null) {
					notification = temp;
					break;
				}
			}
			return notification;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新notifications信息
	 * 
	 * @param brand
	 * @throws Exception
	 */
	public void updateNotifications(Notification notification) throws Exception {
		try {
			notificationDao.updateById(notification);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * SELECT * FROM notification n WHERE n.enabled=1 AND (n.role =3 OR n.role =
	 * 4 OR n.user_id = 897) AND n.send_time <= UTC_TIMESTAMP() AND (
	 * n.category_id IS NULL OR n.category_id in ( SELECT DISTINCT
	 * a.root_category_id FROM ( SELECT c.category_id, COALESCE(c3.category_id ,
	 * c2.category_id, c1.category_id, c.category_id) root_category_id FROM
	 * category c LEFT JOIN category c1 ON c1.category_id=c.parent_id AND
	 * c1.enabled=1 LEFT JOIN category c2 ON c2.category_id=c1.parent_id AND
	 * c2.enabled=1 LEFT JOIN category c3 ON c3.category_id=c2.parent_id AND
	 * c3.enabled=1 WHERE c.enabled=1 )a WHERE a.category_id in ( SELECT
	 * GROUP_CONCAT(vc.category_id) FROM vendor v LEFT JOIN vendor_category vc
	 * ON v.vendor_applicant_id = vc.vendor_application_id WHERE v.vendor_id =
	 * 123 ) ) ) ORDER BY n.send_time DESC LIMIT 0,6
	 * 
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getVendorDashboardNotifications(Long userId, Long vendorId) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();

		sb.append("SELECT * FROM notification n ").append("WHERE n.enabled=:p1 ")
				.append("AND ( n.role = :p2 OR n.role = :p3 OR n.user_id = :p4 ) ")
				.append("AND n.send_time <= UTC_TIMESTAMP() ");
		paramList.add(EnabledType.USED);
		paramList.add(NotificationRoleType.VENDOR);
		paramList.add(NotificationRoleType.ALL);
		paramList.add(userId);

		if (Helper.checkNotNull(vendorId)) {
			sb.append("AND ( n.category_id IS NULL ").append("OR n.category_id in ( ")
					.append("SELECT DISTINCT a.root_category_id FROM ( ")
					.append("SELECT c.category_id, COALESCE(c3.category_id, c2.category_id, c1.category_id, c.category_id) root_category_id ")
					.append("FROM category c ")
					.append("LEFT JOIN category c1 ON c1.category_id=c.parent_id AND c1.enabled=:p5 ")
					.append("LEFT JOIN category c2 ON c2.category_id=c1.parent_id AND c2.enabled=:p6 ")
					.append("LEFT JOIN category c3 ON c3.category_id=c2.parent_id AND c3.enabled=:p7 ")
					.append("WHERE c.enabled=:p8 ) a ").append("WHERE a.category_id in ( ")
					.append("SELECT GROUP_CONCAT(vc.category_id) FROM vendor v ")
					.append("LEFT JOIN vendor_category vc ON v.vendor_applicant_id = vc.vendor_application_id ")
					.append("WHERE v.vendor_id =:p9 ").append(") ) ) ");
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(EnabledType.USED);
			paramList.add(vendorId);
		}
		sb.append("ORDER BY n.send_time DESC ").append("LIMIT 0,6 ");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			list = notificationDao.executeBySql(sb.toString(), paramList.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
