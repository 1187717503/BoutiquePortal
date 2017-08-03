package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Chat;
import pk.shoplus.parameter.EnabledType;

public class ChatService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Chat> chatDao = null;

	/**
	 * @param conn
	 */
	public ChatService(Connection conn) {
		chatDao = new EntityDao<Chat>(conn);
	}

	// 根据id 找 chat
	public Chat getChatById(long chat_id) throws Exception {
		try {
			Chat chat = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("chat_id", chat_id);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<Chat> chatList = chatDao.getByCondition(Chat.class, conditionMap);
			for (Chat temp : chatList) {
				if (temp != null) {
					chat = new Chat();
					chat = temp;
					break;
				}
			}
			return chat;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param Chat
	 * @throws Exception
	 */
	public Chat createChat(Chat chat) throws Exception {
		try {
			Long chat_id = chatDao.create(chat);
			if (chat_id > 0) {
				chat.chat_id = chat_id;
			} else {
				chat = null;
			}
			return chat;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据condition map 来获取 chat list
	 * 
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public List<Chat> getChatListByCondition(Map<String, Object> conditionMap) throws Exception {
		try {
			// 查询
			List<Chat> chatList = chatDao.getByCondition(Chat.class, conditionMap);
			return chatList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改chat信息
	 * 
	 * @param product
	 * @throws Exception
	 */
	public void updateChat(Chat chat) throws Exception {
		try {
			chatDao.updateById(chat);
		} catch (Exception e) {
			throw e;
		}
	}
}
