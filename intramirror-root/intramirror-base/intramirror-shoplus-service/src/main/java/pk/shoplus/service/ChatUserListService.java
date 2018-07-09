package pk.shoplus.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Chat;
import pk.shoplus.model.ChatUserList;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

public class ChatUserListService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ChatUserList> chatUserListDao = null;
	private EntityDao<Chat> chatDao = null;

	/**
	 * @param conn
	 */
	public ChatUserListService(Connection conn) {
		chatUserListDao = new EntityDao<ChatUserList>(conn);
		chatDao = new EntityDao<Chat>(conn);
	}

	// 根据user id 找 chat user list
	public List<ChatUserList> getChatUserListByUserId(long user_id) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("user_id", user_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ChatUserList> chatList = chatUserListDao.getByCondition(ChatUserList.class, conditionMap);
			return chatList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据id 找 chat user list
	public Long getOtehrChatUserByUserIdAndChatId(long chat_id, long user_id) throws Exception {
		try {
			ChatUserList chatUser = null;

			String extensiveSql = " WHERE chat_id = :p1 AND user_id != :p2 AND enabled = :p3";
			List<ChatUserList> chatUserList = chatUserListDao.getBySql(ChatUserList.class, "*", extensiveSql,
					new Object[] { chat_id, user_id, EnabledType.USED });

			for (ChatUserList temp : chatUserList) {
				if (temp != null) {
					chatUser = new ChatUserList();
					chatUser = temp;
					break;
				}
			}
			if (Helper.checkNotNull(chatUser)) {
				return chatUser.user_id;
			}
			return 0l;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据id 找 chat user list
	public ChatUserList getChatUserListById(long chat_user_list_id) throws Exception {
		try {
			ChatUserList chatUser = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("chat_user_list_id", chat_user_list_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ChatUserList> chatList = chatUserListDao.getByCondition(ChatUserList.class, conditionMap);
			for (ChatUserList temp : chatList) {
				if (temp != null) {
					chatUser = new ChatUserList();
					chatUser = temp;
					break;
				}
			}
			return chatUser;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据chat_id ChatUserList
	public ChatUserList getChatUserListByChatIdAndUserId(long chat_id, long user_id) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			ChatUserList chatUser = null;
			// 添加条件
			conditionMap.put("chat_id", chat_id);
			conditionMap.put("user_id", user_id);
			conditionMap.put("enabled", EnabledType.USED);
			// 查询
			List<ChatUserList> chatUserList = chatUserListDao.getByCondition(ChatUserList.class, conditionMap);

			for (ChatUserList temp : chatUserList) {
				if (temp != null) {
					chatUser = new ChatUserList();
					chatUser = temp;
					break;
				}
			}
			return chatUser;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param ChatUserList
	 * @throws Exception
	 */
	public ChatUserList createChatUserList(ChatUserList chatUserList) throws Exception {
		try {
			Long chat_user_list_id = chatUserListDao.create(chatUserList);
			if (chat_user_list_id > 0) {
				chatUserList.chat_user_list_id = chat_user_list_id;
			} else {
				chatUserList = null;
			}
			return chatUserList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 chat user list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<ChatUserList> getChatUserListByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<ChatUserList> chatUserList = chatUserListDao.getByCondition(ChatUserList.class, conditionMap);
			return chatUserList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改chat user list信息
	 * 
	 * @param product
	 * @throws Exception
	 */
	public void updateChatUserList(ChatUserList chatUserList) throws Exception {
		try {
			chatUserListDao.updateById(chatUserList);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件分页查询chatUserList
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	public Long getChatUserListChat(long client_id, long user_id) {
		try {
			String fieldNames = "distinct(chat_id)";

			// sql:
			// select distinct(chat_id) FROM cheezmall_dev.chat_user_list where
			// user_id = 1 and chat_id
			// in(SELECT chat_id FROM cheezmall_dev.chat_user_list where user_id
			// = 233)

			// p1 client_id p2 user_id
			String extensiveSql = " WHERE user_id = :p1 AND chat_id IN (SELECT chat_id FROM "
					+ "chat_user_list WHERE user_id = :p2)";

			// 查询
			ChatUserList chatUser = null;
			List<ChatUserList> chatList = chatUserListDao.getBySql(ChatUserList.class, fieldNames, extensiveSql,
					new Object[] { user_id, client_id });
			for (ChatUserList temp : chatList) {
				if (temp != null) {
					chatUser = new ChatUserList();
					chatUser = temp;
					break;
				}
			}

			if (chatUser == null) {
				// create the new chat
				Chat chat = new Chat();
				chat.created_at = Helper.getCurrentTimeToUTCWithDate();
				chat.updated_at = Helper.getCurrentTimeToUTCWithDate();
				chat.enabled = EnabledType.USED;
				chat.chat_id = chatDao.create(chat);

				ChatUserList chatUserList = new ChatUserList();
				chatUserList.chat_id = chat.chat_id;
				chatUserList.user_id = user_id;
				chatUserList.created_at = Helper.getCurrentTimeToUTCWithDate();
				chatUserList.updated_at = Helper.getCurrentTimeToUTCWithDate();
				chatUserList.chat_time = Helper.getCurrentTimeToUTCWithDate();
				chatUserList.enabled = EnabledType.USED;
				chatUserList.chat_user_list_id = chatUserListDao.create(chatUserList);

				ChatUserList chatClientList = new ChatUserList();
				chatClientList.chat_id = chat.chat_id;
				chatClientList.user_id = client_id;
				chatClientList.chat_time = Helper.getCurrentTimeToUTCWithDate();
				chatClientList.created_at = Helper.getCurrentTimeToUTCWithDate();
				chatClientList.updated_at = Helper.getCurrentTimeToUTCWithDate();
				chatClientList.enabled = EnabledType.USED;
				chatClientList.chat_user_list_id = chatUserListDao.create(chatClientList);

				return chat.chat_id;
			} else {
				return chatUser.chat_id;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Long.parseLong("0");
	}

	/**
	 * 根据条件分页查询chatUserList
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getShopChatUserList(Long user_id) {
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT * FROM( SELECT *, (SELECT count(*) FROM " );
			sql.append("(SELECT l.chat_id, l.chat_time AS lchat_time FROM chat_user_list ");
			sql.append("as l WHERE user_id = :p1) AS s ");
			sql.append("LEFT JOIN chat_user_content AS c ON c.chat_id = s.chat_id ");
			sql.append("WHERE c.chat_id = b.chat_id AND  s.chat_id = b.chat_id AND c.user_id != :p1 ");
			sql.append("AND lchat_time < c.created_at ) AS un_read_count ");
			sql.append("FROM (SELECT * FROM (select chat_user_list.chat_id, ");
			sql.append("chat_user_list.user_id, cul.user_id as other_user_id, ");
			sql.append("chat_user_content.content, chat_user_content.created_at AS last_created_at, ");
			sql.append("user.username as shop_name, user.user_image as shop_pic ");
			sql.append("FROM chat_user_list ");
			sql.append("LEFT JOIN chat_user_content on ");
			sql.append("chat_user_list.chat_id = chat_user_content.chat_id  ");
			sql.append("left join chat_user_list as cul on chat_user_list.user_id != cul.user_id and ");
			sql.append("chat_user_list.chat_id = cul.chat_id ");
			sql.append("left join user on ");
			sql.append("(user.user_id = chat_user_list.user_id and user.user_id != :p1) ");
			sql.append("or (user.user_id = cul.user_id and user.user_id != :p1) ");
			sql.append("WHERE chat_user_list.enabled = :p2 ");
			sql.append("ORDER BY chat_user_content.created_at DESC) AS a ");
			sql.append("GROUP BY a.user_id ) AS b group by b.chat_id ) AS d where ");
			sql.append(":p1 in (SELECT user_id FROM chat_user_list WHERE d.chat_id = chat_user_list.chat_id) ");
			sql.append("ORDER BY d.last_created_at DESC ");

			return chatUserListDao.executeBySql(sql.toString(), new Object[] { user_id, EnabledType.USED });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据条件分页查询chatUserList
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getChatUserList(Long user_id) {
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT * FROM( SELECT *, (SELECT count(*) FROM " );
			sql.append("(SELECT l.chat_id, l.chat_time AS lchat_time FROM chat_user_list ");
			sql.append("as l WHERE user_id = :p1) AS s ");
			sql.append("LEFT JOIN chat_user_content AS c ON c.chat_id = s.chat_id ");
			sql.append("WHERE c.chat_id = b.chat_id AND  s.chat_id = b.chat_id AND c.user_id != :p1 ");
			sql.append("AND lchat_time < c.created_at ) AS un_read_count ");
			sql.append("FROM (SELECT * FROM (select chat_user_list.chat_id, ");
			sql.append("chat_user_list.user_id, cul.user_id as other_user_id, ");
			sql.append("chat_user_content.content, chat_user_content.created_at AS last_created_at, ");
			sql.append("user.username as shop_name, user.user_image as shop_pic ");
			sql.append("FROM chat_user_list ");
			sql.append("LEFT JOIN chat_user_content on ");
			sql.append("chat_user_list.chat_id = chat_user_content.chat_id  ");
			sql.append("left join chat_user_list as cul on chat_user_list.user_id != cul.user_id and ");
			sql.append("chat_user_list.chat_id = cul.chat_id ");
			sql.append("left join user on ");
			sql.append("(user.user_id = chat_user_list.user_id and user.user_id != :p1) ");
			sql.append("or (user.user_id = cul.user_id and user.user_id != :p1) ");
			sql.append("WHERE chat_user_list.enabled = :p2 ");
			sql.append("ORDER BY chat_user_content.created_at DESC) AS a ");
			sql.append("GROUP BY a.user_id ) AS b group by b.chat_id ) AS d where ");
			sql.append(":p1 in (SELECT user_id FROM chat_user_list WHERE d.chat_id = chat_user_list.chat_id) ");
			sql.append("ORDER BY d.last_created_at DESC ");
			
			//System.out.println("sql = " + sql);
			//System.out.println("user_id = " + user_id);
			return chatUserListDao.executeBySql(sql.toString(), new Object[] { user_id, EnabledType.USED });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据条件分页查询chatUserList (未启用 分页功能) select *, (select count(*) from
	 * chat_user_content as c, chat_user_list as l where c.chat_id = b.chat_id
	 * and l.chat_id = b.chat_id and l.user_id = 901 and l.chat_time <
	 * c.created_at) as un_read_count from (SELECT * FROM (SELECT
	 * chat_user_list.chat_id, chat_user_list.user_id
	 * ,chat_user_content.content,chat_user_content.created_at AS
	 * last_created_at, shop.shop_name, shop.shop_pic FROM chat_user_list LEFT
	 * JOIN chat_user_content ON chat_user_list.user_id =
	 * chat_user_content.user_id AND chat_user_list.chat_id =
	 * chat_user_content.chat_id RIGHT JOIN shop ON chat_user_list.user_id =
	 * shop.user_id WHERE chat_user_list.enabled = 1 ORDER BY
	 * chat_user_content.created_at DESC) AS a GROUP BY a.user_id) as b group by
	 * b.chat_id order by b.last_created_at desc
	 * 
	 * @param page,
	 *            conditionMap
	 * @return
	 * @throws Exception
	 */
	public Page getChatUserListPage(Long user_id, Page page) {
		try {
			// cheezmall_dev.chat_user_list.chat_id,
			// cheezmall_dev.chat_user_list.user_id,
			// cheezmall_dev.chat_user_content.content,cheezmall_dev.chat_user_content.created_at
			StringBuilder fieldNames = new StringBuilder("");
			fieldNames.append("*, (select count(*) from chat_user_content as c, chat_user_list as l ")
					.append("where c.chat_id = b.chat_id and l.chat_id = b.chat_id and l.user_id = ").append(user_id)
					.append(" and l.chat_time < c.created_at) as un_read_count ");
			StringBuilder tableName = new StringBuilder("");
			tableName.append(" (SELECT * FROM (SELECT chat_user_list.chat_id, chat_user_list.user_id")
					.append(",chat_user_content.content,chat_user_content.created_at AS last_created_at, shop.shop_name, shop.shop_pic ")
					.append("FROM chat_user_list ").append("LEFT JOIN chat_user_content ")
					.append("ON chat_user_list.user_id = chat_user_content.user_id ")
					.append("AND chat_user_list.chat_id = chat_user_content.chat_id ").append("RIGHT JOIN shop ")
					.append("ON chat_user_list.user_id = shop.user_id ").append("WHERE chat_user_list.enabled = :p1 ")
					.append(" ORDER BY chat_user_content.created_at DESC) AS a ").append("GROUP BY a.user_id) as b ");
			String groupBy = "b.chat_id ";
			String orderBy = "b.last_created_at desc";
			page = chatUserListDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames.toString(),
					tableName.toString(), null, orderBy, new Object[] { EnabledType.USED }, null, groupBy);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
}
