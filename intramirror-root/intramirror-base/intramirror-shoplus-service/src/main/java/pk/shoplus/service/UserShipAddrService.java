package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.UserShipAddress;

public class UserShipAddrService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<UserShipAddress> userShipAddressDao = null;

	/**
	 * @param conn
	 */
	public UserShipAddrService(Connection conn) {
		userShipAddressDao = new EntityDao<UserShipAddress>(conn);
	}

	/**
	 * 通过条件获取categoryList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<UserShipAddress> getUserShipAddrListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<UserShipAddress> userShipAddressList = userShipAddressDao.getByCondition(UserShipAddress.class, fieldName,
					condition);
			return userShipAddressList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param UserShipAddr
	 *            create
	 * @throws Exception
	 */
	public UserShipAddress createUserShipAddr(UserShipAddress userShipAddress) throws Exception {
		try {
			Long id = userShipAddressDao.create(userShipAddress);
			if (id > 0) {
				userShipAddress.user_ship_address_id = id;
			} else {
				userShipAddress = null;
			}
			return userShipAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改UserShipAddr信息
	 * 
	 * @param userShipAddr
	 * @throws Exception
	 */
	public void updateUserShipAddr(UserShipAddress userShipAddress) throws Exception {
		try {
			userShipAddressDao.updateById(userShipAddress);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取UserShipAddr信息
	 * 
	 * @param id
	 * @throws Exception
	 */
	public UserShipAddress getUserShipAddrById(long id) throws Exception {
		try {
			UserShipAddress userShipAddress = userShipAddressDao.getById(UserShipAddress.class, id);
			return userShipAddress;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * delete UserShipAddr信息
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteUserShipAddrById(long id) throws Exception {
		try {
			userShipAddressDao.deleteById(UserShipAddress.class, id);
		} catch (Exception e) {
			throw e;
		}
	}
}
