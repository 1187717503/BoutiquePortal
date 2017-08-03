package pk.shoplus.service;

import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiLogBatch;
import pk.shoplus.model.ApiLogImageError;
import pk.shoplus.parameter.EnabledType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiLogImageErrorService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ApiLogImageError> apiLogImageErrorEntityDao = null;

	/**
	 * @param conn
	 */
	public ApiLogImageErrorService(Connection conn) {
		apiLogImageErrorEntityDao = new EntityDao<ApiLogImageError>(conn);
	}


	public Long create(ApiLogImageError apiLogImageError) throws Exception{
		apiLogImageError.setCreate_time(new Date());
		apiLogImageError.setEnabled(EnabledType.USED);
		return apiLogImageErrorEntityDao.create(apiLogImageError);
	}
}
