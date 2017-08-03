package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;


import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Geography;

public class GeographyService {
	
	/**
	 * 获取数据库连接
	 */
	private EntityDao<Geography> geographyDao = null;

	
	/**
	 * @param conn
	 */
	public GeographyService(Connection conn) {
		geographyDao = new EntityDao<Geography>(conn);
	}



	/**
	 * 查询Geography 列表
	 *
	 * @return
	 */
	public List<Map<String, Object>> getGeographyList() {
		String sql = "SELECT * FROM geography ";
		List<Map<String, Object>> list = geographyDao.executeBySql(sql, null);
		return list;
	}

	
}
