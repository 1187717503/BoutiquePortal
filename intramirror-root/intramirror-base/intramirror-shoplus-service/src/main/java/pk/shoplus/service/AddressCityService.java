package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.AddressCity;

public class AddressCityService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<AddressCity> addressCityDao = null;

	/**
	 * @param conn
	 */
	public AddressCityService(Connection conn) {
		addressCityDao = new EntityDao<AddressCity>(conn);
	}

	/**
	 * 根据 address_city_id 获取AddressCity
	 * 
	 * @param address_city_id
	 * @return
	 * @throws Exception
	 */
	public AddressCity getAddressCityById(long address_city_id) throws Exception {
		try {
			AddressCity addressCity = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("address_city_id", address_city_id);
			// 查询
			List<AddressCity> addressCityList = addressCityDao.getByCondition(AddressCity.class, conditionMap);
			for (AddressCity temp : addressCityList) {
				if (temp != null) {
					addressCity = new AddressCity();
					addressCity = temp;
					break;
				}
			}
			return addressCity;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据 找 address_city_name 找到 address city
	 * 
	 * @param address_city_name
	 * @return
	 * @throws Exception
	 */
	public AddressCity getAddressCityByName(String address_city_name) throws Exception {
		try {
			AddressCity addressCity = null;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			// 添加条件
			conditionMap.put("name", address_city_name);
			// 查询
			List<AddressCity> addressCityList = addressCityDao.getByCondition(AddressCity.class, conditionMap);
			for (AddressCity temp : addressCityList) {
				if (temp != null) {
					addressCity = new AddressCity();
					addressCity = temp;
					break;
				}
			}
			return addressCity;
		} catch (Exception e) {
			throw e;
		}
	}
}
