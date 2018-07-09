package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.Role;
import pk.shoplus.model.UserRole;
import pk.shoplus.parameter.EnabledType;

public class RoleService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<Role> roleDao = null;
	private EntityDao<UserRole> userRoleDao = null;

	/**
	 * @param conn
	 */
	public RoleService(Connection conn) {
		roleDao = new EntityDao<Role>(conn);
		userRoleDao = new EntityDao<UserRole>(conn);
	}

	/**
	 * 获取角色
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Role getRoleById(long id) throws Exception {
		try {
			// 查询
			Role role = this.roleDao.getById(Role.class, id);
			if (!role.enabled) {
				// throw not enabled exception
				throw new Exception();
			}
			return role;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 角色列表获取(附带用户信息)
	 *
	 * @param page
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Page getRoleListWithUser(Page page, String orderBy) throws Exception {
		if (orderBy == null) {
			orderBy = "created_at desc";
		}
		try {
			orderBy = Helper.isNullOrEmpty(orderBy) ? "role_id" : orderBy;
			StringBuilder pageSql = new StringBuilder("");

			pageSql.append(
					"SELECT a.role_id, a.role_name, COUNT(*) AS quantity, a.remark, a.created_at,a.privilege,a.enabled")
					.append(" FROM role AS a ").append(" LEFT JOIN user_role AS b ON a.role_id = b.role_id ")
					.append(" WHERE a.enabled=").append(EnabledType.USED).append(" and a.can_show= ").append(true)
					.append(" GROUP BY a.role_id ").append(" ORDER BY a.").append(orderBy).append(" LIMIT ")
					.append(page.pageSize).append(" OFFSET ").append((page.pageNumber - 1) * page.pageSize);

			String countSql = " LEFT JOIN user_role ON role.role_id = user_role.role_id" + " WHERE role.enabled="
					+ EnabledType.USED + " ORDER BY role." + orderBy;

			long count = roleDao.countBySql(Role.class, countSql, null);

			List<Map<String, Object>> roles = roleDao.executeBySql(pageSql.toString(), null);

			long totalPage = count / page.pageSize;
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
	 * Get select role list
	 *
	 * @return
	 * @throws Exception
	 */
	public List<Role> getSelectRoleList() throws Exception {
		return roleDao.getBySql(Role.class, "role_id, role_name", " WHERE enabled = true", null);
	}

	/**
	 * 更新角色信息
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public void updateRoleById(Role role) throws Exception {
		try {
			this.roleDao.updateById(role);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建角色信息
	 * 
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public Role createRole(Role role) throws Exception {
		long roleId = this.roleDao.create(role);
		if (roleId > 0) {
			role.role_id = roleId;
		} else {
			throw new Exception("Creation failed");
		}
		return role;
	}

	/**
	 * 根据roleId删除数据
	 * 
	 * @param roleId
	 * @throws Exception
	 */
	public void deleteRoleById(long roleId) throws Exception {
		try {
			this.roleDao.deleteById(Role.class, roleId);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询role列表用于文件导出
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> findRoleList() throws Exception {
		try {
			StringBuilder roleLsitMapsSql = new StringBuilder("");
			roleLsitMapsSql
					.append("SELECT a.role_id, a.role_name, COUNT(*) AS quantity, a.remark, a.created_at,a.privilege,a.enabled")
					.append(" FROM role AS a ").append(" LEFT JOIN user_role AS b ON a.role_id = b.role_id ")
					.append("WHERE a.enabled=").append(EnabledType.USED).append(" GROUP BY a.role_id ");
			List<Map<String, Object>> roles = roleDao.executeBySql(roleLsitMapsSql.toString(), null);
			return roles;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param privilege
	 * @return
	 * @throws Exception
	 */
	public Role findRoleByPrivilege(long privilege) throws Exception {
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("privilege", privilege);
			condition.put("enabled", EnabledType.USED);
			List<Role> roles = roleDao.getByCondition(Role.class, condition);
			if (roles.size() > 0) {
				return roles.get(0);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param userRole
	 * @throws Exception
	 */
	public UserRole createUserRole(UserRole userRole) throws Exception {
		long userRoleId = this.userRoleDao.create(userRole);
		if (userRoleId > 0) {
			userRole.user_role_id = userRoleId;
		} else {
			throw new Exception("Creation failed");
		}
		return userRole;
	}

	/**
	 * Delete user_role from table user_role by condition
	 *
	 * @param condition
	 * @throws Exception
	 */
	public void deleteUserRoleByCondition(HashMap<String, Object> condition) throws Exception {
		this.userRoleDao.deleteByCondition(UserRole.class, condition);
	}
}
