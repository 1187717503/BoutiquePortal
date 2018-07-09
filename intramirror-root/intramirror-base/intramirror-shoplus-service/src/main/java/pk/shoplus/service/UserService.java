package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.User;
import pk.shoplus.model.UserRole;
import pk.shoplus.parameter.EnabledType;

/**
 * 
 * @author YeGGo
 *
 */
public class UserService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<User> userDao = null;
	private EntityDao<UserRole> userRoleDao = null;

	/**
	 * @param conn
	 */
	public UserService(Connection conn) {
		userDao = new EntityDao<User>(conn);
		userRoleDao = new EntityDao<UserRole>(conn);
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public User getUserById(long id) throws Exception {
		try {
			User user = this.userDao.getById(User.class, id);
			return user;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 用户列表获取(附带角色信息)
	 *
	 * @param page
	 * @param orderBy
	 * @return page
	 * @throws Exception
	 */
	public Page getUserListWithRole(String filter, String userIdOrName, String roleId, String createdAt, String orderBy,
			Page page, String period) throws Exception {
		try {
			StringBuilder sb = new StringBuilder();
			if (!Helper.isNullOrEmpty(roleId)) {
				// sb.append(" AND b.role_id = ");
				sb.append(" AND c.role_id = ");
				sb.append(roleId);
			}
			if (!Helper.isNullOrEmpty(createdAt)) {
				sb.append(" AND a.created_at = ");
				sb.append(createdAt);
			}
			if(!Helper.isNullOrEmpty(userIdOrName)){
				sb.append(" AND (a.username LIKE ").append("'%").append(userIdOrName).append("%'");
				sb.append(" OR a.user_id =").append("'"+userIdOrName+"')");
			}
//			if (!Helper.isNullOrEmpty(username)) {
//				sb.append(" AND a.username LIKE ").append("'%").append(username).append("%'");
//			}
//			if (!Helper.isNullOrEmpty(userId)) {
//				sb.append(" AND a.user_id =").append(Long.parseLong(userId));
//			}
			if (!Helper.isNullOrEmpty(filter)) {
				sb.append(" and c.role_id = ");
				sb.append("'"+ filter +"'");
			}
			if (!Helper.isNullOrEmpty(period)) {
				if (Helper.isValidDate(period.split("to")[0].trim())
						&& Helper.isValidDate(period.split("to")[1].trim())) {
					sb.append(" and a.created_at > '" + period.split("to")[0].trim() + "' and a.created_at < '"
							+ period.split("to")[1].trim() + "' ");
				}
			}

			String whereCondition = sb.toString();
			orderBy = Helper.isNullOrEmpty(orderBy) ? "created_at desc" : orderBy;

			// String countPrefix = " SELECT COUNT(*) ";
			String countPrefix = "select count(*) as countRow from (SELECT a.*, GROUP_CONCAT(c.role_name) as role_name "
					+ " FROM user AS a " + " LEFT JOIN user_role AS b ON a.user_id = b.user_id "
					+ "  LEFT JOIN role AS c ON b.role_id = c.role_id" + " WHERE a.enabled = " + EnabledType.USED
					+ whereCondition + " GROUP BY a.user_id ) as t";

			// String pagePrefix = " SELECT a.*, GROUP_CONCAT(c.role_name) as
			// role_name ";
			String pagePrefix = " SELECT a.*, if(c.enabled=1,GROUP_CONCAT(c.role_name),\"\") as role_name,if(c.enabled=1,GROUP_CONCAT(c.role_id),\"\") as role_id ";
			String body = " FROM user AS a " + " LEFT JOIN user_role AS b ON a.user_id = b.user_id "
					+ " LEFT JOIN role AS c ON b.role_id = c.role_id" + " WHERE a.enabled = " + EnabledType.USED
					+ whereCondition + " GROUP BY a." + "user_id" + " ORDER BY a." + orderBy + " LIMIT " + page.pageSize
					+ " OFFSET " + (page.pageNumber - 1) * page.pageSize;

			// List<Map<String, Object>> count =
			// userDao.executeBySql(countPrefix + body, null);
			List<Map<String, Object>> count = userDao.executeBySql(countPrefix, null);
			List<Map<String, Object>> roles = userDao.executeBySql(pagePrefix + body, null);

			// long totalPage = count.size() / page.pageSize;
			long totalPage = (long) count.get(0).get("countRow") / page.pageSize;
			if (totalPage % page.pageSize != 0) {
				totalPage++;
			}
			page.totalPage = totalPage == 0 ? 1 : totalPage;
			page.list = roles;
			page.totalRow = (long) roles.size();

			return page;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据邮箱计算用户数
	 *
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public long countByEmail(String email) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();

			// 添加条件
			conditionMap.put("email", email);
			conditionMap.put("enabled", EnabledType.USED);
			// 返回查询结果
			return this.userDao.countByCondition(User.class, conditionMap);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 插入一条用户记录
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public User createUser(User user) throws Exception {
		Long user_id = this.userDao.create(user);
		if (user_id > 0) {
			user.user_id = user_id;
		} else {
			throw new Exception("Creation failed");
		}
		return user;
	}

	/**
	 * 插入一条用户权限记录
	 *
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public UserRole createUserRole(UserRole userRole) throws Exception {
		try {
			long user_role_id = this.userRoleDao.create(userRole);
			if (user_role_id > 0) {
				userRole.user_role_id = user_role_id;
			} else {
				userRole = null;
			}
			return userRole;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过用户邮箱查找用户
	 *
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public User getUserByEmail(String email) throws Exception {
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			User user = null;
			// 添加条件
			conditionMap.put("email", email);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<User> userList = this.userDao.getByCondition(User.class, conditionMap);

			for (User temp : userList) {
				if (temp != null) {
					user = new User();
					user = temp;
					break;
				}
			}

			return user;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 检查用户的帐号密码是否存在
	 *
	 * @param email
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public User getUserByUsernameAndPassword(String email, String password) throws Exception {
		try {
			// 初始化参数
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			User user = null;

			// 添加条件
			conditionMap.put("email", email);
			conditionMap.put("password", password);
			conditionMap.put("enabled", EnabledType.USED);

			// 查询
			List<User> userList = this.userDao.getByCondition(User.class, conditionMap);

			for (User temp : userList) {
				if (temp != null) {
					user = new User();
					user = temp;
					break;
				}
			}

			return user;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新用户信息
	 *
	 * @param user
	 * @throws Exception
	 */
	public void updateUser(User user) throws Exception {
		try {
			userDao.updateById(user);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 用户信息用于导出excel
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> findUserList() throws Exception {
		try {
			String userListSql = "SELECT a.user_id AS userid,a.username AS username,a.email AS email,a.created_at AS  createdat,GROUP_CONCAT(c.role_name) as rolename    FROM user AS a "
					+ "LEFT JOIN user_role AS b ON a.user_id = b.user_id "
					+ "LEFT JOIN role AS c ON b.role_id = c.role_id " + "WHERE a.enabled = " + EnabledType.USED
					+ " GROUP BY a.user_id ";
			List<Map<String, Object>> users = userDao.executeBySql(userListSql, null);
			return users;
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateUserInfo(User user) {
		try {
			userDao.updateById(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据userid删除user_role中的role
	 * 
	 * @param userId
	 */
	public void updateUserRoleByUserId(Long userId) {
		try {
			String sql = "delete *  from user_role where user_id =:p1 ;";
			Object[] params = { userId };
			userDao.executeBySql(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据userid更新UserRole
	 * 
	 * @param userRole
	 */
	public void updateUserRoleByUserId(UserRole userRole) {
		try {
			userRoleDao.updateById(userRole);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得UserRole
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	/*
	 * public UserRole getUserRoleByUserId(Long userId) throws Exception { try {
	 * UserRole userRole = this.userRoleDao.getById(UserRole.class, userId);
	 * return userRole; } catch (Exception e) { throw e; } }
	 */

	/**
	 * 获得UserRole
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserRole getUserRoleByUserId(Long userId) throws Exception {
		try {
			UserRole userRole = null;

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("user_id", userId);

			List<UserRole> list = userRoleDao.getByCondition(UserRole.class, condition);

			for (UserRole temp : list) {
				if (temp != null) {
					userRole = temp;
					break;
				}
			}
			return userRole;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据有条件删除userrole
	 * 
	 * @param condition
	 */
	public void deleteUserRoleByCondition(HashMap<String, Object> condition) {
		try {
			userRoleDao.deleteByCondition(UserRole.class, condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新用户信息 with value
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void updateUserWithValue(User user, Map<String, Object> condition) throws Exception {
		try {
			userDao.updateByIdWithCondition(user, condition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void updateUserByCondition(User user, Map<String, Object> condition) throws Exception {
		try {
			userDao.updateById(user);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据 user_id 删除 user 表中的 user
	 *
	 * @param userId
	 */
	public void deleteUserByUserId(Long userId) throws Exception {
		userDao.deleteById(User.class, userId);
	}
}
