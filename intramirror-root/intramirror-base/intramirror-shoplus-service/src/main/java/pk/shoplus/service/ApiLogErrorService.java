package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiLogError;

public class ApiLogErrorService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ApiLogError> apiLogErrorDao = null;

	/**
	 * @param conn
	 */
	public ApiLogErrorService(Connection conn) {
		apiLogErrorDao = new EntityDao<ApiLogError>(conn);
	}

	/**
	 * @param apiLogError
	 * @return
	 * @throws Exception
	 */
	public ApiLogError createApiLogError(ApiLogError apiLogError) throws Exception {
		try {
			apiLogError.api_log_error_id = apiLogErrorDao.create(apiLogError);
			if (apiLogError.api_log_error_id > 0) {
				return apiLogError;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
