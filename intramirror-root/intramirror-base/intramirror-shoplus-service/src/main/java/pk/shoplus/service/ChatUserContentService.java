package pk.shoplus.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ChatUserContent;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

public class ChatUserContentService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<ChatUserContent> chatUserContentDao = null;

	/**
	 * @param conn
	 */
	public ChatUserContentService(Connection conn) {
		chatUserContentDao = new EntityDao<ChatUserContent>(conn);
	}

	// 根据id 找 ChatUserContent
	public ChatUserContent getChatById(long chat_user_content_id) throws Exception {
		try {
			ChatUserContent chatUserContent = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("chat_user_content_id", chat_user_content_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getByCondition(ChatUserContent.class,
					conditionMap);
			for (ChatUserContent temp : chatUserContentList) {
				if (temp != null) {
					chatUserContent = new ChatUserContent();
					chatUserContent = temp;
					break;
				}
			}
			return chatUserContent;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据chat_id ChatUserContent
	public List<ChatUserContent> getAllChatUserContentByChatId(long chat_id) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("chat_id", chat_id);
			conditionMap.put("enabled", EnabledType.USED);
			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getByCondition(ChatUserContent.class,
					conditionMap);

			return chatUserContentList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据chat_id 找 chat user list
	public List<ChatUserContent> getLimitChatUserContentChatId(long chat_id, int limit) throws Exception {
		try {
			// 添加条件
			// SELECT * FROM cheezmall_dev.chat_user_content
			// where chat_id = 1 order by created_at desc limit 0, 1
			String fieldNames = "*";
			String extensiveSql = " WHERE chat_id = :p1 AND enabled = :p2 ORDER BY created_at DESC LIMIT 0, :p3";

			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getBySql(ChatUserContent.class, fieldNames,
					extensiveSql, new Object[] { chat_id, EnabledType.USED, limit });
			return chatUserContentList;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据chat_id 找 chat user list
	public ChatUserContent getFirstChatUserContentChatId(long chat_id) throws Exception {
		try {
			// 添加条件
			// SELECT * FROM cheezmall_dev.chat_user_content
			// where chat_id = 1 order by created_at desc limit 0, 1
			String fieldNames = "*";
			String extensiveSql = " WHERE chat_id = :p1 AND enabled = :p2 ORDER BY created_at DESC LIMIT 0, 1";

			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getBySql(ChatUserContent.class, fieldNames,
					extensiveSql, new Object[] { chat_id, EnabledType.USED });
			ChatUserContent chatUserContent = null;
			for (ChatUserContent temp : chatUserContentList) {
				if (temp != null) {
					chatUserContent = new ChatUserContent();
					chatUserContent = temp;
					break;
				}
			}
			return chatUserContent;
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据user_id 和 chat_id 找 ChatUserContent
	public ChatUserContent getChatUserContentByUserIdAndChatId(long user_id, long chat_id) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("user_id", user_id);
			conditionMap.put("chat_id", chat_id);
			conditionMap.put("enabled", EnabledType.USED);
			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getByCondition(ChatUserContent.class,
					conditionMap);

			ChatUserContent chatUserContent = null;
			for (ChatUserContent temp : chatUserContentList) {
				if (temp != null) {
					chatUserContent = new ChatUserContent();
					chatUserContent = temp;
					break;
				}
			}
			return chatUserContent;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param ChatUserContent
	 * @throws Exception
	 */
	public ChatUserContent createChatUserContent(ChatUserContent chatUserContent) throws Exception {
		try {
			Long chat_user_content_id = chatUserContentDao.create(chatUserContent);
			if (chat_user_content_id > 0) {
				chatUserContent.chat_user_content_id = chat_user_content_id;
			} else {
				chatUserContent = null;
			}
			return chatUserContent;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 chatUserContent list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<ChatUserContent> getChatUserContentByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getByCondition(ChatUserContent.class,
					conditionMap);
			return chatUserContentList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改chatUserContent信息
	 * 
	 * @param product
	 * @throws Exception
	 */
	public void updateChatUserContent(ChatUserContent chatUserContent) throws Exception {
		try {
			chatUserContentDao.updateById(chatUserContent);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询ChatUserContent
	 * 
	 * @param page,
	 *            chat_id
	 * @return
	 * @throws Exception
	 */
	public Page getChatUserContentPage(Page page, long chat_id) {
		try {
			String fieldNames = "*";
			String tableName = "chat_user_content";
			String orderBy = "created_at DESC";
			String whereCondition = "chat_id = :p1 AND enabled = :p2";

			page = chatUserContentDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName,
					whereCondition, orderBy, new Object[] { chat_id, EnabledType.USED }, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 根据条件查询可读count
	 * 
	 * @param chat_id,
	 *            user_id
	 * @return
	 * @throws Exception
	 */
	public long getChatUserContentUnReadCount(long user_id) {
		long resultCount = 0;
		try {
			StringBuilder extensiveSql = new StringBuilder("");
			extensiveSql.append("SELECT count(*) AS unread FROM ")
			.append("( SELECT chat_id, user_id, chat_time FROM chat_user_list WHERE user_id = :p1 AND enabled = :p2) AS l ")
			.append("RIGHT JOIN (select user_id AS cuserid, chat_id, created_at ")
			.append("FROM chat_user_content WHERE user_id != :p1 AND enabled = :p2) as c ")
			.append("ON c.chat_id = l.chat_id WHERE chat_time < created_at");
			
			// 查询
			List<Map<String, Object>> chatUserContentList = chatUserContentDao.executeBySql(extensiveSql.toString(),
					new Object[] { user_id, EnabledType.USED });
			if (chatUserContentList.size() > 0) {
				resultCount = (Long) chatUserContentList.get(0).get("unread");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCount;
	}

	/**
	 * 根据条件查询new chat user content
	 * 
	 * @param chat_id,
	 *            user_id
	 * @return
	 * @throws Exception
	 */
	public List<ChatUserContent> getNewChatUserContent(long chat_id, long user_id, Date created_at) throws Exception {
		try {
			String fieldNames = "*";
			String extensiveSql = " WHERE chat_id = :p1 AND user_id != :p2 AND created_at > :p3 AND enabled = :p4";
			String orderBy = " created_at ";
			// 查询
			List<ChatUserContent> chatUserContentList = chatUserContentDao.getBySql(ChatUserContent.class, fieldNames,
					extensiveSql, new Object[] { chat_id, user_id, created_at, EnabledType.USED }, null, orderBy);
			return chatUserContentList;
		} catch (Exception e) {
			throw e;
		}
	}
}
